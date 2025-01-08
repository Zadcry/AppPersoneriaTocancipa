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

// Clase que gestiona la interfaz del administrador
class InterfazAdmin : AppCompatActivity() {

    // Declaración de variables para elementos de la interfaz
    private lateinit var txtUsuario: TextView // Elemento de texto para mostrar el nombre del usuario
    private lateinit var mAuth: FirebaseAuth // Variable para gestionar la autenticación de Firebase
    private lateinit var btnGestionarCitas: Button // Botón para gestionar citas
    private lateinit var btnGestionarUsuarios: Button // Botón para gestionar usuarios
    private lateinit var btnSalir: Button // Botón para salir de la interfaz

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Asigna referencias a los elementos del diseño
        // a partir de las variables definidas anteriormente
        btnGestionarUsuarios = findViewById(R.id.btnGestionarUsuarios)
        btnGestionarCitas = findViewById(R.id.btnGestionarCitas)
        btnSalir = findViewById(R.id.btnSalir)

        // Carga el nombre del administrador al iniciar la actividad
        cargarNombre()

        // Listener para gestionar usuarios
        btnGestionarUsuarios.setOnClickListener{
            // Redirige a la pantalla de CRUD para gestionar usuarios
            val intent = Intent(this@InterfazAdmin, CRUD::class.java)
            intent.putExtra("tipo", "usuario") // Define el tipo como usuario
            startActivity(intent)
        }

        // Listener para gestionar citas
        btnGestionarCitas.setOnClickListener {
            // Redirige a la pantalla de CRUD para gestionar citas
            val intent = Intent(this@InterfazAdmin, CRUD::class.java)
            intent.putExtra("tipo", "cita") // Define el tipo como cita
            startActivity(intent)
        }

        // Listener para salir de la sesión
        btnSalir.setOnClickListener {
            // Redirige a la pantalla de bienvenida y finaliza la actividad actual
            val intent = Intent(this@InterfazAdmin, Bienvenida::class.java)
            finish()
            startActivity(intent)
        }
    }

    // Método para cargar el nombre del usuario desde la base de datos
    private fun cargarNombre() {
        // Obtiene el UID del usuario autenticado
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Referencia a la base de datos para obtener los datos del administrador
        val databaseRef = FirebaseDatabase.getInstance().getReference("AdminData").child(userId)
        val userNombreRef = databaseRef.child("nombreCompleto")

        // Lee el nombre completo del usuario desde Firebase
        userNombreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtiene el nombre completo y lo procesa para obtener el primer nombre
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

    // Método para mostrar el primer nombre del administrador en la interfaz
    private fun cargarPrimerNombre(nombreCompleto: String) {
        // Separa el nombre completo y toma el primer nombre
        val primerNombre = nombreCompleto.split(" ")[0]

        // Asigna el texto de bienvenida en el TextView
        txtUsuario = findViewById(R.id.txtUsuario)
        txtUsuario.text = "Bienvenido(a), señor(a) $primerNombre"
    }
}