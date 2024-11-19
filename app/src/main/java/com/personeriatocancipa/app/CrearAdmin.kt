package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
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
    private lateinit var txtAnuncio:EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtConsultar: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var spEstado: Spinner
    private lateinit var txtCorreo: EditText
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_admin)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        spEstado = findViewById(R.id.spEstado)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesEstado,
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
        txtCorreo = findViewById(R.id.txtCorreo)
        txtDocumento = findViewById(R.id.txtDocumento)
        btnSalir=findViewById(R.id.btnSalir)
        btnEliminar=findViewById(R.id.btnEliminar)
        btnConsultar=findViewById(R.id.btnConsultar)
        btnModificar=findViewById(R.id.btnModificar)
        btnSignUp=findViewById(R.id.btnSignUp)
        tarea=intent.getStringExtra("tarea").toString()

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
        btnSalir.setOnClickListener(){
            finish()
        }

        btnSignUp.setOnClickListener(){
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
                val documento = campos[2]
                val correo = campos[4]
                val estado = campos[5]

                signUp(nombre, clave, documento,
                    correo, estado)
            }
        }

        btnConsultar.setOnClickListener{
            consultarPorCedula()
        }

        btnModificar.setOnClickListener{
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
                val documento = campos[2]
                val correo = campos[4]
                val estado = campos[5]

                mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                mDbRef.child(uidConsultado).setValue(
                    Admin( documento,nombre,correo,
                        estado, uidConsultado))
                Toast.makeText(
                    this@CrearAdmin,
                    "Usuario modificado exitosamente",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        btnEliminar.setOnClickListener{
            mDbRef = FirebaseDatabase.getInstance().getReference("userData")
            mDbRef.child(uidConsultado).removeValue()
            Toast.makeText(
                this@CrearAdmin,
                "Usuario eliminado exitosamente",
                Toast.LENGTH_SHORT,
            ).show()
        }


    }
    private fun disableFields(){
        txtNombre.isEnabled = false
        txtClave.isEnabled = false
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
        mAuth.createUserWithEmailAndPassword(correo,clave)
            .addOnCompleteListener(this){
                    task ->
                if (task.isSuccessful){
                    addUserToDatabase(nombre, documento, correo, estado, mAuth.currentUser?.uid!!)

                    println(mAuth.currentUser?.uid)
                        val intent = Intent(this@CrearAdmin, InterfazAdmin::class.java)
                        finish()
                        startActivity(intent)
                        Toast.makeText(
                            this@CrearAdmin,
                            "Cuenta creada exitosamente",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
            }
    }
    private fun conseguirCampos(): Array<String> {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val estado = spEstado.selectedItem.toString()

        return arrayOf(nombre, clave, documento, correo, estado)
    }
    private fun verificarCampos(campos: Array<String>): Boolean {
        //Verifica que todos los campos estén diligenciados
        if (campos[0].isEmpty() || campos[1].isEmpty() || campos[2].isEmpty() || campos[3].isEmpty() || campos[4].isEmpty()) {
            return false
        }
        return true
    }


    private fun addUserToDatabase(nombre: String, documento: String, correo: String, estado: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("userData").child(uid).setValue(
            Admin(nombre, documento, correo, estado,uid))
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
                        val estado = it.child("estado").value.toString()
                        val correo = it.child("correo").value.toString()

                        txtNombre.setText(nombre)
                        txtClave.setText("********")
                        txtDocumento.setText(documento)
                        txtCorreo.setText(correo)
                        spEstado.setSelection((spEstado.adapter as ArrayAdapter<String>).getPosition(estado))
                    }
                } else {
                    Toast.makeText(
                        this@CrearAdmin,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_SHORT,
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