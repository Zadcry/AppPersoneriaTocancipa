package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Properties
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class CrearCita : AppCompatActivity() {

    private var appointmentID = 1 // ID consecutivo para las citas

    // Variables Globales que representan los elementos de la vista
    private lateinit var gridSeleccionarAbogado: GridLayout
    private lateinit var txtConsultar: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var txtFecha: TextView
    private lateinit var txtDocumento: TextView
    private lateinit var txtAnuncio: TextView
    private lateinit var txtDia: TextView
    private lateinit var spAbogado: Spinner
    private lateinit var spTema: Spinner
    private lateinit var spHora: Spinner
    private lateinit var spAutorizaCorreo: Spinner
    private lateinit var spCorreoVigente: Spinner
    private lateinit var spTipoDocumento: Spinner
    private lateinit var btnSeleccionar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnConsultarID: Button
    private lateinit var btnFecha: Button
    private lateinit var gridConsultar: LinearLayout
    private lateinit var mDbRef: DatabaseReference
    private lateinit var tvDocumento: TextView
    private lateinit var tvAnexos: TextView
    private lateinit var tvAutorizaCorreo: TextView
    private lateinit var tvCorreoVigente: TextView
    private lateinit var tvTipoDocumento: TextView
    private lateinit var tarea: String
    private lateinit var sujeto: String
    private lateinit var nombreCliente: String
    private lateinit var correoAbogado: String
    private lateinit var correoCliente: String
    private lateinit var cedulaCliente: String
    private lateinit var tema: String
    private lateinit var descripcion: String
    private lateinit var calendar: Calendar
    private lateinit var correosAdicionales: List<String>

    // Variables globales generales para la creación de la cita
    private var abogado: String = ""
    private var autorizaCorreo: String = ""
    private var correoVigente: String = ""
    private var autorizaCorreoConsulta: String = ""
    private var correoVigenteConsulta: String = ""
    private var fechaAntigua: String = ""
    private var modificacionExitosa: Boolean = false

    // Variables Modificacion Cita
    private var modCitaID = ""
    private var modCitaFecha = ""
    private var modCitaAbogado = ""

    // Listas de abogados activos y sus horarios
    private lateinit var abogadosActivos: List<String>
    private var abogadosPorTema = mutableMapOf<String, List<String>>()
    private var horariosAbogados = mutableMapOf<String, Map<String, Pair<String, String>>>()

    // Duración de las citas por abogado
    private val duracionCitas = mapOf(
        "Edwin Yovanni Franco Bahamón" to 60,
        "Emilio Alexander Mejía Ángulo" to 60,
        "Fransy Yanet Mambuscay López" to 60,
        "José Francisco Alfonso Rojas" to 60,
        "Jose Omar Chaves Bautista" to 60,
        "Kewin Paul Pardo Cortés" to 60,
        "Liliana Zambrano" to 60,
        "Nydia Yurani Suárez Moscoso" to 60,
        "Oscar Mauricio Díaz Muñoz" to 60,
        "Santiago Garzón" to 30
    )

    private val horaAlmuerzo = Pair("12:00", "12:59")

    // Variables para la selección de fecha y hora
    private var seleccionFecha=Calendar.getInstance()
    private var seleccionHora=""

    // Instancia de Firebase
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cita)

        nombreCliente = ""

        // Buscar los elementos de layout
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtFecha = findViewById(R.id.txtFecha)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtDia = findViewById(R.id.txtDia)
        tvDocumento = findViewById(R.id.tvDocumento)
        tvAnexos = findViewById(R.id.tvAnexos)
        tvAutorizaCorreo = findViewById(R.id.tvAutorizaCorreo)
        tvCorreoVigente = findViewById(R.id.tvCorreoVigente)
        tvTipoDocumento = findViewById(R.id.tvTipoDocumento)
        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnSalir = findViewById(R.id.btnSalir)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnConsultarID = findViewById(R.id.btnConsultarID)
        btnFecha = findViewById(R.id.btnFecha)
        gridConsultar = findViewById(R.id.gridConsultar)
        gridSeleccionarAbogado = findViewById(R.id.gridSeleccionarAbogado)

        // Obtener el ID más alto de citas en Firebase al iniciar
        obtenerUltimoID()

        // Inicializar abogados por tema para evitar valores null
        resources.getStringArray(R.array.opcionesTema).forEach { tema ->
            // Convertir en una lista mutable vacia
            abogadosPorTema[tema] = mutableListOf()
        }

        // Mapear abogados activos
        abogadosActivos = buscarAbogadosActivos()

        // Mapear opciones de abogados según tema
        val temaAbogadoMap = abogadosPorTema

        // Ingresar opciones de tema en el Spinner
        spTema = findViewById(R.id.spTema)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTema,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTema.adapter = adapter
        }

        // Ingresar opciones de hora en el Spinner
        spHora = findViewById(R.id.spHora)
        ArrayAdapter.createFromResource(
            this,
            R.array.horas,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spHora.adapter = adapter
        }

        // Ingresar opciones de autorización de correo en el Spinner
        spAutorizaCorreo = findViewById(R.id.spAutorizaCorreo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesAutorizacion,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spAutorizaCorreo.adapter = adapter
        }

        // Ingresar opciones de correo vigente en el Spinner
        spCorreoVigente = findViewById(R.id.spCorreoVigente)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesAutorizacion,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spCorreoVigente.adapter = adapter
        }

        // Ingresar opciones de tipo de documento en el Spinner
        spTipoDocumento = findViewById(R.id.spTipoDocumento)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTipoDocumento,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTipoDocumento.adapter = adapter
        }

        // Configurar las opciones de abogado según el tema seleccionado
        spAbogado = findViewById(R.id.spAbogado)
        spTema.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTema = spTema.selectedItem.toString()
                val abogados = temaAbogadoMap[selectedTema] ?: emptyList()

                // En caso de tener ya seleccionado un dia, cambiar el horario al cambiar el abogado
                if (txtDia.text.isNotEmpty()) {
                    cambiarHorarioSegunAbogado()
                }
                gridSeleccionarAbogado.visibility = View.VISIBLE

                // Configurar el adaptador del Spinner de abogados
                val abogadoAdapter = ArrayAdapter(
                    this@CrearCita,
                    R.drawable.spinner_item,
                    abogados
                )
                abogadoAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
                spAbogado.adapter = abogadoAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita acción
            }
        }

        // Listener para cambiar el horario cuando se seleccione un abogado
        spAbogado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (txtDia.text.isNotEmpty()) {
                    cambiarHorarioSegunAbogado()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita acción
            }
        }

        // Listener para actualizar el valor de la variable global de seleccionHora
        spHora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionHora = spHora.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita acción
            }
        }

        // Obtener los valores desde el Intent
        tarea = intent.getStringExtra("tarea").toString()
        sujeto = intent.getStringExtra("sujeto").toString()

        // Configurar acciones en función de la tarea
        // Dependiendo de la tarea, se habilitan o se deshabilitan los campos
        // Tambien se ajusta la visibilidad de los elementos
        when (tarea) {
            "crear" -> {
                habilitarCampos(true)
                txtAnuncio.text = "Agendar Cita"
                if(sujeto == "cliente"){
                    // Preguntar si se autoriza el envío de correos
                    var builder = AlertDialog.Builder(this)
                    builder.setTitle("Envío de confirmación a Correo Electrónico")
                    builder.setMessage("¿Está de acuerdo con que se le envíe respuesta o información sobre la gestión de su PETICIÓN vía correo electrónico?")
                    // Agregar botones de acción
                    builder.setPositiveButton("Sí") { dialog, which ->
                        autorizaCorreo = "Sí"
                    }

                    builder.setNegativeButton("No") { dialog, which ->
                        // Cancelar el flujo
                        autorizaCorreo = "No"
                    }

                    builder.create().show()

                    // Preguntar si el correo es vigente

                    builder = AlertDialog.Builder(this)
                    builder.setTitle("Vigencia de correo electrónico")
                    builder.setMessage("Certifico que el correo electrónico ingresado se encuentra vigente, y se autoriza a la Personería de Tocancipá para que realice notificaciones electrónicas a través de este medio de las actuaciones realizadas por la entidad, en los términos del artículo 56 de la Ley 1437 de 2011, y las normas que la modifiquen, aclaren o sustituyan")
                    // Agregar botones de acción
                    builder.setPositiveButton("Sí") { dialog, which ->
                        correoVigente = "Sí"
                    }

                    builder.setNegativeButton("No") { dialog, which ->
                        // Cancelar el flujo
                        correoVigente = "No"
                    }

                    builder.create().show()

                    // Un cliente la crea para sí mismo
                    btnSeleccionar.setOnClickListener{
                        scheduleAppointment(sujeto,"crear")
                    }
                    txtDocumento.visibility = EditText.GONE
                    tvDocumento.visibility = TextView.GONE
                    tvAnexos.visibility = TextView.VISIBLE
                    tvTipoDocumento.visibility = TextView.GONE
                    spTipoDocumento.visibility = Spinner.GONE
                }
                else{

                    // Un admin. la crea para un cliente
                    btnSeleccionar.setOnClickListener{
                        scheduleAppointment(sujeto,"crear")
                    }
                    tvAutorizaCorreo.visibility = TextView.VISIBLE
                    tvCorreoVigente.visibility = TextView.VISIBLE
                    spAutorizaCorreo.visibility = Spinner.VISIBLE
                    spCorreoVigente.visibility = Spinner.VISIBLE
                }
                btnFecha.visibility = Button.VISIBLE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                gridConsultar.visibility = GridView.GONE
            }
            "consultar" -> {
                habilitarCampos(false)
                txtAnuncio.text = "Consultar Cita"
                btnFecha.visibility = Button.GONE
                gridConsultar.visibility = GridView.VISIBLE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                btnSeleccionar.visibility = Button.GONE
                btnConsultarID.visibility = Button.VISIBLE
                tvAutorizaCorreo.visibility = TextView.VISIBLE
                tvCorreoVigente.visibility = TextView.VISIBLE
                spAutorizaCorreo.visibility = Spinner.VISIBLE
                spCorreoVigente.visibility = Spinner.VISIBLE
            }
            "modificar" -> {
                habilitarCampos(true)
                txtAnuncio.text = "Modificar Cita"
                btnFecha.visibility = Button.VISIBLE
                gridConsultar.visibility = GridView.VISIBLE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                btnSeleccionar.visibility = Button.GONE
                btnConsultarID.visibility = Button.VISIBLE
            }
            "eliminar" -> {
                habilitarCampos(false)
                txtAnuncio.text = "Eliminar Cita"
                btnFecha.visibility = Button.GONE
                gridConsultar.visibility = GridView.VISIBLE
                btnModificar.visibility = Button.GONE
                btnSeleccionar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                btnConsultarID.visibility = Button.VISIBLE
            }
        }

        btnConsultarID.setOnClickListener {
            consultarPorID()
        }

        btnFecha.setOnClickListener {
            // Ajustar la instancia global del calendario
            calendar = Calendar.getInstance()
            seleccionarFecha()
        }

        btnSalir.setOnClickListener{
            finish()
        }

        btnEliminar.setOnClickListener(){
            eliminarCita()
        }

        btnModificar.setOnClickListener(){
            modificarCita()
        }
    }

    // Función para buscar los horarios de los abogados activos en
    // Firebase y almacenarlos en la variable global de mapa mutable
    private fun conseguirHorariosAbogados(){
        val ref = FirebaseDatabase.getInstance().getReference("horarioAbogados")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        val abogado = childSnapshot.key.toString()
                        val horarios = mutableMapOf<String, Pair<String, String>>()
                        if (abogadosActivos.contains(abogado)) {
                            childSnapshot.children.forEach { diaSnapshot ->
                                // Extraer los horarios de cada día
                                // Y ajustarlos como un par de horas
                                val dia = diaSnapshot.key.toString()
                                val horaInicio = diaSnapshot.child("inicio").value.toString()
                                val horaFin = diaSnapshot.child("fin").value.toString()
                                horarios[dia] = Pair(horaInicio, horaFin)
                            }
                            // Almacenar el horario en la variable global
                            horariosAbogados[abogado] = horarios
                        }
                    }
                    println(horariosAbogados)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearCita, "Error al obtener los horarios de los abogados", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para buscar en Firebase los abogados activos
    // y almacenarlos en la variable de lista global
    private fun buscarAbogadosActivos(): List<String> {
        val abogadosActivos = mutableListOf<String>()
        val ref = FirebaseDatabase.getInstance().getReference("abogadoData")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("ResourceType")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        val estado = childSnapshot.child("estado").value.toString()
                        if (estado == "Activo") {
                            // SI el abogado encontrado está activo, añadirlo a la lista
                            val nombreAbogado = childSnapshot.child("nombreCompleto").value.toString()
                            abogadosActivos.add(nombreAbogado)

                            // Añadir a la lista de abogados por tema
                            val tema = childSnapshot.child("tema").value.toString()
                            agregarAbogadoPorTema(tema, nombreAbogado)
                        }
                    }

                    // Poner el primer tema en el Spinner (Evita errores)
                    spTema.setSelection(0)
                    // Agregar al Spinner de Abogados los abogados del primer tema
                    val abogadosPrimerTema = abogadosPorTema[spTema.selectedItem.toString()] ?: emptyList()
                    val abogadoAdapter = ArrayAdapter(
                        this@CrearCita,
                        R.drawable.spinner_item,
                        abogadosPrimerTema
                    )
                    abogadoAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
                    spAbogado.adapter = abogadoAdapter

                    // Inicializar horarios de abogados usando los abogados activos
                    abogadosActivos.forEach { abogado ->
                        val horarios = horariosAbogados[abogado] ?: emptyMap()
                        horariosAbogados[abogado] = horarios
                    }
                    // Agregar Horarios de Abogados
                    conseguirHorariosAbogados()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearCita, "Error al buscar abogados activos", Toast.LENGTH_SHORT).show()
            }
        })
        return abogadosActivos
    }

    // Función que agrega un abogado a la lista mutable global de abogados por tema
    // Se da por hecho que el abogado está activo
    private fun agregarAbogadoPorTema(tema: String, abogado: String) {
        if(tema=="Todo excepto Victimas"){
            // Incluir al abogado al map de abogados por tema en todos los temas excepto Víctimas
            for (temaF in resources.getStringArray(R.array.opcionesTema).filter { it != "Victimas" }) {
                if (abogadosPorTema.containsKey(temaF)) {
                    val abogados = abogadosPorTema[temaF] as MutableList<String>
                    abogados.add(abogado)
                    abogadosPorTema[temaF] = abogados
                } else {
                    abogadosPorTema[temaF] = mutableListOf(abogado)
                }
            }
        }
        else{
            // Buscar el tema en el mapa y añadir el abogado a dicho tema
            if (abogadosPorTema.containsKey(tema)) {
                val abogados = abogadosPorTema[tema] as MutableList<String>
                abogados.add(abogado)
                abogadosPorTema[tema] = abogados
            } else {
                abogadosPorTema[tema] = mutableListOf(abogado)
            }
        }
        println(abogadosPorTema)
    }

    private fun habilitarCampos(habilitar:Boolean){
        txtDescripcion.isEnabled = habilitar
        txtDocumento.isEnabled = habilitar
        spTema.isEnabled = habilitar
        spAbogado.isEnabled = habilitar
        spHora.isEnabled = habilitar
        btnFecha.isEnabled = habilitar
        btnSeleccionar.isEnabled = habilitar
        spAutorizaCorreo.isEnabled = habilitar
        spCorreoVigente.isEnabled = habilitar
        spTipoDocumento.isEnabled = habilitar
    }

    // Función principal para modificar cita una vez se pulse el boton de modificar
    private fun modificarCita(){
        val idCita = txtConsultar.text.toString().toIntOrNull()

        // Verificar que el ID de la cita sea válido
        if (idCita == null) {
            Toast.makeText(this, "ID de cita inválido", Toast.LENGTH_SHORT).show()
            return
        }

        appointmentID = idCita // Asegurar que appointmentID tiene el ID actual

        val ref = FirebaseDatabase.getInstance().getReference("citas")
        // Buscar cita y si existe, modificarla
        ref.child(idCita.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //  Utiliza la funcion para ajustar el nombre del abogado
                    conseguirNombreAbogado(snapshot.child("correoAbogado").value.toString(), "modificar")
                    // Intenta la reprogramacion de la cita con los nuevos parametros
                    scheduleAppointment("admin","modificar")
                } else {
                    Toast.makeText(this@CrearCita, "No se encontró la cita con ID $idCita", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearCita, "Error al modificar la cita", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función corta para que el usuario ajuste la fecha en
    // el cuadro de dialogo. Selecciona la fecha y la muestra
    // en el TextView de la fecha.
    private fun seleccionarFecha(){
        DatePickerDialog(this, { _, year, month, day ->

            val fechaSeleccionada = Calendar.getInstance().apply { set(year, month, day) }
            val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
            val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
            val diaNombre = dias[diaSemana - 1]
            seleccionFecha = fechaSeleccionada
            txtDia.text = "$diaNombre, $day/${month + 1}/$year"

            // Actualizar el horario con el abogado seleccionado
            cambiarHorarioSegunAbogado()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Función para actualizar el horario en el Spinner de horas
    // según el abogado seleccionado y el día seleccionado
    @SuppressLint("ResourceType")
    private fun cambiarHorarioSegunAbogado(){
        val abogadoSeleccionado = spAbogado.selectedItem.toString()
        val diaSeleccionado = txtDia.text.split(",")[0]
        val horarioAbogado = horariosAbogados[abogadoSeleccionado]?.get(diaSeleccionado)
        if (horarioAbogado != null) {
            // Si el abogado tiene horario para el día seleccionado
            // Conseguir las horas disponibles y ponerlas en el Spinner
            val (horaInicio, horaFin) = horarioAbogado
            val horas = mutableListOf<String>()
            horas.add("Seleccionar hora")
            var hora = horaInicio
            val duracionCita = duracionCitas[abogadoSeleccionado] ?: 60
            while (hora <= horaFin) {
                horas.add(hora)
                if(horarioAbogado.second != ""){
                    hora = calcularHoraFin(hora.split(":")[0].toInt(), hora.split(":")[1].toInt(), duracionCita)
                }else{
                    Toast.makeText(this, "El abogado no tiene horario disponible para el día seleccionado", Toast.LENGTH_SHORT).show()
                    seleccionFecha = Calendar.getInstance()
                    txtDia.text = ""
                    break
                }
                // Filtrar Hora Almuerzo
                if ((hora in horaAlmuerzo.first..horaAlmuerzo.second) && (horarioAbogado.second != "")) {
                    hora = calcularHoraFin(hora.split(":")[0].toInt(), hora.split(":")[1].toInt(), duracionCita)
                }
            }
            val horaAdapter = ArrayAdapter(this, R.drawable.spinner_item, horas)
            horaAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spHora.adapter = horaAdapter
        } else {
            // Si el abogado no tiene horario para el día seleccionado
            // Dejar el Spinner de horas vacío con una opcion por defecto
            val horaAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.horas,
                R.drawable.spinner_item
            )
            horaAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spHora.adapter = horaAdapter
        }
    }

    // Función principal para eliminar una cita al presionar el boton de eliminar
    private fun eliminarCita(){
        val idCita = txtConsultar.text.toString().toIntOrNull()
        if (idCita == null) {
            Toast.makeText(this, "ID de cita inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("citas")
        // Buscar cita y si existe, eliminarla
        ref.child(idCita.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Conseguir el nombre del abogado de la cita
                    // Y con ello, eliminar el horario ocupado de esa cita.
                    conseguirNombreAbogado(snapshot.child("correoAbogado").value.toString(), "eliminar")
                    abogado = spAbogado.selectedItem.toString()
                    eliminarHorarioOcupado(idCita.toString(),abogado, snapshot.child("fecha").value.toString())

                    // Eliminar en Firebase la cita
                    snapshot.ref.removeValue()
                    Toast.makeText(this@CrearCita, "Cita eliminada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CrearCita, "No se encontró la cita con ID $idCita", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearCita, "Error al eliminar la cita", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función que elimina el horario ocupado de Firebase
    private fun eliminarHorarioOcupado(idCita: String, abogado: String, fecha: String){
        println("Eliminando horario ocupado")
        val ref = FirebaseDatabase.getInstance().getReference("horariosOcupados/$abogado/$fecha/$idCita")
        ref.removeValue()
    }

    // Función principal para consultar una cita por ID
    private fun consultarPorID() {
        val idCita = txtConsultar.text.toString().toIntOrNull()
        if (idCita == null) {
            Toast.makeText(this, "Ingrese ID a consultar", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("citas")
        ref.child(idCita.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si la cita existe, extraer los datos y mostrarlos en la vista
                    val descripcionC = snapshot.child("descripcion").value.toString()
                    val fechaC = snapshot.child("fecha").value.toString()
                    fechaAntigua = fechaC
                    val horaC = snapshot.child("hora").value.toString()
                    val abogadoC = snapshot.child("correoAbogado").value.toString()
                    val clienteC = snapshot.child("correoCliente").value.toString()
                    val temaC = snapshot.child("tema").value.toString()
                    val autorizaCorreoC = snapshot.child("autorizaCorreo").value.toString()
                    val correoVigenteC = snapshot.child("correoVigente").value.toString()
                    autorizaCorreoConsulta = autorizaCorreoC
                    correoVigenteConsulta = correoVigenteC
                    txtConsultar.setText(snapshot.key.toString())
                    txtDescripcion.setText(descripcionC)
                    txtFecha.text = "Fecha: $fechaC, Hora: $horaC"
                    spTema.setSelection((spTema.adapter as ArrayAdapter<String>).getPosition(temaC))

                    // Ajustar el formato de la fecha para mostrar el día de la semana
                    val fecha = fechaC.split("-")
                    val year = fecha[2].toInt()
                    val month = fecha[1].toInt()
                    val day = fecha[0].toInt()
                    val fechaSeleccionada = Calendar.getInstance().apply { set(year, month - 1, day) }
                    val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
                    val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
                    val diaNombre = dias[diaSemana - 1]
                    txtDia.text = "$diaNombre, $day/$month/$year"

                    // Actualizar el horario para poder ponerlo en el spinner
                    cambiarHorarioSegunAbogado()
                    spHora.setSelection((spHora.adapter as ArrayAdapter<String>).getPosition(horaC))
                    // Ajustar los spinners de autorización de correo
                    spAutorizaCorreo.setSelection((spAutorizaCorreo.adapter as ArrayAdapter<String>).getPosition(autorizaCorreoC))
                    spCorreoVigente.setSelection((spCorreoVigente.adapter as ArrayAdapter<String>).getPosition(correoVigenteC))

                    // Ajustar variables globales para la modificacion de cita
                    modCitaID = idCita.toString()
                    modCitaFecha = fechaC

                    // Conseguir el nombre del abogado y el documento del cliente
                    conseguirNombreAbogado(abogadoC, "modificar")
                    conseguirCedulaCliente(clienteC)

                    // Mostrar botones de accion una vez la consulta finaliza
                    if (tarea == "modificar") {
                        btnModificar.visibility = Button.VISIBLE
                    } else if (tarea == "eliminar") {
                        btnEliminar.visibility = Button.VISIBLE
                    }

                } else {
                    Toast.makeText(this@CrearCita, "No se encontró la cita con ID $idCita", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearCita, "Error al consultar la cita", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para conseguir el nombre de un abogado usando su correo
    // Varia su utilidad dependiendo del modo en que se llame
    private fun conseguirNombreAbogado(correoAbogado: String, modo:String){
        val ref = FirebaseDatabase.getInstance().getReference("abogadoData")
        ref.orderByChild("correo").equalTo(correoAbogado).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        // Extraer el nombre de Firebase
                        val nombreAbogado = childSnapshot.child("nombreCompleto").value.toString()
                        if (modo=="modificar"){
                            // Si se usa en modificar, ajustar el spinner de abogado
                            spAbogado.setSelection((spAbogado.adapter as ArrayAdapter<String>).getPosition(nombreAbogado))
                            modCitaAbogado = nombreAbogado
                        }
                        else if (modo=="eliminar"){
                            // Si se usa en eliminar, ajustar la variable global de abogado
                            abogado=nombreAbogado
                        }
                    }
                } else {
                    // Si no se encontró el nombre en los datos
                    Toast.makeText(this@CrearCita, "No se encontró el abogado $abogado", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error en la consulta: ${error.message}")
            }
        })
    }

    // Función para conseguir la cédula de un cliente usando su correo
    private fun conseguirCedulaCliente(correoCliente: String){
        val ref = FirebaseDatabase.getInstance().getReference("userData")
        ref.orderByChild("correo").equalTo(correoCliente).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        // Extraer la cedula de Firebase y mostrarla en el TextView
                        var cedulaCliente = childSnapshot.child("documento").value.toString()
                        txtDocumento.setText(cedulaCliente)
                    }
                } else {
                    // Si no se encontró el nombre en los datos
                    Toast.makeText(this@CrearCita, "No se encontró la cédula $cedulaCliente", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error en la consulta: ${error.message}")
            }
        })
    }

    // Función para calcular el ultimo ID de las citas en Firebase
    private fun obtenerUltimoID() {
        val ref = FirebaseDatabase.getInstance().getReference("citas")
        ref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val maxID = snapshot.children.first().key?.toIntOrNull() ?: 0
                    appointmentID = maxID + 1
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearCita, "Error al obtener el último ID", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para enviar un correo electrónico a cada destinatario seleccionado como correoAdicional
    private fun enviarCorreoAdicionales(
        subject: String,
        body: String,
        correosAdicionales: List<String>
    ) {
        correosAdicionales.forEach { correo ->
            sendEmailInBackground(correo, subject, body)
        }
    }

    // Función principal para programar una cita
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun scheduleAppointment(sujeto: String, modo:String) {
        calendar = Calendar.getInstance()
        tema = spTema.selectedItem.toString()
        descripcion = txtDescripcion.text.toString()
        correosAdicionales = listOf("personeriatocancipacol@gmail.com")
        if(spTema.selectedItem.toString() != "Víctimas"){
            abogado = spAbogado.selectedItem.toString()
        }

        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")

        var query = mDbRef.orderByChild("nombreCompleto").equalTo(abogado)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verificar si existe algún dato que coincida con el nombre
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        // Extraer el correo
                        correoAbogado = childSnapshot.child("correo").value.toString()
                    }
                } else {
                    // Si no se encontró el nombre en los datos
                    Toast.makeText(this@CrearCita, "No se encontró el abogado $abogado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error en la consulta: ${error.message}")
            }
        })

        if(sujeto == "cliente"){
            // Obtener el correo del cliente (Asumiendo que es el usuario actual)
            val currentUser = auth.currentUser
            if (currentUser != null) {
                correoCliente = currentUser.email.toString()

                // Obtener nombre del usuario teniendo su correo
                mDbRef = FirebaseDatabase.getInstance().getReference("userData")

                query = mDbRef.orderByChild("correo").equalTo(correoCliente)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Verificar si existe algún dato que coincida con el nombre
                        if (snapshot.exists()) {
                            snapshot.children.forEach { childSnapshot ->
                                nombreCliente = childSnapshot.child("nombreCompleto").value.toString()
                                println(nombreCliente)
                                if (nombreCliente.isNotEmpty()) {
                                    println("Nombre Cliente asignado: $nombreCliente")
                                    finalizarCreacion(modo)
                                }else{
                                    println("El nombre del cliente está vacío.")
                                }
                            }
                        } else {
                            // Si no se encontró el nombre en los datos
                            Toast.makeText(this@CrearCita, "No se encontró el correo  $correoCliente", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Error en la consulta: ${error.message}")
                    }
                })
            } else {
                println("No hay un usuario autenticado.")
                Toast.makeText(this, "No hay un usuario autenticado", Toast.LENGTH_SHORT).show()
            }
        }else{
            // Si el sujeto es un administrador
            cedulaCliente = txtDocumento.text.toString()
            val tipoDocumento = spTipoDocumento.selectedItem.toString()

            if(cedulaCliente.isEmpty()){
                Toast.makeText(this, "Debe ingresar un número de documento", Toast.LENGTH_SHORT).show()
                return
            }

            // Obtener correo del usuario teniendo su número y tipo de documento
            mDbRef = FirebaseDatabase.getInstance().getReference("userData")
            query = mDbRef.orderByChild("tipoDocumento").equalTo(tipoDocumento)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Verificar si existe algún dato que coincida con el tipo de documento
                    if (snapshot.exists()) {
                        query = mDbRef.orderByChild("documento").equalTo(cedulaCliente)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                // Verificar si existe algún dato que coincida con el número de documento
                                if (snapshot.exists()) {
                                    snapshot.children.forEach { childSnapshot ->
                                        // Extraer el correo
                                        correoCliente = childSnapshot.child("correo").value.toString()
                                        nombreCliente = childSnapshot.child("nombreCompleto").value.toString()
                                        println(nombreCliente)
                                        if (correoCliente.isNotEmpty()) {
                                            println("Correo Cliente asignado: $correoCliente")
                                            eliminarHorarioOcupado(appointmentID.toString(),abogado, fechaAntigua)
                                            finalizarCreacion(modo)
                                        }else{
                                            Toast.makeText(this@CrearCita, "El correo del cliente no está disponible.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    // Si no se encontró el nombre en los datos
                                    Toast.makeText(this@CrearCita, "No se encontró el número de documento $cedulaCliente", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                println("Error en la consulta: ${error.message}")
                            }

                        })
                    } else {
                        // Si no se encontró el nombre en los datos
                        Toast.makeText(this@CrearCita, "No se encontró ese tipo de documento", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error en la consulta: ${error.message}")
                }
            })
        }
    }

    // Función que contiene la lógica para finalizar la creación de una cita
    // Contiene las validaciones finales y la creación de la cita en Firebase
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun finalizarCreacion(modo: String) {
        // Validación: Seleccionar Fecha
        if (txtDia.text.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación: Seleccionar Hora
        if (seleccionHora == "Seleccionar hora") {
            Toast.makeText(this, "Debe seleccionar una hora", Toast.LENGTH_SHORT).show()
            return
        }

        var year = seleccionFecha.get(Calendar.YEAR)
        var month = seleccionFecha.get(Calendar.MONTH)
        var day = seleccionFecha.get(Calendar.DAY_OF_MONTH)
        val fechaSeleccionada = seleccionFecha
        val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
        val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        val diaNombre = dias[diaSemana - 1]
        val horarioAbogado = horariosAbogados[abogado]?.get(diaNombre)

        if (txtDia.text.isNotEmpty()) {
            val partesFecha = txtDia.text.split(", ")[1].split("/")
            seleccionFecha.set(
                partesFecha[2].toInt(), // Año
                partesFecha[1].toInt() - 1, // Mes (0 basado)
                partesFecha[0].toInt() // Día
            )
            year = seleccionFecha.get(Calendar.YEAR)
            month = seleccionFecha.get(Calendar.MONTH)
            day = seleccionFecha.get(Calendar.DAY_OF_MONTH)
        }

        val fechaActual = Calendar.getInstance()
        fechaActual.set(Calendar.HOUR_OF_DAY, 0)
        fechaActual.set(Calendar.MINUTE, 0)
        fechaActual.set(Calendar.SECOND, 0)
        fechaActual.set(Calendar.MILLISECOND, 0)

        // Validación: Fecha Pasada
        if (fechaSeleccionada.before(fechaActual)) {
            Toast.makeText(this, "No se puede agendar una cita en una fecha pasada.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación: Una semana de antelación
        fechaActual.add(Calendar.DAY_OF_YEAR, 7) // Sumar una semana
        if (fechaSeleccionada.before(fechaActual)) {
            Toast.makeText(this, "Debe agendarse con al menos una semana de antelación.", Toast.LENGTH_SHORT).show()
        }else{
            // Validación: Descripción no vacía
            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Debe ingresar una descripción", Toast.LENGTH_SHORT).show()
                return
            }
            // Validación: Una cita por semana
            verificarCitasPorSemana(correoCliente, fechaSeleccionada) { puedeAgendar ->
                if (!puedeAgendar) {
                    Toast.makeText(this, "Solo puede agendar una cita por semana.", Toast.LENGTH_SHORT).show()
                    return@verificarCitasPorSemana
                }else{
                    if(horarioAbogado != null){
                        val (horaInicio, horaFin) = horarioAbogado
                        val horaSeleccionada = seleccionHora
                        val hourOfDay = horaSeleccionada.split(":")[0].toInt()
                        val minute = horaSeleccionada.split(":")[1].toInt()
                        // Validación: Hora seleccionada no disponible
                        if(horaSeleccionada !in horaInicio..horaFin || horaSeleccionada in horaAlmuerzo.first..horaAlmuerzo.second){
                            Toast.makeText(this, "Hora seleccionada no disponible", Toast.LENGTH_SHORT).show()
                        }else{
                            val duracion = duracionCitas[abogado] ?: 60 // Duración predeterminada de 60 minutos
                            val fecha = "$day-${month + 1}-$year"
                            val horaFinCita = calcularHoraFin(hourOfDay, minute, duracion)

                            // Se valida la disponibilidad de la hora seleccionada como validacion final
                            verificarDisponibilidad(abogado, fecha, horaSeleccionada, horaFinCita) { disponible ->
                                if (disponible) {
                                    // Logica de creacion de cita en Firebase
                                    // En este punto, la cita es válida y se puede crear

                                    // Si el modo es crear, se obtiene el último ID
                                    if(modo == "crear"){
                                        obtenerUltimoID()
                                        if((spAutorizaCorreo.visibility == Spinner.VISIBLE) && (spCorreoVigente.visibility == Spinner.VISIBLE)){
                                            autorizaCorreo = spAutorizaCorreo.selectedItem.toString()
                                            correoVigente = spCorreoVigente.selectedItem.toString()
                                        }
                                    }
                                    // Si el modo es modificar, se obtiene el ID de la cita a modificar y
                                    // se elimina el horario ocupado de la cita para reemplazarlo con uno nuevo
                                    else if(modo == "modificar"){
                                        eliminarHorarioOcupado(modCitaID,modCitaAbogado, modCitaFecha)
                                        autorizaCorreo = autorizaCorreoConsulta
                                        correoVigente = correoVigenteConsulta
                                        Toast.makeText(this, "Autoriza correo: $autorizaCorreo, Correo vigente: $correoVigente", Toast.LENGTH_SHORT).show()
                                        appointmentID = txtConsultar.text.toString().toInt()
                                    }else{
                                        // Si el modo es eliminar, se obtiene el ID de la cita a eliminar
                                        autorizaCorreo = autorizaCorreoConsulta
                                        correoVigente = correoVigenteConsulta
                                        Toast.makeText(this, "Autoriza correo: $autorizaCorreo, Correo vigente: $correoVigente", Toast.LENGTH_SHORT).show()
                                        appointmentID = txtConsultar.text.toString().toInt()
                                    }

                                    // Crear instancia de un objeto Cita
                                    var cita = Cita(
                                        appointmentID,
                                        descripcion,
                                        fecha,
                                        horaSeleccionada,
                                        correoAbogado,
                                        correoCliente,
                                        tema,
                                        autorizaCorreo,
                                        correoVigente,
                                        "Pendiente"
                                    )

                                    // Enviar el correo con la información de la cita
                                    val subject = "Cita en Personería - ID: ${cita.id}"
                                    var body=""

                                    if((modo == "crear")){
                                        body = "Estimado Usuario:\n\nSu cita ha sido asignada exitosamente.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nAbogado: ${abogado}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."
                                    }else if (modo == "modificar"){
                                        body = "Estimado Usuario:\n\nSu cita ha sido modificada exitosamente.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nAbogado: ${abogado}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."
                                    }

                                    if(autorizaCorreo == "Sí"){
                                        sendEmailInBackground(correoCliente, subject, body)
                                    }

                                    cita = Cita(
                                        appointmentID,
                                        descripcion,
                                        fecha,
                                        horaSeleccionada,
                                        correoAbogado,
                                        correoCliente,
                                        tema,
                                        autorizaCorreo,
                                        correoVigente,
                                        "Pendiente"
                                    )

                                    val bodyAdicionales = """
                                    Información de la cita:
                                    
                                    ID: ${cita.id}
                                    Fecha: $fecha
                                    Hora: $horaSeleccionada
                                    Cliente: $nombreCliente
                                    Abogado: $abogado
                                    Descripción: $descripcion
                                """.trimIndent()
                                    enviarCorreoAdicionales(subject, bodyAdicionales, correosAdicionales)

                                    if (modo == "crear"){
                                        body = "Estimado Abogado:\n\nTiene una nueva cita.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nUsuario: ${nombreCliente}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."
                                    }else if (modo == "modificar"){
                                        body = "Estimado Abogado:\n\nSe ha modificado una de sus citas.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nUsuario: ${nombreCliente}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."
                                    }
                                    sendEmailInBackground(correoAbogado, subject, body)

                                    // Mostrar detalles en la pantalla
                                    txtFecha.text = "Cita agendada para $fecha, $horaSeleccionada con ID: ${cita.id}"
                                    modificacionExitosa = true
                                    saveAppointmentToFirebase(cita, abogado, fecha, horaSeleccionada, horaFinCita, autorizaCorreo, correoVigente)
                                } else {
                                    Toast.makeText(this, "La hora seleccionada ya está ocupada.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(this, "Día no disponible", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Función para verificar si el cliente tiene una cita en la semana seleccionada
    private fun verificarCitasPorSemana(correoCliente: String, fechaSeleccionada: Calendar, callback: (Boolean) -> Unit) {
       // Obtener el inicio y fin de la semana
        val inicioSemana = fechaSeleccionada.clone() as Calendar
        inicioSemana.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val finSemana = fechaSeleccionada.clone() as Calendar
        finSemana.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)

        // Verificar si el cliente tiene una cita en la semana seleccionada
        val citasRef = FirebaseDatabase.getInstance().getReference("citas")
        citasRef.orderByChild("correoCliente").equalTo(correoCliente)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (citaSnapshot in snapshot.children) {
                        val fechaCitaStr = citaSnapshot.child("fecha").value.toString()
                        val fechaCita = Calendar.getInstance().apply {
                            val parts = fechaCitaStr.split("-")
                            set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                        }
                        if (!fechaCita.before(inicioSemana) && !fechaCita.after(finSemana)) {
                            // Si la cita está dentro de la semana, no se puede agendar y retorna falso
                            callback(false)
                            return
                        }
                    }
                    // Si no hay citas en la semana, se puede agendar y retorna verdadero
                    callback(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    // Función para calcular la hora de finalizacion de una cita
    @SuppressLint("DefaultLocale")
    private fun calcularHoraFin(hour: Int, minute: Int, duracion: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.add(Calendar.MINUTE, duracion)
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    // Función para verificar la disponibilidad de una hora en un día
    private fun verificarDisponibilidad(
        abogado: String,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        callback: (Boolean) -> Unit
    ) {
        // Verificar si la hora seleccionada está disponible en los horarios ocupados de Firebase
        val ref = FirebaseDatabase.getInstance().getReference("horariosOcupados/$abogado/$fecha")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (horario in snapshot.children) {
                        // Excluir la cita actual si el modo es "modificar"
                        if (tarea == "modificar" && horario.key == appointmentID.toString()) {
                            continue
                        }

                        val horaOcupadaInicio = horario.child("horaInicio").value.toString()
                        val horaOcupadaFin = horario.child("horaFin").value.toString()

                        // Verificar si las horas se solapan
                        if (!(horaFin <= horaOcupadaInicio || horaInicio >= horaOcupadaFin)) {
                            callback(false)
                            return
                        }
                    }
                }
                callback(true)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    // Función para guardar una cita en Firebase
    private fun saveAppointmentToFirebase(
        cita: Cita,
        abogado: String,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        autorizaCorreo: String,
        correoVigente: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val citasRef = database.getReference("citas")
        val horariosRef = database.getReference("horariosOcupados/$abogado/$fecha")

        if(tarea == "modificar" && modificacionExitosa){
            // Eliminar el horario ocupado anterior
            horariosRef.child(cita.id.toString()).removeValue()
                .addOnSuccessListener {
                    println("Referencia a eliminar: ${horariosRef.child(cita.id.toString())}")
                    println("Horario ocupado eliminado exitosamente")
                }
                .addOnFailureListener {
                    println("Error al eliminar el horario ocupado: ${it.message}")
                    Toast.makeText(this, "Error al modificar la cita.", Toast.LENGTH_SHORT).show()
                }
        }

        // Guardar la cita y el nuevo horario ocupado
        val citaData = mapOf(
            "id" to cita.id,
            "descripcion" to cita.descripcion,
            "fecha" to cita.fecha,
            "hora" to cita.hora,
            "correoAbogado" to cita.correoAbogado,
            "correoCliente" to cita.correoCliente,
            "tema" to cita.tema,
            "estado" to cita.estado,
            "autorizaCorreo" to autorizaCorreo,
            "correoVigente" to correoVigente
        )

        val horarioData = mapOf(
            "horaInicio" to horaInicio,
            "horaFin" to horaFin
        )

        citasRef.child(cita.id.toString()).setValue(citaData)
            .addOnSuccessListener {
                if(((tarea == "modificar") && (modificacionExitosa)) || (tarea == "crear")){
                    horariosRef.child(cita.id.toString()).setValue(horarioData)
                        .addOnSuccessListener {
                            // Mostrar mensaje de éxito
                            if(tarea == "modificar"){
                                Toast.makeText(this, "Cita modificada exitosamente", Toast.LENGTH_SHORT).show()
                            }else if(tarea == "crear"){
                                Toast.makeText(this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            println("Error al guardar el horario ocupado: ${it.message}")
                            Toast.makeText(this, "Error al guardar el horario ocupado", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                println("Error al guardar la cita: ${it.message}")
                // Restaurar el horario eliminado si la cita falla al guardarse
                horariosRef.child(cita.id.toString()).setValue(horarioData)
                    .addOnSuccessListener {
                        println("Horario restaurado debido a fallo en la modificación de la cita.")
                    }
                    .addOnFailureListener {
                        println("Error al restaurar el horario: ${it.message}")
                    }
                Toast.makeText(this, "Error al modificar la cita.", Toast.LENGTH_SHORT).show()
            }
    }


    // Función que activa el envío de un correo electrónico en segundo plano
    private fun sendEmailInBackground(recipientEmail: String, subject: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendEmail(recipientEmail, subject, body)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Función asincrona para enviar un correo electrónico usando JavaMail
    private suspend fun sendEmail(recipientEmail: String, subject: String, body: String) {
        withContext(Dispatchers.IO) {
            val props = Properties()
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.host"] = "smtp.gmail.com" // Cambia según tu proveedor de email
            props["mail.smtp.port"] = "587"
            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                    return javax.mail.PasswordAuthentication(
                        "personeriatocancipacol@gmail.com",
                        "goyk bode tksv mcmx"
                    )
                }
            })
            // https://support.google.com/mail/answer/185833?hl=es-419 -documentacion
            // es importante para crear la key de enviar
            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress("personeriatocancipacol@gmail.com"))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                message.subject = subject
                message.setText(body)

                Transport.send(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}