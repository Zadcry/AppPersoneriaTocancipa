package com.personeriatocancipa.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RecuperarPassword : AppCompatActivity() {

    private lateinit var txtCorreo: EditText
    private lateinit var btnRestablecer: Button
    private lateinit var btnVolver: Button
    private lateinit var mAuth: FirebaseAuth

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
                restablecer(correo)
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
                    Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                } else {
                    // Email not sent
                    Toast.makeText(this, "Error al enviar correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}