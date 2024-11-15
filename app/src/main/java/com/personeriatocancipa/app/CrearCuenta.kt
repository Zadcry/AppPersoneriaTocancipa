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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.log

class CrearCuenta : AppCompatActivity() {
    //Crea variables de Layout

    private lateinit var gridConsultar: GridLayout
    private lateinit var txtConsultar: EditText
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
    private lateinit var btnConsultar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
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
        println(tarea)
        println(usuario)
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
        txtConsultar = findViewById(R.id.txtConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtNombre = findViewById(R.id.txtNombre)
        txtClave = findViewById(R.id.txtClave)
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

                signUp(nombre, clave, documento, edad,
                    direccion, telefono, correo, sexo, escolaridad,
                    grupo, siGrupo, comunidad)
            }
        }

        btnConsultar.setOnClickListener{
            consultarPorCedula()
        }

        btnModificar.setOnClickListener{
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
                        correo, sexo, escolaridad, siGrupo, grupo, comunidad, "0",uidConsultado))
                Toast.makeText(
                    this@CrearCuenta,
                    "Usuario modificado exitosamente",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        btnEliminar.setOnClickListener{
            mDbRef = FirebaseDatabase.getInstance().getReference("userData")
            mDbRef.child(uidConsultado).removeValue()
            Toast.makeText(
                this@CrearCuenta,
                "Usuario eliminado exitosamente",
                Toast.LENGTH_SHORT,
            ).show()
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

                    println(mAuth.currentUser?.uid)
                    if (usuario.equals("cliente")){
                        val intent = Intent(this@CrearCuenta, InterfazCliente::class.java)
                        finish()
                        startActivity(intent)
                    } else{
                        Toast.makeText(
                            this@CrearCuenta,
                            "Cuenta creada exitosamente",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
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

    private fun conseguirCampos(): Array<String> {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
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

        return arrayOf(nombre, clave, documento, edad, direccion, telefono, correo, sexo, escolaridad, grupo, comunidad, grupoEtnico)
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
                "Ingrese un número de cédula",
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
                        println(nombre)
                        val clave = it.child("clave").value.toString()
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
                } else {
                    Toast.makeText(
                        this@CrearCuenta,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_SHORT,
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