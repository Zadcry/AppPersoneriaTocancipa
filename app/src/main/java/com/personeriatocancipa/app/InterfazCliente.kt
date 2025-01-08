package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Clase que gestiona la interfaz del cliente
class InterfazCliente : AppCompatActivity() {

    // Declaración de variables para elementos de la interfaz
    private lateinit var txtUsuario: TextView // Elemento de texto para mostrar el nombre del usuario
    private lateinit var mAuth: FirebaseAuth // Variable para gestionar la autenticación
    private lateinit var btnAgendarCita: Button // Botón para agendar una cita
    private lateinit var btnVerCitas: Button // Botón para ver las citas agendadas
    private lateinit var btnSalir: Button // Botón para cerrar sesión
    private lateinit var btnModificar: Button // Botón para modificar la cuenta

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente)

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Asignación de elementos de la interfaz a variables
        btnAgendarCita = findViewById(R.id.btnAgendarCita)
        btnVerCitas = findViewById(R.id.btnVerCitas)
        btnSalir = findViewById(R.id.btnSalir)
        btnModificar = findViewById(R.id.btnModificar)

        // Cargar el nombre del cliente al iniciar
        cargarNombre()

        // Listener para agendar una nueva cita
        btnAgendarCita.setOnClickListener{
            // Redirige a la pantalla de creación de cita
            val intent = Intent(this@InterfazCliente, CrearCita::class.java)
            intent.putExtra("tarea", "crear") // Especifica la tarea como creación
            intent.putExtra("sujeto", "cliente") // Define el sujeto como cliente
            startActivity(intent)
        }

        // Listener para ver las citas del cliente
        btnVerCitas.setOnClickListener{
            // Redirige a la pantalla de consulta de citas
            val intent = Intent(this@InterfazCliente, ConsultarCitasCliente::class.java)
            startActivity(intent)
        }

        // Listener para modificar información personal
        btnModificar.setOnClickListener{
            // Redirige a la pantalla de gestión de cuenta
            val intent = Intent(this@InterfazCliente, CrearCuenta::class.java)
            intent.putExtra("tarea", "modificar") // Especifica la tarea como modificación
            intent.putExtra("sujeto", "propio") // Define el sujeto como propio
            startActivity(intent)
        }

        // Listener para salir de la sesión
        btnSalir.setOnClickListener{
            finish() // Finaliza la actividad actual
        }

    }

    // Método para cargar el nombre del cliente desde la base de datos
    private fun cargarNombre() {
        // Obtiene el UID del usuario actual
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Referencia a la base de datos para obtener los datos del cliente
        val databaseRef = FirebaseDatabase.getInstance().getReference("userData").child(userId)
        val userNombreRef = databaseRef.child("nombreCompleto")

        // Lee el nombre completo del cliente desde Firebase
        userNombreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtiene el nombre completo y lo procesa para mostrar solo el primer nombre
                val userNombre = snapshot.getValue(String::class.java)
                userNombre?.let {
                    cargarPrimerNombre(it) // Llama al método para obtener el primer nombre
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Maneja errores de lectura desde la base de datos
                Log.w("FirebaseDatabase", "Error al obtener el nombre del usuario.", error.toException())
            }
        })
    }

    // Método para mostrar el primer nombre del cliente en la interfaz
    private fun cargarPrimerNombre(nombreCompleto: String) {
        // Separa el nombre completo y toma el primer nombre
        val primerNombre = nombreCompleto.split(" ")[0]

        // Asigna el texto de bienvenida en el TextView
        txtUsuario = findViewById(R.id.txtUsuario)
        txtUsuario.text = "Bienvenido(a), señor(a) $primerNombre"
    }

}