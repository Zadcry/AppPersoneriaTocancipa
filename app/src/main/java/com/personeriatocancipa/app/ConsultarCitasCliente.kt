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

class ConsultarCitasCliente : AppCompatActivity() {

    // Declaración de variables para elementos de la interfaz y Firebase
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de citas
    private lateinit var citasList: MutableList<Cita> // Lista de citas
    private lateinit var adapter: CitaAdapterCliente // Adaptador para la lista de citas
    private lateinit var databaseReference: DatabaseReference // Referencia a la base de datos de Firebase
    private lateinit var auth: FirebaseAuth // Instancia de autenticación de Firebase
    private lateinit var btnSalir: Button // Botón para salir de la actividad
    private lateinit var spinnerAnioFiltro: Spinner // Spinner para filtrar por año
    private lateinit var spinnerMesFiltro: Spinner // Spinner para filtrar por mes

    // Variables auxiliares para gestionar los filtros de mes y año
    private var mesesList: MutableList<String> = mutableListOf() // Lista de meses disponibles
    private var aniosList: MutableList<String> = mutableListOf() // Lista de años disponibles
    private var mesesPorAnio: MutableMap<String, MutableList<String>> = mutableMapOf() // Meses por año

    // Función que se ejecuta al crear la actividad
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar_citas_cliente)

        // Inicialización de variables
        recyclerView = findViewById(R.id.recyclerViewCitaCliente)
        btnSalir = findViewById(R.id.btnSalir)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spinnerAnioFiltro = findViewById(R.id.spinnerAnioFiltro)
        spinnerMesFiltro = findViewById(R.id.spinnerMesFiltro)

        // Inicialización de listas y adaptador
        citasList = mutableListOf()
        adapter = CitaAdapterCliente(citasList)
        recyclerView.adapter = adapter

        // Inicialización de Firebase Authentication y la referencia a la base de datos
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("citas")

        cargarCitas() // Cargar las citas del cliente

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
                // Obtener el año seleccionado y actualizar los meses disponibles
                val anioSeleccionado = parentView.getItemAtPosition(position)?.toString() ?: ""
                actualizarMesesPorAnio(anioSeleccionado) // Actualizar los meses disponibles
                // Filtrar las citas por el mes y año seleccionados
                filtrarCitasPorMesYAnio(spinnerMesFiltro.selectedItem?.toString() ?: "", anioSeleccionado)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Listener para el filtro de mes
        spinnerMesFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // Obtener el mes seleccionado y filtrar las citas por mes y año
                val mesSeleccionado = parentView.getItemAtPosition(position)?.toString() ?: ""
                // Filtrar las citas por el mes y año seleccionados
                filtrarCitasPorMesYAnio(mesSeleccionado, spinnerAnioFiltro.selectedItem?.toString() ?: "")
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Configura el botón para salir de la actividad
        btnSalir.setOnClickListener {
            finish() // Finaliza la actividad actual
        }
    }

    // Función para cargar las citas del cliente autenticado
    private fun cargarCitas() {
        val userEmail = auth.currentUser?.email ?: "" // Obtener el correo del usuario autenticado
        Log.d("CorreoUsuario", "Correo autenticado: $userEmail") // Imprimir el correo del usuario autenticado

        // Consultar las citas del cliente autenticado
        databaseReference.orderByChild("correoCliente").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Limpiar las listas de citas, meses y años
                    citasList.clear() // Limpiar la lista de citas
                    mesesList.clear() // Limpiar la lista de meses
                    aniosList.clear() // Limpiar la lista de años
                    mesesPorAnio.clear() // Limpiar el mapa de meses por año

                    for (citaSnapshot in snapshot.children) {
                        val cita = citaSnapshot.getValue(Cita::class.java) // Obtener la cita actual y convertirla a objeto Cita
                        if (cita != null && !cita.fecha.isNullOrEmpty()) { // Verificar que la cita y la fecha no sean nulas o vacías
                            val mes = obtenerMesDeFecha(cita.fecha!!) // Obtener el mes de la fecha de la cita
                            val anio = obtenerAnioDeFecha(cita.fecha!!) // Obtener el año de la fecha de la cita

                            // Agregar meses y años únicos a las listas
                            if (!aniosList.contains(anio)) {
                                aniosList.add(anio) // Agregar el año a la lista de años si no está presente
                            }

                            if (!mesesPorAnio.containsKey(anio)) {
                                mesesPorAnio[anio] = mutableListOf() // Inicializar la lista de meses para el año actual si no existe
                            }

                            if (!mesesPorAnio[anio]!!.contains(mes)) {
                                mesesPorAnio[anio]!!.add(mes) // Agregar el mes a la lista de meses para el año actual si no está presente
                            }

                            citasList.add(cita) // Agregar la cita a la lista de citas
                        } else {
                            Log.d("ConsultarCitas", "Cita sin fecha válida: ${citaSnapshot.key}")
                        }
                    }

                    // Ordenar las listas
                    aniosList.sort() // Ordenar la lista de años
                    mesesList.sort() // Ordenar la lista de meses

                    // Actualizar adaptadores de Spinner
                    (spinnerAnioFiltro.adapter as ArrayAdapter<String>).notifyDataSetChanged() // Notificar al adaptador de años que los datos cambiaron
                    actualizarMesesPorAnio(aniosList.firstOrNull() ?: "") // Actualizar los meses disponibles
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores al cargar las citas
                    Log.e("ConsultarCitas", "Error al cargar citas: ${error.message}") // Imprimir el mensaje de error
                    Toast.makeText(this@ConsultarCitasCliente, "Error al cargar citas", Toast.LENGTH_SHORT).show() // Mostrar mensaje de error en pantalla
                }
            })
    }

    // Función para actualizar los meses disponibles en el Spinner de Mes
    private fun actualizarMesesPorAnio(anio: String) {
        mesesList.clear() // Limpiar la lista de meses
        if (mesesPorAnio.containsKey(anio)) { // Verificar si el año tiene meses disponibles
            mesesList.addAll(mesesPorAnio[anio]!!) // Agregar los meses disponibles al listado
            mesesList.sort() // Ordenar la lista de meses
        }

        // Notificar al adaptador de meses que los datos cambiaron
        (spinnerMesFiltro.adapter as ArrayAdapter<String>).notifyDataSetChanged()

        // Asegurarse de que haya elementos en mesesList antes de llamar a setSelection
        if (mesesList.isNotEmpty()) {
            spinnerMesFiltro.setSelection(0) // Seleccionar el primer mes disponible
        } else {
            Log.d("ActualizarMeses", "La lista de meses está vacía para el año $anio") // Imprimir mensaje de error
        }
    }

    // Función para obtener el nombre del mes a partir de una fecha en formato "YYYY-MM-DD"
    private fun obtenerMesDeFecha(fecha: String): String {
        val parts = fecha.split("-") // Separar la fecha en partes usando el caracter "-"
        return if (parts.size >= 2) {
            val mesIndex = parts[1].toIntOrNull() ?: 0 // Obtener el índice del mes
            val calendar = Calendar.getInstance() // Obtener una instancia de Calendar
            calendar.set(Calendar.MONTH, mesIndex - 1) // Establecer el mes en el calendario
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "" // Obtener el nombre del mes
        } else ""
    }

    // Función para obtener el año a partir de una fecha en formato "YYYY-MM-DD"
    private fun obtenerAnioDeFecha(fecha: String): String {
        val parts = fecha.split("-") // Separar la fecha en partes usando el caracter "-"
        return if (parts.size >= 3) parts[2] else "" // Obtener el año de la fecha
    }

    // Función para filtrar las citas por mes y año
    private fun filtrarCitasPorMesYAnio(mes: String, anio: String) {
        val citasFiltradas = citasList.filter { cita -> // Filtrar las citas por mes y año
            val mesCita = obtenerMesDeFecha(cita.fecha ?: "") // Obtener el mes de la fecha de la cita
            val anioCita = obtenerAnioDeFecha(cita.fecha ?: "") // Obtener el año de la fecha de la cita
            (mes.isEmpty() || mesCita == mes) && (anio.isEmpty() || anioCita == anio) // Verificar si la cita cumple con los filtros
        }
        adapter.actualizarCitas(citasFiltradas) // Actualizar la lista de citas en el adaptador
    }
}
