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
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrearCuenta : AppCompatActivity() {
    //Crea variables de Layout

    private lateinit var gridConsultar: GridLayout
    private lateinit var txtAnuncio: TextView
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
    private lateinit var spComunidad: Spinner
    private lateinit var txtGrupoEtnico: EditText
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var tarea: String = ""

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        tarea = intent.getStringExtra("tarea").toString()
        println(tarea)
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
        spComunidad = findViewById(R.id.spComunidad)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesComunidad,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spComunidad.adapter = adapter
        }



        //Obtiene demás elementos de Layout
        gridConsultar = findViewById(R.id.gridConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
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
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)

        if(tarea.equals("crear")){
            txtAnuncio.setText("Crear Cuenta")
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
        }else{
            txtAnuncio.setText("Gestión de Cuenta")
            gridConsultar.visibility = View.VISIBLE
            if (tarea.equals("consultar")){
                btnSignUp.visibility = Button.GONE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                disableFields()
            }else if(tarea.equals("modificar")){
                btnSignUp.visibility = Button.GONE
                btnModificar.visibility = Button.VISIBLE
                btnEliminar.visibility = Button.GONE
            }else{
                btnSignUp.visibility = Button.GONE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.VISIBLE
                disableFields()
            }
        }

        btnSalir.setOnClickListener(){
            finish()
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

        btnModificar.setOnClickListener{

        }

        btnEliminar.setOnClickListener{

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

                    val intent = Intent(this@CrearCuenta, InterfazCliente::class.java)
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

    private fun disableFields(){
        txtNombre.isEnabled = false
        txtClave.isEnabled = false
        txtDocumento.isEnabled = false
        txtEdad.isEnabled = false
        txtDireccion.isEnabled = false
        txtTelefono.isEnabled = false
        txtCorreo.isEnabled = false
        spSexo.isEnabled = false
        spEscolaridad.isEnabled = false
        spGrupo.isEnabled = false
        spComunidad.isEnabled = false
        txtGrupoEtnico.isEnabled = false
    }

    private fun saltarBienvenida() {
        val intent = Intent(this@CrearCuenta, Bienvenida::class.java)
        finish()
        startActivity(intent)
    }
}