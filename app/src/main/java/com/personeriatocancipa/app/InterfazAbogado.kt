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

class InterfazAbogado : AppCompatActivity() {
    private lateinit var txtUsuario: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnConsultarCitas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abogado)

        mAuth = FirebaseAuth.getInstance()
        btnConsultarCitas = findViewById(R.id.btnConsultarCitas)

        cargarNombre()

        btnConsultarCitas.setOnClickListener{
            // Redirigir a la actividad de consulta de citas
            val intent = Intent(this@InterfazAbogado, ConsultarCitasAbogado::class.java)
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