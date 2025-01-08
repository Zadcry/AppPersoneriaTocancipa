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

// Clase para consultar las citas de un abogado
class ConsultarCitasAbogado : AppCompatActivity() {

    // Declaración de variables
    private lateinit var recyclerView: RecyclerView // RecyclerView que muestra la lista de citas
    private lateinit var citasList: MutableList<Cita> // Lista mutable de citas
    private lateinit var adapter: CitaAdapterAbogado // Adaptador para la lista de citas
    private lateinit var databaseReference: DatabaseReference // Referencia a la base de datos
    private lateinit var auth: FirebaseAuth // Autenticación de Firebase
    private lateinit var btnSalir: Button // Botón para salir de la actividad
    private lateinit var spinnerAnioFiltro: Spinner // Spinner para filtrar por año
    private lateinit var spinnerMesFiltro: Spinner // Spinner para filtrar por mes

    // Variables auxiliares para gestionar los filtros
    private var mesesList: MutableList<String> = mutableListOf() // Lista de meses disponibles
    private var aniosList: MutableList<String> = mutableListOf() // Lista de años disponibles
    private var mesesPorAnio: MutableMap<String, MutableList<String>> = mutableMapOf() // Mapa de meses por año

    // Variables para guardar el estado actual de los filtros
    private var mesSeleccionado: String = "" // Mes seleccionado
    private var anioSeleccionado: String = "" // Año seleccionado

    // Método que se ejecuta al crear la actividad
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar_citas_abogado)

        // Inicialización de variables
        recyclerView = findViewById(R.id.recyclerViewCitaAbogado)
        btnSalir = findViewById(R.id.btnSalir)
        spinnerAnioFiltro = findViewById(R.id.spinnerAnioFiltro)
        spinnerMesFiltro = findViewById(R.id.spinnerMesFiltro)

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialización de la lista de citas y el adaptador
        citasList = mutableListOf()
        adapter = CitaAdapterAbogado(citasList)
        recyclerView.adapter = adapter

        // Inicialización de la autenticación y la base de datos
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("citas")

        // Cargar las citas de la base de datos
        cargarCitas()

        // Configuración del Spinner de Mes
        val mesAdapter = ArrayAdapter(this, R.drawable.spinner_item, mesesList)
        mesAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
        spinnerMesFiltro.adapter = mesAdapter

        // Configuración del Spinner de Año
        val anioAdapter = ArrayAdapter(this, R.drawable.spinner_item, aniosList)
        anioAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
        spinnerAnioFiltro.adapter = anioAdapter


        // Listener para manejar cambios en el filtro de año
        spinnerAnioFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                anioSeleccionado = parentView.getItemAtPosition(position)?.toString() ?: "" // Guardar el año seleccionado
                actualizarMesesPorAnio(anioSeleccionado) // Actualizar los meses disponibles
                filtrarCitasPorMesYAnio(spinnerMesFiltro.selectedItem?.toString() ?: "", anioSeleccionado) // Aplicar el filtro
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Listener para manejar cambios en el filtro de mes
        spinnerMesFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Guardar el mes seleccionado y aplicar el filtro
                mesSeleccionado = parentView.getItemAtPosition(position)?.toString() ?: ""
                // Aplicar el filtro
                filtrarCitasPorMesYAnio(mesSeleccionado, anioSeleccionado)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Configura el botón para salir de la actividad
        btnSalir.setOnClickListener {
            finish() // Finaliza la actividad
        }
    }

    // Método para cargar las citas desde la base de datos Firebase
    private fun cargarCitas() {
        val userEmail = auth.currentUser?.email ?: "" // Obtener el correo del usuario autenticado
        Log.d("CorreoAbogado", "Correo autenticado: $userEmail")

        databaseReference.orderByChild("correoAbogado").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Guardar el estado de los filtros antes de recargar las citas
                    val mesGuardado = mesSeleccionado // Guardar el mes seleccionado
                    val anioGuardado = anioSeleccionado // Guardar el año seleccionado

                    citasList.clear() // Limpiar la lista de citas
                    mesesList.clear() // Limpiar la lista de meses
                    aniosList.clear() // Limpiar la lista de años
                    mesesPorAnio.clear() // Limpiar el mapa de meses por año

                    for (citaSnapshot in snapshot.children) {
                        val cita = citaSnapshot.getValue(Cita::class.java) // Convierte el snapshot en un objeto Cita
                        if (cita != null && !cita.fecha.isNullOrEmpty()) { // Verifica que la cita no sea nula y tenga fecha
                            val mes = obtenerMesDeFecha(cita.fecha!!) // Obtener el mes de la fecha
                            val anio = obtenerAnioDeFecha(cita.fecha!!) // Obtener el año de la fecha

                            if (!aniosList.contains(anio)) { // Agregar el año a la lista si no está
                                aniosList.add(anio)
                            }

                            if (!mesesPorAnio.containsKey(anio)) { // Agregar el año al mapa si no está
                                mesesPorAnio[anio] = mutableListOf()
                            }

                            if (!mesesPorAnio[anio]!!.contains(mes)) { // Agregar el mes al mapa si no está
                                mesesPorAnio[anio]!!.add(mes)
                            }

                            citasList.add(cita) // Agregar la cita a la lista
                        }
                    }

                    aniosList.sort() // Ordenar la lista de años
                    mesesList.sort() // Ordenar la lista de meses

                    // Actualizar los adaptadores de los Spinners sin cambiar los filtros
                    (spinnerAnioFiltro.adapter as ArrayAdapter<String>).notifyDataSetChanged() // Actualizar el Spinner de Año
                    actualizarMesesPorAnio(anioGuardado) // Actualizar el Spinner de Mes

                    // Restaurar los filtros después de recargar los datos
                    spinnerAnioFiltro.setSelection(aniosList.indexOf(anioGuardado)) // Seleccionar el año guardado
                    spinnerMesFiltro.setSelection(mesesList.indexOf(mesGuardado)) // Seleccionar el mes guardado

                    // Aplicar el filtro de citas según el estado guardado
                    filtrarCitasPorMesYAnio(mesGuardado, anioGuardado)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ConsultarCitas", "Error al cargar citas: ${error.message}") // Mostrar mensaje de error en la consola
                    Toast.makeText(this@ConsultarCitasAbogado, "Error al cargar citas", Toast.LENGTH_SHORT).show() // Mostrar mensaje de error en pantalla
                }
            })
    }

    // Método para actualizar los meses disponibles según el año seleccionado
    private fun actualizarMesesPorAnio(anio: String) {
        mesesList.clear() // Limpiar la lista de meses
        if (mesesPorAnio.containsKey(anio)) { // Verificar si el año está en el mapa
            mesesList.addAll(mesesPorAnio[anio]!!) // Agregar los meses disponibles
            mesesList.sort() // Ordenar la lista de meses
        }

        (spinnerMesFiltro.adapter as ArrayAdapter<String>).notifyDataSetChanged() // Actualizar el Spinner de Mes

        if (mesesList.isNotEmpty()) { // Verificar si hay meses disponibles
            spinnerMesFiltro.setSelection(0) // Selecciona el primer mes disponible
        }
    }

    // Método para obtener el nombre del mes a partir de una fecha en formato "YYYY-MM-DD"
    private fun obtenerMesDeFecha(fecha: String): String {
        val parts = fecha.split("-") // Separar la fecha en partes
        return if (parts.size >= 2) {
            val mesIndex = parts[1].toIntOrNull() ?: 0 // Obtener el índice del mes
            val calendar = Calendar.getInstance() // Crear una instancia de Calendar
            calendar.set(Calendar.MONTH, mesIndex - 1) // Establecer el mes en el calendario
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "" // Obtener el nombre del mes
        } else ""
    }

    // Método para obtener el año a partir de una fecha en formato "YYYY-MM-DD"
    private fun obtenerAnioDeFecha(fecha: String): String {
        val parts = fecha.split("-") // Separar la fecha en partes
        return if (parts.size >= 3) parts[2] else "" // Obtener el año si está presente
    }

    // Método para filtrar las citas por mes y año
    private fun filtrarCitasPorMesYAnio(mes: String, anio: String) {
        val citasFiltradas = citasList.filter { cita ->
            val mesCita = obtenerMesDeFecha(cita.fecha ?: "") // Obtener el mes de la cita
            val anioCita = obtenerAnioDeFecha(cita.fecha ?: "") // Obtener el año de la cita
            (mes.isEmpty() || mesCita == mes) && (anio.isEmpty() || anioCita == anio) // Verificar si la cita cumple con los filtros
        }
        adapter.actualizarCitas(citasFiltradas) // Actualizar la lista de citas en el adaptador
    }
}
