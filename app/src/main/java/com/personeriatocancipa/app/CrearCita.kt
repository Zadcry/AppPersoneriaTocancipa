package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar
import java.util.Properties
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class CrearCita : AppCompatActivity() {

    private var appointmentID = 1 // ID consecutivo para las citas

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
    private lateinit var btnHorarios: Button
    private lateinit var btnSeleccionar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnConsultarID: Button
    private lateinit var btnFecha: Button
    private lateinit var gridConsultar: LinearLayout
    private lateinit var mDbRef: DatabaseReference
    private lateinit var tvDocumento: TextView
    private lateinit var tarea: String
    private lateinit var abogado: String
    private lateinit var sujeto: String
    private lateinit var nombreCliente: String
    private lateinit var correoAbogado: String
    private lateinit var correoCliente: String
    private lateinit var cedulaCliente: String
    private lateinit var tema: String
    private lateinit var descripcion: String
    private lateinit var calendar: Calendar
    private lateinit var correosAdicionales: List<String>

    private val horariosAbogados = mapOf(
        "Edwin Yovanni Franco Bahamón" to mapOf(
            "Lunes" to Pair("07:00", "15:00"),
            "Martes" to Pair("07:00", "15:00"),
            "Miércoles" to Pair("07:00", "15:00"),
            "Jueves" to Pair("07:00", "15:00"),
            "Viernes" to Pair("07:00", "14:00")
        ),
        "Emilio Alexander Mejía Ángulo" to mapOf(
            "Jueves" to Pair("09:00", "15:00")
        ),
        "Fransy Yanet Mambuscay López" to mapOf(
            "Lunes" to Pair("07:00", "15:00"),
            "Martes" to Pair("13:00", "15:00"),
            "Miércoles" to Pair("07:00", "15:00"),
            "Jueves" to Pair("13:00", "15:00"),
            "Viernes" to Pair("07:00", "14:00")
        ),
        "José Francisco Alfonso Rojas" to mapOf(
            "Jueves" to Pair("09:00", "15:00")
        ),
        "Jose Omar Chaves Bautista" to mapOf(
            "Lunes" to Pair("09:00", "15:00"),
            "Martes" to Pair("09:00", "15:00"),
            "Miércoles" to Pair("09:00", "15:00")
        ),
        "Kewin Paul Pardo Cortés" to mapOf(
            "Lunes" to Pair("07:00", "12:00"),
            "Martes" to Pair("07:00", "12:00"),
            "Miércoles" to Pair("07:00", "10:00"),
            "Jueves" to Pair("07:00", "12:00"),
            "Viernes" to Pair("07:00", "10:00")
        ),
        "Liliana Zambrano" to mapOf(
            "Miércoles" to Pair("08:00", "10:00")
        ),
        "Nydia Yurani Suárez Moscoso" to mapOf(
            "Lunes" to Pair("07:00", "15:00"),
            "Martes" to Pair("07:00", "15:00"),
            "Miércoles" to Pair("07:00", "15:00"),
            "Jueves" to Pair("07:00", "15:00"),
            "Viernes" to Pair("07:00", "14:00")
        ),
        "Oscar Mauricio Díaz Muñoz" to mapOf(
            "Lunes" to Pair("07:00", "15:00"),
            "Martes" to Pair("13:00", "15:00"),
            "Miércoles" to Pair("07:00", "15:00"),
            "Jueves" to Pair("13:00", "15:00"),
            "Viernes" to Pair("07:00", "10:00")
        ),
        "Santiago Garzón" to mapOf(
            "Miércoles" to Pair("08:00", "11:00")
        )
    )

    private val duracionCitas = mapOf(
        "Edwin Yovanni Franco Bahamón" to 60, // Duración en minutos
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

    private var seleccionFecha=Calendar.getInstance()
    private var seleccionHora=""

    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cita)

        nombreCliente = ""

        // Buscar elementos de layout
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtFecha = findViewById(R.id.txtFecha)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtDia = findViewById(R.id.txtDia)
        tvDocumento = findViewById(R.id.tvDocumento)
        btnHorarios = findViewById(R.id.btnHorarios)
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

        // Mapear opciones de abogados según tema
        val temaAbogadoMap = mapOf(
            "Víctimas" to listOf("Edwin Yovanni Franco Bahamón"),
            "Servicios Públicos" to listOf("Emilio Alexander Mejía Ángulo",
                "Fransy Yanet Mambuscay López", "José Francisco Alfonso Rojas",
                "Jose Omar Chaves Bautista", "Kewin Paul Pardo Cortés",
                "Oscar Mauricio Díaz Muñoz"),
            "Administrativo" to listOf("Emilio Alexander Mejía Ángulo",
                "Fransy Yanet Mambuscay López", "José Francisco Alfonso Rojas",
                "Kewin Paul Pardo Cortés", "Liliana Zambrano",
                "Oscar Mauricio Díaz Muñoz"),
            "Menores y Familia" to listOf("Emilio Alexander Mejía Ángulo",
                "Fransy Yanet Mambuscay López", "José Francisco Alfonso Rojas",
                "Kewin Paul Pardo Cortés", "Nydia Yurani Suárez Moscoso",
                "Oscar Mauricio Díaz Muñoz"),
            "Familia y Civil" to listOf("Emilio Alexander Mejía Ángulo",
                "Fransy Yanet Mambuscay López", "José Francisco Alfonso Rojas",
                "Kewin Paul Pardo Cortés", "Oscar Mauricio Díaz Muñoz",
                "Santiago Garzón")
        )

        // Tema de la cita
        spTema = findViewById(R.id.spTema)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTema,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTema.adapter = adapter
        }

        spHora = findViewById(R.id.spHora)
        ArrayAdapter.createFromResource(
            this,
            R.array.horas,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spHora.adapter = adapter
        }

        // Configurar abogado según el tema seleccionado
        spAbogado = findViewById(R.id.spAbogado)
        spTema.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTema = spTema.selectedItem.toString()
                val abogados = temaAbogadoMap[selectedTema] ?: emptyList()

                if (txtDia.text.isNotEmpty()) {
                    cambiarHorarioSegunAbogado()
                }

                // Mostrar u ocultar la grilla de selección de abogado
                if (selectedTema == "Víctimas") {
                    gridSeleccionarAbogado.visibility = View.VISIBLE

                    val abogadoAdapter = ArrayAdapter(
                        this@CrearCita,
                        R.drawable.spinner_item, // Usa el estilo definido
                        listOf("Edwin Yovanni Franco Bahamón")
                    )
                    abogado = "Edwin Yovanni Franco Bahamón"
                    abogadoAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
                    spAbogado.adapter = abogadoAdapter
                } else {
                    gridSeleccionarAbogado.visibility = View.VISIBLE

                    // Configurar el adaptador del Spinner de abogados
                    val abogadoAdapter = ArrayAdapter(
                        this@CrearCita,
                        R.drawable.spinner_item, // Usa el estilo definido
                        abogados
                    )
                    abogadoAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
                    spAbogado.adapter = abogadoAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita acción
            }
        }

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

        spHora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionHora = spHora.selectedItem.toString()
                println("Hora seleccionada: $seleccionHora")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita acción
            }
        }

        // Obtener el valor de la tarea desde el Intent
        tarea = intent.getStringExtra("tarea").toString()
        sujeto = intent.getStringExtra("sujeto").toString()

        // Configurar acciones en función de la tarea
        when (tarea) {
            "crear" -> {
                habilitarCampos(true)
                txtAnuncio.text = "Agendar Cita"
                if(sujeto == "cliente"){
                    // Un cliente la crea para sí mismo
                    btnSeleccionar.setOnClickListener{
                        scheduleAppointment(sujeto)
                    }
                    txtDocumento.visibility = EditText.GONE
                    tvDocumento.visibility = TextView.GONE
                }
                else{
                    // Un admin. la crea para un cliente
                    btnSeleccionar.setOnClickListener{
                        scheduleAppointment(sujeto)
                    }
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
            calendar = Calendar.getInstance()
            seleccionarFecha()
        }

        btnSalir.setOnClickListener{
            finish()
        }

        btnHorarios.setOnClickListener(){
            checkAndRequestManageStoragePermission()
        }

        btnEliminar.setOnClickListener(){
            eliminarCita()
        }
    }

    private fun habilitarCampos(habilitar:Boolean){
        txtDescripcion.isEnabled = habilitar
        spTema.isEnabled = habilitar
        spAbogado.isEnabled = habilitar
        spHora.isEnabled = habilitar
        btnFecha.isEnabled = habilitar
        btnSeleccionar.isEnabled = habilitar
    }

    private fun seleccionarFecha(){
        DatePickerDialog(this, { _, year, month, day ->

            val fechaSeleccionada = Calendar.getInstance().apply { set(year, month, day) }
            val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
            val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
            val diaNombre = dias[diaSemana - 1]
            seleccionFecha = fechaSeleccionada
            txtDia.text = "$diaNombre, $day/${month + 1}/$year"

            cambiarHorarioSegunAbogado()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    @SuppressLint("ResourceType")
    private fun cambiarHorarioSegunAbogado(){
        val abogadoSeleccionado = spAbogado.selectedItem.toString()
        val diaSeleccionado = txtDia.text.split(",")[0]
        val horarioAbogado = horariosAbogados[abogadoSeleccionado]?.get(diaSeleccionado)
        if (horarioAbogado != null) {
            val (horaInicio, horaFin) = horarioAbogado
            val horas = mutableListOf<String>()
            horas.add("Seleccionar hora")
            var hora = horaInicio
            val duracionCita = duracionCitas[abogadoSeleccionado] ?: 60
            while (hora <= horaFin) {
                horas.add(hora)
                hora = calcularHoraFin(hora.split(":")[0].toInt(), hora.split(":")[1].toInt(), duracionCita)
                // Filtrar Hora Almuerzo
                if (hora in horaAlmuerzo.first..horaAlmuerzo.second) {
                    hora = calcularHoraFin(hora.split(":")[0].toInt(), hora.split(":")[1].toInt(), duracionCita)
                }
            }
            val horaAdapter = ArrayAdapter(this, R.drawable.spinner_item, horas)
            horaAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spHora.adapter = horaAdapter
        } else {
            val horaAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.horas,
                R.drawable.spinner_item
            )
            horaAdapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spHora.adapter = horaAdapter
        }
    }

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
                    //  Tambien quitar horario de la cita
                    println(snapshot)
                    conseguirNombreAbogado(snapshot.child("correoAbogado").value.toString(), "eliminar")
                    eliminarHorarioOcupado(idCita.toString(),abogado, snapshot.child("fecha").value.toString())

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

    private fun eliminarHorarioOcupado(idCita: String, abogado: String, fecha: String){
        val ref = FirebaseDatabase.getInstance().getReference("horariosOcupados/$abogado/$fecha/$idCita")
        ref.removeValue()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkAndRequestManageStoragePermission() {
        // Verifica si ya se tiene el permiso especial
        if (Environment.isExternalStorageManager()) {
            // Permiso ya concedido, procede con la operación
            descargarHorarios()
        } else {
            // Dirige al usuario a las configuraciones para conceder el permiso
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:${applicationContext.packageName}")
            startActivityForResult(intent, 1002)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1002) {
            // Verifica nuevamente si se otorgó el permiso
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Permiso concedido, iniciando descarga.", Toast.LENGTH_SHORT).show()
                descargarHorarios()
            } else {
                Toast.makeText(this, "Permiso no concedido, no se puede proceder.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun descargarHorarios() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "download_channel"

        // Crear canal de notificación para API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Descargas",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación de progreso de descarga"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Crear notificación inicial
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Descargando archivo...")
            .setProgress(100, 0, true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManager.notify(1, notificationBuilder.build())

        // Configurar la ruta de descarga
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val localFile = File(downloadsDir, "Posibles Horarios.pdf")

        // Referencia al archivo en Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference.child("Archivos/Posibles Horarios.pdf")

        // Descarga el archivo
        storageReference.getFile(localFile).addOnSuccessListener {
            // Crear URI usando FileProvider para compartir el archivo
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", localFile)

            // Crear Intent para abrir el archivo PDF
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Crear PendingIntent para la notificación
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Actualizar la notificación al completar la descarga
            notificationBuilder
                .setContentTitle("Descarga completada")
                .setContentText("Pulsa para abrir el archivo")
                .setProgress(0, 0, false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)

            notificationManager.notify(1, notificationBuilder.build())

            Toast.makeText(this, "Archivo descargado: ${localFile.path}", Toast.LENGTH_LONG).show()

        }.addOnFailureListener { exception ->
            // Manejar errores en la descarga
            notificationBuilder
                .setContentTitle("Error en la descarga")
                .setContentText(exception.message)
                .setProgress(0, 0, false)
                .setSmallIcon(android.R.drawable.stat_notify_error)

            notificationManager.notify(1, notificationBuilder.build())

            Toast.makeText(this, "Error al descargar archivo: ${exception.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun consultarPorID() {
        // Consultar datos de la cita por el ID de la misma
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
                    val descripcionC = snapshot.child("descripcion").value.toString()
                    val fechaC = snapshot.child("fecha").value.toString()
                    val horaC = snapshot.child("hora").value.toString()
                    val abogadoC = snapshot.child("correoAbogado").value.toString()
                    val clienteC = snapshot.child("correoCliente").value.toString()
                    val temaC = snapshot.child("tema").value.toString()
                    val estadoC = snapshot.child("estado").value.toString()

                    txtConsultar.setText(snapshot.key.toString())
                    txtDescripcion.setText(descripcionC)
                    txtFecha.text = "Fecha: $fechaC, Hora: $horaC"
                    spTema.setSelection((spTema.adapter as ArrayAdapter<String>).getPosition(temaC))
                    println(spTema.selectedItem.toString())
                    // Change the format of the day and make it like the one in the calendar
                    val fecha = fechaC.split("-")
                    val year = fecha[2].toInt()
                    val month = fecha[1].toInt()
                    val day = fecha[0].toInt()
                    val fechaSeleccionada = Calendar.getInstance().apply { set(year, month - 1, day) }
                    val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
                    val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
                    val diaNombre = dias[diaSemana - 1]
                    txtDia.text = "$diaNombre, $day/$month/$year"

                    // Put the hour in the spinner
                    cambiarHorarioSegunAbogado()
                    spHora.setSelection((spHora.adapter as ArrayAdapter<String>).getPosition(horaC))

                    conseguirNombreAbogado(abogadoC, "modificar")
                    conseguirCedulaCliente(clienteC)

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

    private fun conseguirNombreAbogado(correoAbogado: String, modo:String){
        val ref = FirebaseDatabase.getInstance().getReference("abogadoData")
        ref.orderByChild("correo").equalTo(correoAbogado).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        // Extraer el correo
                        println(snapshot)
                        var nombreAbogado = childSnapshot.child("nombreCompleto").value.toString()
                        //
                        if (modo=="modificar"){
                        spAbogado.setSelection((spAbogado.adapter as ArrayAdapter<String>).getPosition(nombreAbogado))
                        }
                        else if (modo=="eliminar"){
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

    private fun conseguirCedulaCliente(correoCliente: String){
        val ref = FirebaseDatabase.getInstance().getReference("userData")
        ref.orderByChild("correo").equalTo(correoCliente).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    println(snapshot)
                    snapshot.children.forEach { childSnapshot ->
                        // Extraer el correo
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

    private fun enviarCorreoAdicionales(
        subject: String,
        body: String,
        correosAdicionales: List<String>
    ) {
        correosAdicionales.forEach { correo ->
            sendEmailInBackground(correo, subject, body)
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun scheduleAppointment(sujeto: String) {
        calendar = Calendar.getInstance()
        tema = spTema.selectedItem.toString()
        descripcion = txtDescripcion.text.toString()
        correosAdicionales = listOf("ricardoprovisional45@gmail.com", "ricardocorco@unisabana.edu.co")
        if(spTema.selectedItem.toString() != "Víctimas"){
            abogado = spAbogado.selectedItem.toString()
        }

        // Obtener correo del abogado teniendo su nombre
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
                                // Extraer el correo
                                nombreCliente = childSnapshot.child("nombreCompleto").value.toString()
                                println(nombreCliente)
                                if (nombreCliente.isNotEmpty()) {
                                    println("Nombre Cliente asignado: $nombreCliente")
                                    finalizarCreacion()
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

            // Obtener correo del usuario teniendo su cédula
            mDbRef = FirebaseDatabase.getInstance().getReference("userData")

            query = mDbRef.orderByChild("documento").equalTo(cedulaCliente)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { childSnapshot ->
                            correoCliente = childSnapshot.child("correo").value.toString()
                            nombreCliente = childSnapshot.child("nombreCompleto").value.toString()
                            if (correoCliente.isNotEmpty()) {
                                println("Correo Cliente asignado: $correoCliente")
                                finalizarCreacion()
                            } else {
                                Toast.makeText(this@CrearCita, "El correo del cliente no está disponible.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@CrearCita, "No se encontró información del cliente.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error en la consulta: ${error.message}")
                }
            })
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun finalizarCreacion() {
        // Validación: Seleccionar Fecha
        if (txtDia.text.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación: Seleccionar Hora
        if (seleccionHora.equals("Seleccionar hora")) {
            Toast.makeText(this, "Debe seleccionar una hora", Toast.LENGTH_SHORT).show()
            return
        }

        val year = seleccionFecha.get(Calendar.YEAR)
        val month = seleccionFecha.get(Calendar.MONTH)
        val day = seleccionFecha.get(Calendar.DAY_OF_MONTH)
        val fechaSeleccionada = seleccionFecha
        val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
        val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        val diaNombre = dias[diaSemana - 1]
        val horarioAbogado = horariosAbogados[abogado]?.get(diaNombre)


        val fechaActual = Calendar.getInstance()
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
                        val  horaSeleccionada = seleccionHora
                        val hourOfDay = horaSeleccionada.split(":")[0].toInt()
                        val minute = horaSeleccionada.split(":")[1].toInt()
                        if(horaSeleccionada !in horaInicio..horaFin || horaSeleccionada in horaAlmuerzo.first..horaAlmuerzo.second){
                            Toast.makeText(this, "Hora seleccionada no disponible", Toast.LENGTH_SHORT).show()
                        }else{
                            val duracion = duracionCitas[abogado] ?: 60 // Duración predeterminada de 60 minutos
                            val fecha = "$day-${month + 1}-$year"
                            val horaFinCita = calcularHoraFin(hourOfDay, minute, duracion)

                            verificarDisponibilidad(abogado, fecha, horaSeleccionada, horaFinCita) { disponible ->
                                if (disponible) {
                                    obtenerUltimoID()
                                    val cita = Cita(
                                        appointmentID,
                                        descripcion,
                                        fecha,
                                        horaSeleccionada,
                                        correoAbogado,
                                        correoCliente,
                                        tema,
                                        "Pendiente"
                                    )
                                    saveAppointmentToFirebase(cita, abogado, fecha, horaSeleccionada, horaFinCita)
                                    // Enviar el correo con la información de la cita
                                    val subject = "Cita en Personería - ID: ${cita.id}"
                                    var body = "Estimado Usuario:\n\nSu cita ha sido asignada exitosamente.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nAbogado: ${abogado}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."

                                    sendEmailInBackground(correoCliente, subject, body)

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

                                    body = "Estimado Abogado:\n\nTiene una nueva cita.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nUsuario: ${nombreCliente}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."
                                    sendEmailInBackground(correoAbogado, subject, body)

                                    // Mostrar detalles en la pantalla
                                    txtFecha.text = "Cita agendada para $fecha, $horaSeleccionada con ID: ${cita.id}"
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

    private fun verificarCitasPorSemana(correoCliente: String, fechaSeleccionada: Calendar, callback: (Boolean) -> Unit) {
        val inicioSemana = fechaSeleccionada.clone() as Calendar
        inicioSemana.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val finSemana = fechaSeleccionada.clone() as Calendar
        finSemana.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)

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
                            callback(false)
                            return
                        }
                    }
                    callback(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    @SuppressLint("DefaultLocale")
    private fun calcularHoraFin(hour: Int, minute: Int, duracion: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.add(Calendar.MINUTE, duracion)
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }

    private fun verificarDisponibilidad(
        abogado: String,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        callback: (Boolean) -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance().getReference("horariosOcupados/$abogado/$fecha")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (horario in snapshot.children) {
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


    private fun saveAppointmentToFirebase(cita: Cita, abogado: String, fecha: String, horaInicio: String, horaFin: String) {
        val database = FirebaseDatabase.getInstance()
        val citasRef = database.getReference("citas")
        val horariosRef = database.getReference("horariosOcupados/$abogado/$fecha")

        val citaData = mapOf(
            "id" to cita.id,
            "descripcion" to cita.descripcion,
            "fecha" to cita.fecha,
            "hora" to cita.hora,
            "correoAbogado" to cita.correoAbogado,
            "correoCliente" to cita.correoCliente,
            "tema" to cita.tema,
            "estado" to cita.estado
        )

        val horarioData = mapOf(
            "horaInicio" to horaInicio,
            "horaFin" to horaFin
        )

        citasRef.child(cita.id.toString()).setValue(citaData)
            .addOnSuccessListener {
                horariosRef.child(cita.id.toString()).setValue(horarioData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar el horario ocupado", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show()
            }
    }


    private fun sendEmailInBackground(recipientEmail: String, subject: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendEmail(recipientEmail, subject, body)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
            //es importante para crear la key de enviar
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