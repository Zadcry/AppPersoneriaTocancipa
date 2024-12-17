package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar
import java.util.Locale

class ConsultarCitasAbogado : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var citasList: MutableList<Cita>
    private lateinit var adapter: CitaAdapterAbogado
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var btnSalir: Button
    private lateinit var spinnerAnioFiltro: Spinner
    private lateinit var spinnerMesFiltro: Spinner

    private var mesesList: MutableList<String> = mutableListOf()
    private var aniosList: MutableList<String> = mutableListOf()
    private var mesesPorAnio: MutableMap<String, MutableList<String>> = mutableMapOf()

    // Variables para guardar el estado actual de los filtros
    private var mesSeleccionado: String = ""
    private var anioSeleccionado: String = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar_citas_abogado)

        recyclerView = findViewById(R.id.recyclerViewCitaAbogado)
        btnSalir = findViewById(R.id.btnSalir)
        spinnerAnioFiltro = findViewById(R.id.spinnerAnioFiltro)
        spinnerMesFiltro = findViewById(R.id.spinnerMesFiltro)

        recyclerView.layoutManager = LinearLayoutManager(this)

        citasList = mutableListOf()
        adapter = CitaAdapterAbogado(citasList)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("citas")

        cargarCitas()

        // Configuración del Spinner de Mes
        val mesAdapter = ArrayAdapter(this, R.drawable.spinner_item, mesesList)
        mesAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
        spinnerMesFiltro.adapter = mesAdapter

        // Configuración del Spinner de Año
        val anioAdapter = ArrayAdapter(this, R.drawable.spinner_item, aniosList)
        anioAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
        spinnerAnioFiltro.adapter = anioAdapter


        // Listener para el filtro de año
        spinnerAnioFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                anioSeleccionado = parentView.getItemAtPosition(position)?.toString() ?: ""
                actualizarMesesPorAnio(anioSeleccionado)
                filtrarCitasPorMesYAnio(spinnerMesFiltro.selectedItem?.toString() ?: "", anioSeleccionado)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Listener para el filtro de mes
        spinnerMesFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                mesSeleccionado = parentView.getItemAtPosition(position)?.toString() ?: ""
                filtrarCitasPorMesYAnio(mesSeleccionado, anioSeleccionado)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        btnSalir.setOnClickListener {
            finish()
        }
    }

    private fun cargarCitas() {
        val userEmail = auth.currentUser?.email ?: ""
        Log.d("CorreoAbogado", "Correo autenticado: $userEmail")

        databaseReference.orderByChild("correoAbogado").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Guardar el estado de los filtros antes de recargar las citas
                    val mesGuardado = mesSeleccionado
                    val anioGuardado = anioSeleccionado

                    citasList.clear()
                    mesesList.clear()
                    aniosList.clear()
                    mesesPorAnio.clear()

                    for (citaSnapshot in snapshot.children) {
                        val cita = citaSnapshot.getValue(Cita::class.java)
                        if (cita != null && !cita.fecha.isNullOrEmpty()) {
                            val mes = obtenerMesDeFecha(cita.fecha!!)
                            val anio = obtenerAnioDeFecha(cita.fecha!!)

                            if (!aniosList.contains(anio)) {
                                aniosList.add(anio)
                            }

                            if (!mesesPorAnio.containsKey(anio)) {
                                mesesPorAnio[anio] = mutableListOf()
                            }

                            if (!mesesPorAnio[anio]!!.contains(mes)) {
                                mesesPorAnio[anio]!!.add(mes)
                            }

                            citasList.add(cita)
                        }
                    }

                    aniosList.sort()
                    mesesList.sort()

                    // Actualizar los adaptadores de los Spinners sin cambiar los filtros
                    (spinnerAnioFiltro.adapter as ArrayAdapter<String>).notifyDataSetChanged()
                    actualizarMesesPorAnio(anioGuardado)

                    // Restaurar los filtros después de recargar los datos
                    spinnerAnioFiltro.setSelection(aniosList.indexOf(anioGuardado))
                    spinnerMesFiltro.setSelection(mesesList.indexOf(mesGuardado))

                    // Aplicar el filtro de citas según el estado guardado
                    filtrarCitasPorMesYAnio(mesGuardado, anioGuardado)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ConsultarCitas", "Error al cargar citas: ${error.message}")
                    Toast.makeText(this@ConsultarCitasAbogado, "Error al cargar citas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun actualizarMesesPorAnio(anio: String) {
        mesesList.clear()
        if (mesesPorAnio.containsKey(anio)) {
            mesesList.addAll(mesesPorAnio[anio]!!)
            mesesList.sort()
        }

        (spinnerMesFiltro.adapter as ArrayAdapter<String>).notifyDataSetChanged()

        if (mesesList.isNotEmpty()) {
            spinnerMesFiltro.setSelection(0) // Selecciona el primer mes disponible
        }
    }

    private fun obtenerMesDeFecha(fecha: String): String {
        val parts = fecha.split("-")
        return if (parts.size >= 2) {
            val mesIndex = parts[1].toIntOrNull() ?: 0
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, mesIndex - 1)
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
        } else ""
    }

    private fun obtenerAnioDeFecha(fecha: String): String {
        val parts = fecha.split("-")
        return if (parts.size >= 3) parts[2] else ""
    }

    private fun filtrarCitasPorMesYAnio(mes: String, anio: String) {
        val citasFiltradas = citasList.filter { cita ->
            val mesCita = obtenerMesDeFecha(cita.fecha ?: "")
            val anioCita = obtenerAnioDeFecha(cita.fecha ?: "")
            (mes.isEmpty() || mesCita == mes) && (anio.isEmpty() || anioCita == anio)
        }
        adapter.actualizarCitas(citasFiltradas)
    }
}
