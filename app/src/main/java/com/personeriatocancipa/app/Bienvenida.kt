package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                        val intent = Intent(this@Bienvenida, Cliente::class.java)
                        finish()
                        startActivity(intent)
                    }else {
                        mAuth.signInWithEmailAndPassword(correo, clave)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val currentUser = mAuth.currentUser
                                    if (currentUser != null) {
                                        val db = FirebaseFirestore.getInstance()
                                        db.collection("usuarios").document(currentUser.uid).get()
                                            .addOnSuccessListener { document ->
                                                if (document != null) {
                                                    val role = document.getString("rol")
                                                    // porque en kotlin el switch case es when?
                                                    when (role) {
                                                        "0" -> {
                                                            val intent = Intent(this@Bienvenida, Cliente::class.java)
                                                            startActivity(intent)
                                                        }
                                                        "1" -> {
                                                            val intent = Intent(this@Bienvenida, Abogado::class.java)
                                                            startActivity(intent)
                                                        }
                                                        "2" -> {
                                                            val intent = Intent(this@Bienvenida, Admin::class.java)
                                                            startActivity(intent)
                                                        }
                                                        else -> {
                                                            Toast.makeText(
                                                                this@Bienvenida,
                                                                "no se como pero el rol no existe",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                    finish()
                                                } else {
                                                    Toast.makeText(
                                                        this@Bienvenida,
                                                        "¡Hubo un error al obtener el rol!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    }
                                } else {
                                    // Si el login falla, mostrar un mensaje
                                    Toast.makeText(
                                        this@Bienvenida,
                                        "¡Hubo un error!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                }
        }
    }


}}}}