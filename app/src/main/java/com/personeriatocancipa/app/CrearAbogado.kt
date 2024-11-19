package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.os.Bundle
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

class CrearAbogado : AppCompatActivity() {

    private lateinit var gridConsultar: LinearLayout
    private lateinit var txtConsultar: EditText
    private lateinit var txtAnuncio: TextView
    private lateinit var txtNombre: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var spCargo: Spinner
    private lateinit var spTema: Spinner
    private lateinit var spEstado: Spinner
    private lateinit var btnConsultar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
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
        txtDocumento = findViewById(R.id.txtDocumento)
        txtCorreo = findViewById(R.id.txtCorreo)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnSalir = findViewById(R.id.btnSalir)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)

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
            R.array.opcionesEstado,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spEstado.adapter = adapter
        }

        if(tarea.equals("crear")){
            txtAnuncio.setText("Crear Abogado")
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
        }else {
            txtAnuncio.setText("Gestión de Abogado")
            gridConsultar.visibility = View.VISIBLE
            if (tarea.equals("consultar")) {
                btnSignUp.visibility = Button.GONE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.GONE
                disableFields()
            } else if (tarea.equals("modificar")) {
                btnSignUp.visibility = Button.GONE
                btnModificar.visibility = Button.VISIBLE
                btnEliminar.visibility = Button.GONE
            } else {
                btnSignUp.visibility = Button.GONE
                btnModificar.visibility = Button.GONE
                btnEliminar.visibility = Button.VISIBLE
                disableFields()
            }
        }

        btnSalir.setOnClickListener {
            finish()
        }

        btnConsultar.setOnClickListener {
            consultarPorCedula()
        }

        btnSignUp.setOnClickListener {
            crearAbogado()
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
                "Ingrese un número de cédula",
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
                        val clave = it.child("clave").value.toString()
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
                } else {
                    Toast.makeText(
                        this@CrearAbogado,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_SHORT,
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
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val cargo = spCargo.selectedItem.toString()
        val tema = spTema.selectedItem.toString()
        val estado = spEstado.selectedItem.toString()

        if (nombre.isEmpty() || clave.isEmpty() || documento.isEmpty() || correo.isEmpty()) {
            Toast.makeText(
                this@CrearAbogado,
                "Por favor complete todos los campos",
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
                                "Error al crear el usuario",
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

        if (nombre.isEmpty() || clave.isEmpty() || documento.isEmpty() || correo.isEmpty()) {
            Toast.makeText(
                this@CrearAbogado,
                "Por favor complete todos los campos",
                Toast.LENGTH_SHORT,
            ).show()
            return
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