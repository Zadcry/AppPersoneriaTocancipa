package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Admin : AppCompatActivity() {

    private lateinit var txtUsuario: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnGestionarCitas: Button
    private lateinit var btnGestionarUsuarios: Button
    private lateinit var btnSalir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        mAuth = FirebaseAuth.getInstance()
        btnGestionarUsuarios = findViewById(R.id.btnGestionarUsuarios)
        btnGestionarCitas = findViewById(R.id.btnGestionarCitas)
        btnSalir = findViewById(R.id.btnSalir)

        cargarNombre()

        btnGestionarUsuarios.setOnClickListener{
            val intent = Intent(this@Admin, CRUD::class.java)
            intent.putExtra("tipo", "usuario")
            startActivity(intent)
        }

        btnGestionarCitas.setOnClickListener{
            val intent = Intent(this@Admin, CRUD::class.java)
            intent.putExtra("tipo", "cita")
            startActivity(intent)
        }

        btnSalir.setOnClickListener{
            val intent = Intent(this@Admin, Bienvenida::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun cargarNombre() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("userData").child(userId)
        val userNombreRef = databaseRef.child("nombreCompleto")

        userNombreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Aquí obtienes el valor del rol
                val userNombre = snapshot.getValue(String::class.java)
                userNombre?.let {
                    cargarPrimerNombre(it)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Maneja cualquier error de lectura de la base de datos
                Log.w("FirebaseDatabase", "Error al obtener el nombre del usuario.", error.toException())
            }
        })
    }

    private fun cargarPrimerNombre(nombreCompleto: String) {
        val primerNombre = nombreCompleto.split(" ")[0]
        txtUsuario = findViewById(R.id.txtUsuario)
        txtUsuario.text = "Bienvenido(a), señor(a) $primerNombre"
    }
}