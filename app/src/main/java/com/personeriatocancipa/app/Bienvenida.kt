package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Clase que gestiona la pantalla de bienvenida e inicio de sesión
class Bienvenida : AppCompatActivity() {

    // Declaración de variables para los elementos de la interfaz de usuario
    private lateinit var txtCorreo: EditText // Variable de Texto Editable Correo Electrónico
    private lateinit var txtClave: EditText // Variable de Texto Editable Clave/Contraseña
    private lateinit var btnLogin: Button // Variable de Botón Login
    private lateinit var btnSignUp: Button // Variable de Botón Sign Up
    private lateinit var btnRecuperarPassword: Button // Variable Botón Recuperar Password
    private lateinit var btnTogglePassword: Button // Variable Botón Toggle mostrar contraseña
    private lateinit var mAuth: FirebaseAuth // Instancia de autenticación de Firebase

    // Método que se ejecuta al crear la actividad
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Asigna referencias a los elementos de la interfaz de usuario
        // en las variables definidas anteriormente
        txtCorreo = findViewById(R.id.txtCorreo)
        txtClave = findViewById(R.id.txtClave)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnRecuperarPassword = findViewById(R.id.btnRecuperarPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)


        // Listener para el botón de inicio de sesión
        btnLogin.setOnClickListener(){
            val correo = txtCorreo.text.toString()
            val clave = txtClave.text.toString()
            login(correo, clave) // Llama al método login con las credenciales ingresadas
        }

        // Listener para el botón de registro
        btnSignUp.setOnClickListener(){
            signup() // Llama al método signup para registrar un nuevo usuario
            txtCorreo.text.clear()
            txtClave.text.clear()
        }

        // Listener para el botón de recuperación de contraseña
        btnRecuperarPassword.setOnClickListener() {
            recuperarPassword() // Navega a la pantalla de recuperación de contraseña
        }

        // Listener para mostrar/ocultar contraseña
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnTogglePassword.setOnClickListener { v: View? ->
            // Alterna entre mostrar y ocultar la contraseña
            if (txtClave.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                txtClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtClave.setSelection(txtClave.text.length) // Mantiene el cursor al final del texto
        }
    }

    // Método para abrir la pantalla de registro
    private fun signup() {
        val intent = Intent(this@Bienvenida, CrearCuenta::class.java)
        intent.putExtra("tarea","crear") // Añade tarea como parámetro para crear cuenta
        intent.putExtra("usuario","cliente") // Añade tipo de usuario como parámetro
        startActivity(intent) // Inicia la actividad de creación de cuenta
    }

    // Método para iniciar sesión con correo y contraseña
    private fun login(correo: String?, clave: String?) {
        // Verifica si los campos están vacíos
        if(correo.isNullOrEmpty() || clave.isNullOrEmpty()){
            Toast.makeText( // Muestra mensaje de error
                this@Bienvenida,
                "¡Ingresa información!",
                Toast.LENGTH_SHORT
            ) .show()
        }else{
            // Autentica al usuario en Firebase con correo y contraseña
            mAuth.signInWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this){
                        task ->
                    if(task.isSuccessful){
                        showRoleScreen() // Muestra la pantalla de acuerdo al rol del usuario
                        txtCorreo.text.clear()
                        txtClave.text.clear()
                    }else{
                        Toast.makeText( // Muestra mensaje de error
                            this@Bienvenida,
                            "¡Hubo un error!",
                            Toast.LENGTH_SHORT
                        ) .show()
                    }
                }
        }
    }

    // Muestra la pantalla correspondiente según el rol del usuario
    private fun showRoleScreen() {
        buscarSiCliente() // Inicia la búsqueda del rol cliente
    }

    // Busca si el usuario tiene rol de cliente
    private fun buscarSiCliente(){
        println("Buscando Cliente")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRefCliente = FirebaseDatabase.getInstance().getReference("userData").child(userId)

        databaseRefCliente.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) {
                    val estado = snapshot.child("estado").value.toString()
                    if (estado == "Activo") {
                        // Navega a la interfaz cliente si el estado es activo
                        val intent = Intent(this@Bienvenida, InterfazCliente::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText( // Muestra mensaje de error
                            this@Bienvenida,
                            "¡Esta cuenta ha sido desactivada!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    buscarSiAbogado() // Busca si el usuario es abogado
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", "Error al leer datos", error.toException())
            }
        })
    }

    // Busca si el usuario tiene rol de abogado
    private fun buscarSiAbogado(){
        println("Buscando Abogado")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Obtiene el UID del usuario
        val databaseRefAbogado = FirebaseDatabase.getInstance().getReference("abogadoData").child(userId) // Obtiene la referencia a la base de datos

        databaseRefAbogado.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) { // Si el usuario existe
                    val estado = snapshot.child("estado").value.toString() // Obtiene el estado del usuario
                    if(estado == "Activo") { // Si el estado es activo
                        val intent = Intent(this@Bienvenida, InterfazAbogado::class.java) // Navega a la interfaz de abogado
                        startActivity(intent)
                    }else{
                        Toast.makeText( // Muestra mensaje de error
                            this@Bienvenida,
                            "¡Esta cuenta ha sido desactivada!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    buscarSiAdmin() // Busca si el usuario es administrador
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", "Error al leer datos", error.toException())
            }
        })
    }

    // Busca si el usuario tiene rol de administrador
    private fun buscarSiAdmin(){
        println("Buscando Admin")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Obtiene el UID del usuario
        val databaseRefAdmin = FirebaseDatabase.getInstance().getReference("AdminData").child(userId) // Obtiene la referencia a la base de datos


        println(userId)
        databaseRefAdmin.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) { // Si el usuario existe
                    val estado = snapshot.child("estado").value.toString() // Obtiene el estado del usuario
                    if(estado == "Activo") { // Si el estado es activo
                        val intent = Intent(this@Bienvenida, InterfazAdmin::class.java) // Navega a la interfaz de administrador
                        startActivity(intent)
                    }else{
                        Toast.makeText( // Muestra mensaje de error
                            this@Bienvenida,
                            "¡Esta cuenta ha sido desactivada!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText( // Muestra mensaje de error
                        this@Bienvenida,
                        "¡Usuario no encontrado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", "Error al leer datos", error.toException())
            }
        })
    }

    // Método para abrir la pantalla de recuperación de contraseña
    private fun recuperarPassword(){
        val intent = Intent(this@Bienvenida, RecuperarPassword::class.java) // Navega a la pantalla de recuperación de contraseña
        startActivity(intent)
    }

}