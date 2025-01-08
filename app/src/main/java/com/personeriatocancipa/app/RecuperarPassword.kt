package com.personeriatocancipa.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Clase para recuperar contraseña
class RecuperarPassword : AppCompatActivity() {

    // Variables para Layout y Firebase
    private lateinit var txtCorreo: EditText // Campo de texto para correo
    private lateinit var btnRestablecer: Button // Botón para restablecer contraseña
    private lateinit var btnVolver: Button // Botón para volver
    private lateinit var mAuth: FirebaseAuth // Autenticación de Firebase
    private lateinit var mDbRef: DatabaseReference // Referencia a la base de datos

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        // Inicializa Autenticación de Firebase
        mAuth = FirebaseAuth.getInstance()

        // Asigna las referencias de los elementos de la interfaz
        txtCorreo = findViewById(R.id.txtCorreo)
        btnRestablecer = findViewById(R.id.btnRestablecer)
        btnVolver = findViewById(R.id.btnVolver)

        // Listener para el botón Restablecer
        btnRestablecer.setOnClickListener(){
            val correo = txtCorreo.text.toString() // Obtiene el correo ingresado
            if(correo.isEmpty()){ // Verifica si el campo está vacío
                Toast.makeText(this, // Muestra mensaje de error
                    "Ingrese un correo",
                    Toast.LENGTH_SHORT).show()
            }else{ // Si el campo no está vacío
                // Verificar si el correo está registrado en Admin.
                mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                var query = mDbRef.orderByChild("correo").equalTo(correo)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Si el correo existe en Admin, se procede al restablecimiento de contraseña
                            restablecer(correo)
                        } else {
                            // Verificar si el correo está registrado en Abogados
                            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                            query = mDbRef.orderByChild("correo").equalTo(correo)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // Si el correo existe en Abogados, restablece
                                        restablecer(correo)
                                    } else {
                                        // Verificar si el correo está registrado en Clientes
                                        mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                                        query = mDbRef.orderByChild("correo").equalTo(correo)
                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    // Si el correo existe en Clientes, restablece
                                                    restablecer(correo)
                                                } else {
                                                    // Muestra un mensaje si el correo no está registrado en ninguna categoría
                                                    Toast.makeText(
                                                        this@RecuperarPassword,
                                                        "Correo no registrado",
                                                        Toast.LENGTH_SHORT,
                                                    ).show()
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(  // Error en la consulta para Clientes
                                                    this@RecuperarPassword,
                                                    "Error al consultar la base de datos",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                            }
                                        })
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText( // Error en la consulta para Abogados
                                        this@RecuperarPassword,
                                        "Error al consultar la base de datos",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText( // Error en la consulta para Admin.
                            this@RecuperarPassword,
                            "Error al consultar la base de datos",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                })
            }
        }

        // Listener para el botón Volver
        btnVolver.setOnClickListener(){
            finish() // Finaliza la actividad actual y vuelve a la pantalla anterior
        }
    }

    // Método para enviar el correo de restablecimiento de contraseña
    private fun restablecer(correo: String) {
        mAuth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Muestra un mensaje indicando que el correo fue enviado con éxito
                    Toast.makeText(this, "Correo enviado. Revise su bandeja de entrada o su carpeta de 'No Deseados'", Toast.LENGTH_LONG).show()
                } else {
                    // Muestra un mensaje si hubo un error al enviar el correo
                    Toast.makeText(this, "Error al enviar correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}