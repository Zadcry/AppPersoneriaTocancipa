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

// Clase que gestiona la interfaz de un abogado
class InterfazAbogado : AppCompatActivity() {

    // Declaración de variables para elementos de la interfaz
    private lateinit var txtUsuario: TextView // Elemento de texto para mostrar el nombre del usuario
    private lateinit var mAuth: FirebaseAuth // Variable para gestionar la autenticación de Firebase
    private lateinit var btnConsultarCitas: Button // Botón para consultar las citas
    private lateinit var btnSalir: Button // Botón para salir de la interfaz
    private lateinit var btnModificar: Button // Botón para modificar información de abogado

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abogado)

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Asigna referencias a los elementos del diseño
        // a partir de las variables definidas anteriormente
        btnConsultarCitas = findViewById(R.id.btnConsultarCitas)
        btnSalir = findViewById(R.id.btnSalir)
        btnModificar = findViewById(R.id.btnModificar)

        // Asigna referencias a los botones en el diseño
        cargarNombre()

        // Listener para consultar citas
        btnConsultarCitas.setOnClickListener{
            // Redirige a la pantalla de consulta de citas
            val intent = Intent(this@InterfazAbogado, ConsultarCitasAbogado::class.java)
            startActivity(intent)
        }

        // Listener para modificar información del abogado
        btnModificar.setOnClickListener{
            // Redirige a la pantalla de modificación de información
            val intent = Intent(this@InterfazAbogado, CrearAbogado::class.java)
            intent.putExtra("tarea", "modificar") // Define la tarea como modificar
            intent.putExtra("sujeto", "propio") // Define el sujeto como el propio usuario
            startActivity(intent)
        }

        // Listener para salir de la sesión
        btnSalir.setOnClickListener {
            finish() // Finaliza la actividad actual y cierra la sesión
        }
    }

    // Método para cargar el nombre del usuario desde la base de datos
    private fun cargarNombre() {
        // Obtiene el ID del usuario autenticado
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Referencia a la base de datos para obtener los datos del abogado
        val databaseRef = FirebaseDatabase.getInstance().getReference("abogadoData").child(userId)
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
                // Maneja cualquier error de lectura de la base de datos
                Log.w("FirebaseDatabase", "Error al obtener el nombre del usuario.", error.toException())
            }
        })
    }

    // Método para mostrar el primer nombre del abogado en la interfaz
    private fun cargarPrimerNombre(nombreCompleto: String) {
        // Separa el nombre completo y toma el primer nombre
        val primerNombre = nombreCompleto.split(" ")[0]

        // Asigna el texto de bienvenida en el TextView
        txtUsuario = findViewById(R.id.txtUsuario)
        txtUsuario.text = "Bienvenido(a), señor(a) $primerNombre"
    }

}