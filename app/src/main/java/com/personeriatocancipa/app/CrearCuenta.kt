package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrearCuenta : AppCompatActivity() {
    //Crea variables de Layout

    private lateinit var txtNombre: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var txtEdad: EditText
    private lateinit var txtDireccion: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var spSexo: Spinner
    private lateinit var spEscolaridad: Spinner
    private lateinit var spGrupo: Spinner
    private lateinit var txtGrupoEtnico: EditText
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var tarea = intent.getStringExtra("tarea")


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        //Manejo valores de Combo Box

        //Sexo
        spSexo = findViewById(R.id.spSexo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesSexo,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spSexo.adapter = adapter
        }

        //Escolaridad
        spEscolaridad = findViewById(R.id.spEscolaridad)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesEscolaridad,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spEscolaridad.adapter = adapter
        }

        // Grupo Étnico
        spGrupo = findViewById(R.id.spGrupo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesGrupo,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spGrupo.adapter = adapter
        }

        // Muestra pregunta si hace parte de Grupo Étnico
        spGrupo.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtener el elemento seleccionado
                val selectedItem = parent.getItemAtPosition(position).toString()

                if (selectedItem == "Sí") {
                    findViewById<GridLayout>(R.id.gridSiGrupo).visibility = View.VISIBLE
                } else {
                    findViewById<GridLayout>(R.id.gridSiGrupo).visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })

        //Comunidad Vulnerable
        val spComunidad: Spinner = findViewById(R.id.spComunidad)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesComunidad,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spComunidad.adapter = adapter
        }



        //Obtiene demás elementos de Layout
        txtNombre = findViewById(R.id.txtNombre)
        txtClave = findViewById(R.id.txtClave)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtEdad = findViewById(R.id.txtEdad)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtGrupoEtnico = findViewById(R.id.txtGrupoEtnico)
        btnSalir = findViewById(R.id.btnSalir)
        btnSignUp = findViewById(R.id.btnSignUp)

        if(!tarea.equals("crear")){

        }

        btnSalir.setOnClickListener(){
            saltarBienvenida()
        }

        btnSignUp.setOnClickListener(){
            val nombre = txtNombre.text.toString()
            val clave = txtClave.text.toString()
            val documento = txtDocumento.text.toString()
            val edadTexto = txtEdad.text.toString()
            val direccion = txtDireccion.text.toString()
            val telefono = txtTelefono.text.toString()
            val correo = txtCorreo.text.toString()
            val sexo = spSexo.selectedItem.toString()
            val escolaridad = spEscolaridad.selectedItem.toString()
            val grupo = spGrupo.selectedItem.toString()
            val siGrupo = txtGrupoEtnico.text.toString()
            val comunidad = spComunidad.selectedItem.toString()

            if (nombre.isEmpty() || clave.isEmpty() || documento.isEmpty()
                || edadTexto.isBlank() || direccion.isEmpty() || telefono.isEmpty()
                || correo.isEmpty() ||
                ((findViewById<GridLayout>(R.id.gridSiGrupo).visibility == View.VISIBLE)
                        && siGrupo.isEmpty())) {
                Toast.makeText(
                    this@CrearCuenta,
                    "Diligencie todos los datos",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            } else {
                val edad = edadTexto.toInt()
                signUp(nombre, clave, documento, edad,
                    direccion, telefono, correo, sexo, escolaridad,
                    grupo, siGrupo, comunidad)
            }

        }

    }

    private fun signUp(
        nombre: String,
        clave: String,
        documento: String,
        edad: Int,
        direccion: String,
        telefono: String,
        correo: String,
        sexo: String,
        escolaridad: String,
        grupo: String,
        siGrupo: String?,
        comunidad: String
    ) {
        //Logica para crear usuarios
        mAuth.createUserWithEmailAndPassword(correo, clave)
            .addOnCompleteListener(this){
                    task ->
                if (task.isSuccessful){
                    addUserToDatabase(nombre, documento, edad, direccion,
                        telefono, correo, sexo, escolaridad, grupo,
                        siGrupo, comunidad, mAuth.currentUser?.uid!!)

                    val intent = Intent(this@CrearCuenta, Cliente::class.java)
                    finish()
                    startActivity(intent)

                }else{
                    Toast.makeText(
                        this@CrearCuenta,
                        "Ha ocurrido un error",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(nombre: String,
                                  documento: String,
                                  edad: Int,
                                  direccion: String,
                                  telefono: String,
                                  correo: String,
                                  sexo: String,
                                  escolaridad: String,
                                  grupo: String,
                                  siGrupo: String?,
                                  comunidad: String,
                                  uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("userData").child(uid).setValue(
            Usuario(nombre, documento, edad, direccion, telefono,
                correo, sexo, escolaridad, siGrupo, grupo, comunidad, "0",uid))
        Toast.makeText(
            this@CrearCuenta,
            "Cuenta creada exitosamente",
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun saltarBienvenida() {
        val intent = Intent(this@CrearCuenta, Bienvenida::class.java)
        finish()
        startActivity(intent)
    }
}