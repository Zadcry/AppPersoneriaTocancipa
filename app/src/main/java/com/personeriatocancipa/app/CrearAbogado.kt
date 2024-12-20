package com.personeriatocancipa.app

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CrearAbogado : AppCompatActivity() {

    private lateinit var gridConsultar: LinearLayout
    private lateinit var txtConsultar: EditText
    private lateinit var txtAnuncio: TextView
    private lateinit var txtNombre: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtConfirmarClave: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var spCargo: Spinner
    private lateinit var spTema: Spinner
    private lateinit var spEstado: Spinner
    private lateinit var btnConsultar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnTogglePassword: Button
    private lateinit var btnToggleCheckPassword: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvClave: TextView
    private lateinit var tvConfirmarClave: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var tarea: String = ""
    private var uidConsultado: String = ""

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_abogado)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        tarea = intent.getStringExtra("tarea").toString()

        gridConsultar = findViewById(R.id.gridConsultar)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtNombre = findViewById(R.id.txtNombre)
        txtClave = findViewById(R.id.txtClave)
        txtConfirmarClave = findViewById(R.id.txtConfirmarClave)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtCorreo = findViewById(R.id.txtCorreo)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnSalir = findViewById(R.id.btnSalir)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleCheckPassword = findViewById(R.id.btnToggleCheckPassword)
        tvClave = findViewById(R.id.tvClave)
        tvConfirmarClave = findViewById(R.id.tvConfirmarClave)

        spCargo = findViewById(R.id.spCargo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesCargoAbogado,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spCargo.adapter = adapter
        }

        spTema = findViewById(R.id.spTema)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTemaAbogado,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTema.adapter = adapter
        }

        spEstado = findViewById(R.id.spEstado)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesEstadoCargo,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spEstado.adapter = adapter
        }


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
            txtAnuncio.text = "Crear Abogado"
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
        }else {
            txtAnuncio.text = "Gestión de Abogado"
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

        btnConsultar.setOnClickListener {
            consultarPorCedula()
        }

        btnSignUp.setOnClickListener {
            // Crear y configurar el diálogo inicial
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permiso de Habeas Data")
            builder.setMessage("Al crear una cuenta, autorizas el tratamiento de tus datos personales conforme a nuestra política de privacidad. ¿Aceptas continuar?")

            // Agregar botones de acción
            builder.setPositiveButton("Acepto") { dialog, which ->
                // Continuar con la creación de la cuenta
                crearAbogado()
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                // Cancelar el flujo
                dialog.dismiss()
            }

            // Mostrar el diálogo de permiso de habeas data
            builder.create().show()

        }

        btnModificar.setOnClickListener {
            modificarAbogado()
        }

        btnEliminar.setOnClickListener {
            eliminarAbogado()
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
        spCargo.isEnabled = false
        spTema.isEnabled = false
        spEstado.isEnabled = false
    }

    private fun consultarPorCedula() {
        val cedula = txtConsultar.text.toString()
        if (cedula.isEmpty()) {
            Toast.makeText(
                this@CrearAbogado,
                "Ingrese cédula para consultar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")

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
                        val documento = it.child("documento").value.toString()
                        val correo = it.child("correo").value.toString()
                        val cargo = it.child("cargo").value.toString()
                        val tema = it.child("tema").value.toString()
                        val estado = it.child("estado").value.toString()

                        txtNombre.setText(nombre)
                        txtClave.setText("********")
                        txtDocumento.setText(documento)
                        txtCorreo.setText(correo)
                        spCargo.setSelection((spCargo.adapter as ArrayAdapter<String>).getPosition(cargo))
                        spTema.setSelection((spTema.adapter as ArrayAdapter<String>).getPosition(tema))
                        spEstado.setSelection((spEstado.adapter as ArrayAdapter<String>).getPosition(estado))
                    }
                    if(tarea == "modificar"){
                        btnModificar.visibility = Button.VISIBLE
                    }else if(tarea == "eliminar"){
                        btnEliminar.visibility = Button.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@CrearAbogado,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }

    private fun crearAbogado() {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val confirmarClave = txtConfirmarClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val cargo = spCargo.selectedItem.toString()
        val tema = spTema.selectedItem.toString()
        val estado = spEstado.selectedItem.toString()

        if (nombre.isEmpty() || clave.isEmpty() || documento.isEmpty() || correo.isEmpty()) {
            Toast.makeText(
                this@CrearAbogado,
                "Diligencie todos los datos",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        // Verificar contraseñas iguales
        if (clave != confirmarClave) {
            Toast.makeText(
                this@CrearAbogado,
                "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        // Buscar en RealtimeDatabase si ya existe un usuario con el mismo documento

        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        val query = mDbRef.orderByChild("documento").equalTo(documento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(
                        this@CrearAbogado,
                        "Ya existe un abogado con el mismo documento",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    mAuth.createUserWithEmailAndPassword(correo, clave).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = mAuth.currentUser
                            val uid = user?.uid
                            val abogado = Abogado(documento, nombre, cargo, tema, correo, estado)
                            mDbRef.child(uid!!).setValue(abogado).addOnSuccessListener {
                                Toast.makeText(
                                    this@CrearAbogado,
                                    "Abogado creado exitosamente",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@CrearAbogado,
                                    "Error al crear el abogado",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CrearAbogado,
                                "Error al crear el abogado",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }

    private fun modificarAbogado() {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val cargo = spCargo.selectedItem.toString()
        val tema = spTema.selectedItem.toString()
        val estado = spEstado.selectedItem.toString()

        if(documento.isEmpty()){
            Toast.makeText(
                this@CrearAbogado,
                "Ingrese cédula para modificar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }else{
            if (nombre.isEmpty() || clave.isEmpty() || correo.isEmpty()) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Diligencie todos los datos",
                    Toast.LENGTH_SHORT,
                ).show()
                return
            }
        }

        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        val abogado = Abogado(documento, nombre, cargo, tema, correo, estado)
        mDbRef.child(uidConsultado).setValue(abogado).addOnSuccessListener {
            Toast.makeText(
                this@CrearAbogado,
                "Abogado modificado exitosamente",
                Toast.LENGTH_SHORT,
            ).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@CrearAbogado,
                "Error al modificar el abogado",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun eliminarAbogado() {
        if(uidConsultado.isEmpty()){
            Toast.makeText(
                this@CrearAbogado,
                "Ingrese cédula para eliminar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        mDbRef.child(uidConsultado).removeValue().addOnSuccessListener {
            Toast.makeText(
                this@CrearAbogado,
                "Abogado eliminado exitosamente",
                Toast.LENGTH_SHORT,
            ).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@CrearAbogado,
                "Error al eliminar el abogado",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}