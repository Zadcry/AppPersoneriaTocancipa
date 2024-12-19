package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
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


class CrearAdmin : AppCompatActivity() {
    private lateinit var gridConsultar: LinearLayout
    private lateinit var txtAnuncio:TextView
    private lateinit var txtNombre: EditText
    private lateinit var txtConsultar: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtConfirmarClave: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var spEstado: Spinner
    private lateinit var txtCorreo: EditText
    private lateinit var btnConsultar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnTogglePassword: Button
    private lateinit var btnToggleCheckPassword: Button
    private lateinit var tvClave: TextView
    private lateinit var tvConfirmarClave: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var tarea: String = ""
    private var uidConsultado: String = ""

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_admin)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        spEstado = findViewById(R.id.spEstado)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesEstadoCargo,
            R.drawable.spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spEstado.adapter = adapter
        }
        gridConsultar = findViewById(R.id.gridConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtNombre = findViewById(R.id.txtNombre)
        txtClave = findViewById(R.id.txtClave)
        txtConfirmarClave = findViewById(R.id.txtConfirmarClave)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtDocumento = findViewById(R.id.txtDocumento)
        btnSalir = findViewById(R.id.btnSalir)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnModificar = findViewById(R.id.btnModificar)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleCheckPassword = findViewById(R.id.btnToggleCheckPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvClave = findViewById(R.id.tvClave)
        tvConfirmarClave = findViewById(R.id.tvConfirmarClave)
        tarea = intent.getStringExtra("tarea").toString()

        // Botón Ver Contraseña
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnTogglePassword.setOnClickListener { v: View? ->
            if (txtClave.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                txtClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtClave.setSelection(txtClave.text.length) // Mantener cursor al final
        }

        // Botón Ver Confirmar Contraseña
        btnToggleCheckPassword = findViewById(R.id.btnToggleCheckPassword)
        btnToggleCheckPassword.setOnClickListener { v: View? ->
            if (txtConfirmarClave.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                txtConfirmarClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtConfirmarClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtConfirmarClave.setSelection(txtConfirmarClave.text.length) // Mantener cursor al final
        }

        if(tarea == "crear"){
            txtAnuncio.text = "Crear Administrador"
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
        }else {
            txtAnuncio.text = "Gestión de Administrador"
            gridConsultar.visibility = View.VISIBLE
            when (tarea) {
                "consultar" -> {
                    btnSignUp.visibility = Button.GONE
                    btnModificar.visibility = Button.GONE
                    btnEliminar.visibility = Button.GONE
                    txtClave.visibility = EditText.GONE
                    btnTogglePassword.visibility = Button.GONE
                    txtConfirmarClave.visibility = EditText.GONE
                    btnToggleCheckPassword.visibility = Button.GONE
                    tvClave.visibility = TextView.GONE
                    tvConfirmarClave.visibility = TextView.GONE
                    disableFields()
                }
                "modificar" -> {
                    btnSignUp.visibility = Button.GONE
                    btnModificar.visibility = Button.GONE
                    btnEliminar.visibility = Button.GONE
                    txtClave.visibility = EditText.GONE
                    btnTogglePassword.visibility = Button.GONE
                    txtConfirmarClave.visibility = EditText.GONE
                    btnToggleCheckPassword.visibility = Button.GONE
                    tvClave.visibility = TextView.GONE
                    tvConfirmarClave.visibility = TextView.GONE
                }
                else -> {
                    btnSignUp.visibility = Button.GONE
                    btnModificar.visibility = Button.GONE
                    btnEliminar.visibility = Button.GONE
                    txtClave.visibility = EditText.GONE
                    btnTogglePassword.visibility = Button.GONE
                    txtConfirmarClave.visibility = EditText.GONE
                    btnToggleCheckPassword.visibility = Button.GONE
                    tvClave.visibility = TextView.GONE
                    tvConfirmarClave.visibility = TextView.GONE
                    disableFields()
                }
            }
        }
        btnSalir.setOnClickListener {
            finish()
        }

        btnSignUp.setOnClickListener {
            val campos = conseguirCampos()
            if (!verificarCampos(campos)) {
                Toast.makeText(
                    this@CrearAdmin,
                    "Diligencie todos los datos",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            } else{
                val nombre = campos[0]
                val clave = campos[1]
                val confirmarClave = campos[2]
                val documento = campos[3]
                val correo = campos[4]
                val estado = campos[5]

                if(clave != confirmarClave){
                    Toast.makeText(
                        this@CrearAdmin,
                        "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                }else{
                    signUp(nombre, clave, documento,
                        correo, estado)
                }
            }
        }

        btnConsultar.setOnClickListener{
            consultarPorCedula()
        }

        btnModificar.setOnClickListener{
            val campos = conseguirCampos()
            if(campos[3].isEmpty()){
                Toast.makeText(
                    this@CrearAdmin,
                    "Ingrese cédula para modificar",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }else{
                if (!verificarCampos(campos)) {
                    Toast.makeText(
                        this@CrearAdmin,
                        "Diligencie todos los datos",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                } else{
                    val nombre = campos[0]
                    val documento = campos[2]
                    val correo = campos[3]
                    val estado = campos[4]

                    mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                    mDbRef.child(uidConsultado).setValue(
                        Admin(documento,nombre,correo,
                            estado))
                    Toast.makeText(
                        this@CrearAdmin,
                        "Administrador modificado exitosamente",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }

        btnEliminar.setOnClickListener{
            if(uidConsultado.isEmpty()){
                Toast.makeText(
                    this@CrearAdmin,
                    "Ingrese cédula para eliminar",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }else{
                mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                mDbRef.child(uidConsultado).removeValue()
                Toast.makeText(
                    this@CrearAdmin,
                    "Administrador eliminado exitosamente",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }


    }
    private fun disableFields(){
        txtNombre.isEnabled = false
        txtClave.isEnabled = false
        txtConfirmarClave.isEnabled = false
        btnTogglePassword.isEnabled = false
        btnToggleCheckPassword.isEnabled = false
        txtDocumento.isEnabled = false
        txtCorreo.isEnabled = false
        spEstado.isEnabled = false
    }

    private fun signUp(
        nombre: String,
        clave:String,
        documento:String,
        correo:String,
        estado:String
    )
    {
        // Buscar en RealtimeDatabase si ya existe un usuario con el mismo documento
        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
        val query = mDbRef.orderByChild("cedula").equalTo(documento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(
                        this@CrearAdmin,
                        "Ya existe un usuario con la cédula ingresada",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    mAuth.createUserWithEmailAndPassword(correo,clave)
                        .addOnCompleteListener(this@CrearAdmin){
                                task ->
                            if (task.isSuccessful){
                                addUserToDatabase(nombre, documento, correo, estado, mAuth.currentUser?.uid!!)
                                Toast.makeText(
                                    this@CrearAdmin,
                                    "Administrador creado exitosamente",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                val intent = Intent(this@CrearAdmin, InterfazAdmin::class.java)
                                finish()
                                startActivity(intent)
                            }
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAdmin,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }
    private fun conseguirCampos(): Array<String> {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val confirmarClave = txtConfirmarClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val estado = spEstado.selectedItem.toString()

        return arrayOf(nombre, clave, confirmarClave, documento, correo, estado)
    }
    private fun verificarCampos(campos: Array<String>): Boolean {
        //Verifica que todos los campos estén diligenciados
        return !(campos[0].isEmpty() || campos[1].isEmpty() || campos[2].isEmpty() || campos[3].isEmpty() || campos[4].isEmpty())
    }

    private fun addUserToDatabase(nombre: String, documento: String, correo: String, estado: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("AdminData").child(uid).setValue(
            Admin(documento, nombre, correo, estado))
        Toast.makeText(
            this@CrearAdmin,
            "Cuenta creada exitosamente",
            Toast.LENGTH_SHORT,
        ).show()

    }

    private fun consultarPorCedula() {
        val cedula = txtConsultar.text.toString()
        if (cedula.isEmpty()) {
            Toast.makeText(
                this@CrearAdmin,
                "Ingrese cédula para consultar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")

        val query = mDbRef.orderByChild("cedula").equalTo(cedula)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        uidConsultado = it.key.toString()
                        println(it)
                        val nombre = it.child("nombreCompleto").value.toString()
                        println(nombre)
                        val documento = it.child("cedula").value.toString()
                        val estado = it.child("estado").value.toString()
                        val correo = it.child("correo").value.toString()

                        txtNombre.setText(nombre)
                        txtClave.setText("********")
                        txtDocumento.setText(documento)
                        txtCorreo.setText(correo)
                        spEstado.setSelection((spEstado.adapter as ArrayAdapter<String>).getPosition(estado))
                    }
                    if(tarea == "modificar"){
                        btnModificar.visibility = Button.VISIBLE
                    }else if(tarea == "eliminar"){
                        btnEliminar.visibility = Button.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@CrearAdmin,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAdmin,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }
}