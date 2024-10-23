package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Bienvenida : AppCompatActivity() {

    private lateinit var txtCorreo: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        //Obtiene valores de Layout
        txtCorreo = findViewById(R.id.txtCorreo)
        txtContraseña = findViewById(R.id.txtContraseña)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)


        //Crea eventListener para clicks en "Log In"
        btnLogin.setOnClickListener(){
            val correo = txtCorreo.text.toString()
            val clave = txtContraseña.text.toString()
            login(correo, clave)
        }

        //Crea eventListener para clicks en "Sign Up"
        btnSignUp.setOnClickListener(){
            signup()
        }
    }

    private fun signup() {
        val intent = Intent(this@Bienvenida, CrearCuenta::class.java)
        finish()
        startActivity(intent)
    }

    private fun login(correo: String?, clave: String?) {
        //Login de usuario
        if(correo.isNullOrEmpty() || clave.isNullOrEmpty()){
            Toast.makeText(
                this@Bienvenida,
                "¡Ingresa información!",
                Toast.LENGTH_SHORT
            ) .show()
        }else{
            mAuth.signInWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this){
                        task ->
                    if(task.isSuccessful){
                        val intent = Intent(this@Bienvenida, MenuPrincipal::class.java)
                        finish()
                        startActivity(intent)
                    }else{
                        Toast.makeText(
                            this@Bienvenida,
                            "¡Hubo un error!",
                            Toast.LENGTH_SHORT
                        ) .show()
                    }
                }
        }
    }


}