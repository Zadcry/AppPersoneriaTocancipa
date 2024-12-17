package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CrearCuenta : AppCompatActivity() {

    //Crea variables de Layout
    private lateinit var gridConsultar: LinearLayout
    private lateinit var txtConsultar: EditText
    private lateinit var txtAnuncio: TextView
    private lateinit var txtNombre: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtConfirmarClave: EditText
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
    private var usuario: String = ""
    private var uidConsultado: String = ""

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        tarea = intent.getStringExtra("tarea").toString()
        usuario = intent.getStringExtra("usuario").toString()
        // Manejo valores de Combo Box

        // Sexo
        spSexo = findViewById(R.id.spSexo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesSexo,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spSexo.adapter = adapter
        }

        // Escolaridad
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
        spGrupo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        }

        // Comunidad Vulnerable
        spComunidad = findViewById(R.id.spComunidad)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesComunidad,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spComunidad.adapter = adapter
        }


        txtClave = findViewById(R.id.txtClave)
        txtConfirmarClave = findViewById(R.id.txtConfirmarClave)

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


        //Obtiene demás elementos de Layout
        gridConsultar = findViewById(R.id.gridConsultar)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtNombre = findViewById(R.id.txtNombre)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtEdad = findViewById(R.id.txtEdad)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtGrupoEtnico = findViewById(R.id.txtGrupoEtnico)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnSalir = findViewById(R.id.btnSalir)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        tvClave = findViewById(R.id.tvClave)
        tvConfirmarClave = findViewById(R.id.tvConfirmarClave)

        if(tarea == "crear"){
            txtAnuncio.text = "Crear Cuenta"
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
        }else{
            txtAnuncio.text = "Gestión de Cuenta"
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
                    this@CrearCuenta,
                    "Diligencie todos los datos",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            } else{
                val nombre = campos[0]
                val clave = campos[1]
                val confirmarClave = campos[2]
                val documento = campos[3]
                val edad = campos[4].toInt()
                val direccion = campos[5]
                val telefono = campos[6]
                val correo = campos[7]
                val sexo = campos[8]
                val escolaridad = campos[9]
                val grupo = campos[10]
                val siGrupo = campos[11]
                val comunidad = campos[12]

                if(clave != confirmarClave){
                    Toast.makeText(
                        this@CrearCuenta,
                        "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                }else{
                    signUp(nombre, clave, documento, edad,
                        direccion, telefono, correo, sexo, escolaridad,
                        grupo, siGrupo, comunidad)
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
                    this@CrearCuenta,
                    "Ingrese cédula para modificar",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }else{
                if (!verificarCampos(campos)) {
                    Toast.makeText(
                        this@CrearCuenta,
                        "Diligencie todos los datos",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                }else{
                    val nombre = campos[0]
                    val documento = campos[2]
                    val edad = campos[3].toInt()
                    val direccion = campos[4]
                    val telefono = campos[5]
                    val correo = campos[6]
                    val sexo = campos[7]
                    val escolaridad = campos[8]
                    val grupo = campos[9]
                    val siGrupo = campos[10]
                    val comunidad = campos[11]

                    mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                    mDbRef.child(uidConsultado).setValue(
                        Usuario(nombre, documento, edad, direccion, telefono,
                            correo, sexo, escolaridad, siGrupo, grupo, comunidad,uidConsultado))
                    Toast.makeText(
                        this@CrearCuenta,
                        "Usuario modificado exitosamente",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }

        btnEliminar.setOnClickListener{
            if(uidConsultado.isEmpty()){
                Toast.makeText(
                    this@CrearCuenta,
                    "Ingrese cédula para eliminar",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }else{
                mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                mDbRef.child(uidConsultado).removeValue()
                Toast.makeText(
                    this@CrearCuenta,
                    "Usuario eliminado exitosamente",
                    Toast.LENGTH_SHORT,
                ).show()
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

        // Buscar en RealtimeDatabase si ya existe un usuario con el mismo documento
        mDbRef = FirebaseDatabase.getInstance().getReference("userData")
        val query = mDbRef.orderByChild("documento").equalTo(documento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(
                        this@CrearCuenta,
                        "Ya existe un usuario con el documento ingresado",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    mAuth.createUserWithEmailAndPassword(correo, clave)
                        .addOnCompleteListener(this@CrearCuenta) { task ->
                            if (task.isSuccessful) {
                                addUserToDatabase(
                                    nombre, documento, edad, direccion,
                                    telefono, correo, sexo, escolaridad, grupo,
                                    siGrupo, comunidad, mAuth.currentUser?.uid!!
                                )

                                println(mAuth.currentUser?.uid)
                                if (usuario == "cliente") {
                                    val intent = Intent(this@CrearCuenta, InterfazCliente::class.java)
                                    finish()
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@CrearCuenta,
                                        "Cuenta creada exitosamente",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@CrearCuenta,
                                    "Ha ocurrido un error",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearCuenta,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
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
                correo, sexo, escolaridad, siGrupo, grupo, comunidad,uid))
        Toast.makeText(
            this@CrearCuenta,
            "Cuenta creada exitosamente",
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun conseguirCampos(): Array<String> {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val confirmarClave = txtConfirmarClave.text.toString()
        val documento = txtDocumento.text.toString()
        val edad = txtEdad.text.toString()
        val direccion = txtDireccion.text.toString()
        val telefono = txtTelefono.text.toString()
        val correo = txtCorreo.text.toString()
        val sexo = spSexo.selectedItem.toString()
        val escolaridad = spEscolaridad.selectedItem.toString()
        val grupo = spGrupo.selectedItem.toString()
        val comunidad = spComunidad.selectedItem.toString()
        val grupoEtnico = txtGrupoEtnico.text.toString()

        return arrayOf(nombre, clave, confirmarClave, documento, edad, direccion, telefono, correo, sexo, escolaridad, grupo, grupoEtnico, comunidad)
    }

    private fun verificarCampos(campos: Array<String>): Boolean {
       //Verifica que todos los campos estén diligenciados
        // Si el grupo étnico es "Sí", se debe diligenciar el campo de grupo étnico
        if (campos[0].isEmpty() || campos[1].isEmpty() || campos[2].isEmpty() || campos[3].isEmpty() || campos[4].isEmpty() || campos[5].isEmpty() || campos[6].isEmpty()) {
            return false
        } else if (campos[9] == "Sí" && campos[11].isEmpty()) {
            return false
        }
        return true
    }

    private fun disableFields(){
        txtNombre.isEnabled = false
        txtClave.isEnabled = false
        txtConfirmarClave.isEnabled = false
        btnTogglePassword.isEnabled = false
        btnToggleCheckPassword.isEnabled = false
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

    private fun consultarPorCedula() {
        val cedula = txtConsultar.text.toString()
        if (cedula.isEmpty()) {
            Toast.makeText(
                this@CrearCuenta,
                "Ingrese cédula para consultar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        mDbRef = FirebaseDatabase.getInstance().getReference("userData")

        val query = mDbRef.orderByChild("documento").equalTo(cedula)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        uidConsultado = it.key.toString()
                        println(it)
                        val nombre = it.child("nombreCompleto").value.toString()
                        val documento = it.child("documento").value.toString()
                        val edad = it.child("edad").value.toString()
                        val direccion = it.child("direccion").value.toString()
                        val telefono = it.child("telefono").value.toString()
                        val correo = it.child("correo").value.toString()
                        val sexo = it.child("sexo").value.toString()
                        val escolaridad = it.child("escolaridad").value.toString()
                        val grupo = it.child("grupo").value.toString()
                        val grupoSi = it.child("grupoSi").value.toString()
                        val comunidad = it.child("comunidad").value.toString()

                        txtNombre.setText(nombre)
                        txtClave.setText("********")
                        txtDocumento.setText(documento)
                        txtEdad.setText(edad)
                        txtDireccion.setText(direccion)
                        txtTelefono.setText(telefono)
                        txtCorreo.setText(correo)
                        spSexo.setSelection((spSexo.adapter as ArrayAdapter<String>).getPosition(sexo))
                        spEscolaridad.setSelection((spEscolaridad.adapter as ArrayAdapter<String>).getPosition(escolaridad))
                        spGrupo.setSelection((spGrupo.adapter as ArrayAdapter<String>).getPosition(grupo))
                        spComunidad.setSelection((spComunidad.adapter as ArrayAdapter<String>).getPosition(comunidad))
                        txtGrupoEtnico.setText(grupoSi)
                    }
                    if(tarea == "modificar"){
                        btnModificar.visibility = Button.VISIBLE
                    }else if(tarea == "eliminar"){
                        btnEliminar.visibility = Button.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@CrearCuenta,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearCuenta,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })

    }
}