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
import javax.mail.PasswordAuthentication
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



    val auth = FirebaseAuth.getInstance()

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
                if (selectedTema == "Administrativo") {
                    gridSeleccionarAbogado.visibility = View.GONE
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

    private fun scheduleAppointment() {
        val calendar = Calendar.getInstance()
        val tema = spTema.selectedItem.toString()
        val abogado = spAbogado.selectedItem.toString()
        var nombreCliente = ""
        var correoAbogado = ""
        var correoCliente = ""

        // Obtener correo del abogado teniendo su nombre
        mDbRef = FirebaseDatabase.getInstance().getReference("userData")

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

        // Obtener el correo del usuario actual
        val currentUser = auth.currentUser
        if (currentUser != null) {
            correoCliente = currentUser.email.toString()
        } else {
            println("No hay un usuario autenticado.")
        }

        // Obtener correo del abogado teniendo su nombre
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

        /*if (tarea.equals("crear")) {
            // Obtener el correo del usuario actual
            val currentUser = auth.currentUser
            if (currentUser != null) {
                correoCliente = currentUser.email.toString()
            } else {
                println("No hay un usuario autenticado.")
            }
        }*/

        val descripcion = txtDescripcion.text.toString()

        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val fecha = "$day-${month + 1}-$year"
                val hora = String.format("%02d:%02d", hourOfDay, minute) // Formato de hora y minuto con dos dígitos

                // Crear la cita con el ID único obtenido
                val cita = Cita(appointmentID, descripcion, fecha, hora, correoAbogado, correoCliente, tema, "Pendiente")

                // Guardar la cita en Firebase
                saveAppointmentToFirebase(cita)

                // Enviar el correo con la información de la cita
                val subject = "Cita en Personería - ID: ${cita.id}"
                var body = "Estimado Usuario:\n\nSu cita ha sido asignada exitosamente.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nAbogado: ${abogado}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."

                sendEmailInBackground(correoCliente, subject, body)

                body = "Estimado Abogado:\n\nTiene una nueva cita.\n\nFecha: $fecha, $hourOfDay:$minute.\nNúmero de cita: ${cita.id}.\nUsuario: ${nombreCliente}.\nTema: ${cita.tema}.\nDescripción: $descripcion.\n\nAtentamente,\nPersonería de Tocancipá."
                sendEmailInBackground(correoAbogado, subject, body)

                // Mostrar detalles en la pantalla
                txtFecha.text = "Cita agendada para $fecha, $hora con ID: ${cita.id}"

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun saveAppointmentToFirebase(cita: Cita) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("citas")

        val appointmentData = mapOf(
            "id" to cita.id,
            "descripcion" to cita.descripcion,
            "fecha" to cita.fecha,
            "hora" to cita.hora,
            "correoAbogado" to cita.correoAbogado,
            "correoCliente" to cita.correoCliente,
            "tema" to cita.tema,
            "estado" to cita.estado
        )

        ref.child(cita.id.toString()).setValue(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita agendada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show()
            }
    }


    fun sendEmailInBackground(recipientEmail: String, subject: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendEmail(recipientEmail, subject, body)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun sendEmail(recipientEmail: String, subject: String, body: String) {
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