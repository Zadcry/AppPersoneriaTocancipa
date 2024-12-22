package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
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
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class RecuperarCorreo : AppCompatActivity() {

    private lateinit var txtCedula: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtNuevoCorreo: EditText
    private lateinit var tvClave: TextView
    private lateinit var tvNuevoCorreo: TextView
    private lateinit var btnCedula: Button
    private lateinit var btnRestablecer: Button
    private lateinit var btnVolver: Button
    private lateinit var btnTogglePassword: Button
    private lateinit var btnClave: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var tipoUsuario: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_correo)

        mAuth = FirebaseAuth.getInstance()

        // Obtiene valores de Layout
        txtCedula = findViewById(R.id.txtCedula)
        txtClave = findViewById(R.id.txtClave)
        txtNuevoCorreo = findViewById(R.id.txtNuevoCorreo)
        tvClave = findViewById(R.id.tvClave)
        tvNuevoCorreo = findViewById(R.id.tvNuevoCorreo)
        btnCedula = findViewById(R.id.btnCedula)
        btnRestablecer = findViewById(R.id.btnRestablecer)
        btnVolver = findViewById(R.id.btnVolver)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnClave = findViewById(R.id.btnClave)

        // Oculta campos inicialmente
        tvClave.visibility = TextView.GONE
        txtClave.visibility = EditText.GONE
        btnTogglePassword.visibility = Button.GONE
        btnClave.visibility = Button.GONE
        tvNuevoCorreo.visibility = TextView.GONE
        txtNuevoCorreo.visibility = EditText.GONE
        btnRestablecer.visibility = Button.GONE

        // Botón Ver Contraseña
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

        // Verifica existencia de cuenta asociada a cédula
        btnCedula.setOnClickListener {
            if(txtCedula.text.isEmpty()){
                Toast.makeText(this@RecuperarCorreo,
                    "Ingrese cédula para consultar",
                    Toast.LENGTH_SHORT).show()
            }else{
                // Verificar si el correo está registrado en Usuarios
                mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                var query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                query.addListenerForSingleValueEvent( object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            tvClave.visibility = TextView.VISIBLE
                            txtClave.visibility = EditText.VISIBLE
                            btnTogglePassword.visibility = Button.VISIBLE
                            btnClave.visibility = Button.VISIBLE
                            Toast.makeText(this@RecuperarCorreo,
                                "Usuario encontrado",
                                Toast.LENGTH_SHORT).show()
                            tipoUsuario = "usuario"
                        }else{
                            // Busca en Abogados
                            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                            query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                            query.addListenerForSingleValueEvent( object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        tvClave.visibility = TextView.VISIBLE
                                        txtClave.visibility = EditText.VISIBLE
                                        btnTogglePassword.visibility = Button.VISIBLE
                                        btnClave.visibility = Button.VISIBLE
                                        Toast.makeText(this@RecuperarCorreo,
                                            "Abogado encontrado",
                                            Toast.LENGTH_SHORT).show()
                                        tipoUsuario = "abogado"
                                    }else{
                                        // Verificar si el correo está registrado en Admins.
                                        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                                        query = mDbRef.orderByChild("cedula").equalTo(txtCedula.text.toString())
                                        query.addListenerForSingleValueEvent( object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()){
                                                    tvClave.visibility = TextView.VISIBLE
                                                    txtClave.visibility = EditText.VISIBLE
                                                    btnTogglePassword.visibility = Button.VISIBLE
                                                    btnClave.visibility = Button.VISIBLE
                                                    Toast.makeText(this@RecuperarCorreo,
                                                        "Administrador encontrado",
                                                        Toast.LENGTH_SHORT).show()
                                                    tipoUsuario = "admin"
                                                }else{
                                                    Toast.makeText(this@RecuperarCorreo,
                                                        "No se encontró una cuenta asociada a esa cédula",
                                                        Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Toast.makeText(this@RecuperarCorreo,
                                                    "Error al consultar abse de datos: $error",
                                                    Toast.LENGTH_SHORT).show()
                                            }

                                        }

                                        )
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@RecuperarCorreo,
                                        "Error al consultar abse de datos: $error",
                                        Toast.LENGTH_SHORT).show()
                                }

                            }

                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@RecuperarCorreo,
                            "Error al consultar abse de datos: $error",
                            Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

        btnClave.setOnClickListener{
            val clave = txtClave.text.toString()
            if(clave.isEmpty()){
                Toast.makeText(this, "Digite su contraseña", Toast.LENGTH_SHORT).show()
            }else{
                // Verificar si la contraseña está asociada a esa cuenta
                mAuth = FirebaseAuth.getInstance()
                val query: Query

                when (tipoUsuario) {
                    "usuario" -> {
                        mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                        query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                    }

                    "abogado" -> {
                        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                        query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                    }

                    else -> {
                        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                        query = mDbRef.orderByChild("cedula").equalTo(txtCedula.text.toString())
                    }
                }
                // Busca en la base de datos el correo asociado a la cédula
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                val correo = it.child("correo").value.toString()
                                mAuth.signInWithEmailAndPassword(correo, clave)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            tvNuevoCorreo.visibility = TextView.VISIBLE
                                            txtNuevoCorreo.visibility = EditText.VISIBLE
                                            btnRestablecer.visibility = Button.VISIBLE
                                            Toast.makeText(
                                                this@RecuperarCorreo,
                                                "Contraseña correcta",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@RecuperarCorreo,
                                                "Contraseña incorrecta",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this@RecuperarCorreo,
                                "No se encontró una cuenta asociada a esa cédula",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@RecuperarCorreo,
                            "Error al consultar la base de datos",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                })
            }
        }

        btnRestablecer.setOnClickListener{
            val nuevoCorreo = txtNuevoCorreo.text.toString()
            if(nuevoCorreo.isEmpty()){
                Toast.makeText(this, "Digite su nuevo correo", Toast.LENGTH_SHORT).show()
            }else{
                // Verificar si el correo está asociado a esa cuenta
                mAuth = FirebaseAuth.getInstance()
                val query: Query

                when (tipoUsuario) {
                    "usuario" -> {
                        mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                        query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                    }

                    "abogado" -> {
                        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                        query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                    }

                    else -> {
                        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                        query = mDbRef.orderByChild("cedula").equalTo(txtCedula.text.toString())
                    }
                }
                // Busca en la base de datos el correo asociado a la cédula
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach { _ ->
                                mAuth.currentUser!!.updateEmail(nuevoCorreo)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@RecuperarCorreo,
                                                "Correo actualizado",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            finish()
                                        } else {
                                            println(task.exception) 
                                            Toast.makeText(
                                                this@RecuperarCorreo,
                                                "Error al actualizar correo",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            Toast.makeText(
                                                this@RecuperarCorreo,
                                                "${mAuth.currentUser!!.email}",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            Toast.makeText(
                                                this@RecuperarCorreo,
                                                "${task.exception}",
                                                Toast.LENGTH_LONG,
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this@RecuperarCorreo,
                                "No se encontró una cuenta asociada a esa cédula",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@RecuperarCorreo,
                            "Error al consultar la base de datos",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                })
            }
        }

        // Crea eventListener para clicks en "Volver"
        btnVolver.setOnClickListener(){
            finish()
        }
    }
}