package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Bienvenida : AppCompatActivity() {

    private lateinit var txtCorreo: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        mAuth = FirebaseAuth.getInstance()

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
                        showRoleScreen()
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

    private fun showRoleScreen() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("userData").child(userId)
        val userRolRef = databaseRef.child("rol")

        userRolRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Aquí obtienes el valor del rol
                val userRol = snapshot.getValue(String::class.java)
                userRol?.let {
                    println("El rol del usuario es: $it")
                    if(userRol == "2") {
                        val intent = Intent(this@Bienvenida, InterfazAdmin::class.java)
                        finish()
                        startActivity(intent)
                    } else if(userRol == "1") {
                        val intent = Intent(this@Bienvenida, InterfazAbogado::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@Bienvenida, InterfazCliente::class.java)
                        finish()
                        startActivity(intent)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Maneja cualquier error de lectura de la base de datos
                Log.w("FirebaseDatabase", "Error al obtener el rol del usuario.", error.toException())
            }
        })
    }

}