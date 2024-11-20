package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.GridView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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

    private lateinit var gridSeleccionarAbogado: GridLayout
    private lateinit var txtConsultar: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var spAbogado: Spinner
    private lateinit var spTema: Spinner
    private lateinit var btnHorarios: Button
    private lateinit var btnSeleccionar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var txtFecha: TextView
    private lateinit var gridConsultar: GridLayout
    private lateinit var mDbRef: DatabaseReference
    private lateinit var tarea: String
    private lateinit var abogado: String

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


    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cita)

        // Buscar elementos de layout
        txtConsultar = findViewById(R.id.txtConsultar)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        btnHorarios = findViewById(R.id.btnHorarios)
        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnSalir = findViewById(R.id.btnSalir)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        txtFecha = findViewById(R.id.txtFecha)
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

        // Configurar abogado según el tema seleccionado
        spAbogado = findViewById(R.id.spAbogado)
        spTema.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTema = spTema.selectedItem.toString()
                val abogados = temaAbogadoMap[selectedTema] ?: emptyList()

                // Mostrar u ocultar la grilla de selección de abogado
                if (selectedTema == "Víctimas") {
                    gridSeleccionarAbogado.visibility = View.GONE
                    abogado = "Edwin Yovanni Franco Bahamón"
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

        // Obtener el valor de la tarea desde el Intent
        tarea = intent.getStringExtra("tarea").toString()

        btnSeleccionar.setOnClickListener(){
            scheduleAppointment()
        }

        // Configurar acciones en función de la tarea
        when (tarea) {
            "crear" -> {
                btnSeleccionar.setOnClickListener{
                    scheduleAppointment()
                }
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                gridConsultar.visibility = GridView.GONE
            }
            "modificar" -> {
                btnModificar.setOnClickListener {
                    // Lógica para modificar cita
                }
                btnEliminar.visibility = Button.GONE
            }
            "eliminar" -> {
                btnEliminar.setOnClickListener {
                    // Lógica para eliminar cita
                }
                btnModificar.visibility = Button.GONE
            }
        }

        btnSalir.setOnClickListener{
            finish()
        }

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
    private fun scheduleAppointment() {
        val calendar = Calendar.getInstance()
        val tema = spTema.selectedItem.toString()
        val descripcion = txtDescripcion.text.toString()
        var nombreCliente = ""
        var correoAbogado = ""
        var correoCliente = ""
        val correosAdicionales = listOf("ricardoprovisional45@gmail.com", "ricardocorco@unisabana.edu.co")
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
                    println("No se encontró ningún abogado con el nombre: $abogado")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error en la consulta: ${error.message}")
            }
        })

        // Obtener el correo del cliente (Asumiendo que es el usuario actual)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            correoCliente = currentUser.email.toString()
        } else {
            println("No hay un usuario autenticado.")
        }

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
                    }
                } else {
                    // Si no se encontró el nombre en los datos
                    println("No se encontró ningún usuario con el correo: $correoCliente")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error en la consulta: ${error.message}")
            }
        })

        DatePickerDialog(this, { _, year, month, day ->

            val fechaSeleccionada = Calendar.getInstance().apply { set(year, month, day) }
            val diaSemana = fechaSeleccionada.get(Calendar.DAY_OF_WEEK)
            val dias = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
            val diaNombre = dias[diaSemana - 1]
            val horarioAbogado = horariosAbogados[abogado]?.get(diaNombre)

            // Validación: Una semana de antelación
            val fechaActual = Calendar.getInstance()
            fechaActual.add(Calendar.DAY_OF_YEAR, 7) // Sumar una semana
            if (fechaSeleccionada.before(fechaActual)) {
                Toast.makeText(this, "Debe agendarse con al menos una semana de antelación.", Toast.LENGTH_SHORT).show()
                return@DatePickerDialog
            }else{
                // Validación: Una cita por semana
                verificarCitasPorSemana(correoCliente, fechaSeleccionada) { puedeAgendar ->
                    if (!puedeAgendar) {
                        Toast.makeText(this, "Solo puede agendar una cita por semana.", Toast.LENGTH_SHORT).show()
                        return@verificarCitasPorSemana
                    }else{
                        if(horarioAbogado != null){
                            val (horaInicio, horaFin) = horarioAbogado
                            TimePickerDialog(this, { _, hourOfDay, minute ->
                                val horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute)
                                if(horaSeleccionada !in horaInicio..horaFin || horaSeleccionada in horaAlmuerzo.first..horaAlmuerzo.second){
                                    Toast.makeText(this, "Hora seleccionada no disponible", Toast.LENGTH_SHORT).show()
                                    return@TimePickerDialog
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
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                        }else{
                            Toast.makeText(this, "Día no disponible", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
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