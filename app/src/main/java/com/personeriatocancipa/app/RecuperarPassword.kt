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

class RecuperarPassword : AppCompatActivity() {

    private lateinit var txtCorreo: EditText
    private lateinit var btnRestablecer: Button
    private lateinit var btnVolver: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        mAuth = FirebaseAuth.getInstance()

        //Obtiene valores de Layout
        txtCorreo = findViewById(R.id.txtCorreo)
        btnRestablecer = findViewById(R.id.btnRestablecer)
        btnVolver = findViewById(R.id.btnVolver)

        //Crea eventListener para clicks en "Restablecer"
        btnRestablecer.setOnClickListener(){
            val correo = txtCorreo.text.toString()
            if(correo.isEmpty()){
                Toast.makeText(this, "Ingrese un correo", Toast.LENGTH_SHORT).show()
            }else{
                // Verificar si el correo está registrado en Admin.
                mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                var query = mDbRef.orderByChild("correo").equalTo(correo)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Si encuentra en Admin.
                            restablecer(correo)
                        } else {
                            // Si no es Admin.
                            // Verificar si el correo está registrado en Abogados
                            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                            query = mDbRef.orderByChild("correo").equalTo(correo)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // Si encuentra en Abogados
                                        restablecer(correo)
                                    } else {
                                        // Si no es Abogado
                                        // Verificar si el correo está registrado en Cliente
                                        mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                                        query = mDbRef.orderByChild("correo").equalTo(correo)
                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    restablecer(correo)
                                                } else {
                                                    Toast.makeText(
                                                        this@RecuperarPassword,
                                                        "Correo no registrado",
                                                        Toast.LENGTH_SHORT,
                                                    ).show()
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(
                                                    this@RecuperarPassword,
                                                    "Error al consultar la base de datos",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                            }
                                        })
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        this@RecuperarPassword,
                                        "Error al consultar la base de datos",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@RecuperarPassword,
                            "Error al consultar la base de datos",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                })
            }
        }

        //Crea eventListener para clicks en "Volver"
        btnVolver.setOnClickListener(){
            finish()
        }
    }

    private fun restablecer(correo: String) {
        mAuth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email sent
                    Toast.makeText(this, "Correo enviado. Revise su bandeja de entrada o su carpeta de 'No Deseados'", Toast.LENGTH_LONG).show()
                } else {
                    // Email not sent
                    Toast.makeText(this, "Error al enviar correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}