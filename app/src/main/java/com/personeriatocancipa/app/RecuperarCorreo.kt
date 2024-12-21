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
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class RecuperarCorreo : AppCompatActivity() {

    private lateinit var txtCedula: EditText
    private lateinit var txtClave: EditText
    private lateinit var tvClave: TextView
    private lateinit var btnCedula: Button
    private lateinit var btnRestablecer: Button
    private lateinit var btnVolver: Button
    private lateinit var btnTogglePassword: Button
    private lateinit var gridPassword: GridLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_correo)

        mAuth = FirebaseAuth.getInstance()

        // Obtiene valores de Layout
        txtCedula = findViewById(R.id.txtCedula)
        txtClave = findViewById(R.id.txtClave)
        tvClave = findViewById(R.id.tvClave)
        btnCedula = findViewById(R.id.btnCedula)
        btnRestablecer = findViewById(R.id.btnRestablecer)
        btnVolver = findViewById(R.id.btnVolver)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        gridPassword = findViewById(R.id.gridPassword)

        // Oculta campos inicialmente
        tvClave.visibility = TextView.GONE
        gridPassword.visibility = GridLayout.GONE
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
                            gridPassword.visibility = GridLayout.VISIBLE
                            btnRestablecer.visibility = Button.VISIBLE
                            Toast.makeText(this@RecuperarCorreo,
                                "Usuario encontrado",
                                Toast.LENGTH_SHORT).show()
                        }else{
                            // Busca en Abogados
                            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                            query = mDbRef.orderByChild("documento").equalTo(txtCedula.text.toString())
                            query.addListenerForSingleValueEvent( object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        tvClave.visibility = TextView.VISIBLE
                                        gridPassword.visibility = GridLayout.VISIBLE
                                        btnRestablecer.visibility = Button.VISIBLE
                                        Toast.makeText(this@RecuperarCorreo,
                                            "Abogado encontrado",
                                            Toast.LENGTH_SHORT).show()
                                    }else{
                                        // Verificar si el correo está registrado en Admins.
                                        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                                        query = mDbRef.orderByChild("cedula").equalTo(txtCedula.text.toString())
                                        query.addListenerForSingleValueEvent( object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()){
                                                    tvClave.visibility = TextView.VISIBLE
                                                    gridPassword.visibility = GridLayout.VISIBLE
                                                    btnRestablecer.visibility = Button.VISIBLE
                                                    Toast.makeText(this@RecuperarCorreo,
                                                        "Administrador encontrado",
                                                        Toast.LENGTH_SHORT).show()
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

        // Crea eventListener para clicks en "Restablecer"
        btnRestablecer.setOnClickListener(){
            val correo = txtCedula.text.toString()
            if(correo.isEmpty()){
                Toast.makeText(this, "Ingrese un correo", Toast.LENGTH_SHORT).show()
            }else{
                // Verificar si el correo está registrado en Admin.
                mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                var query = mDbRef.orderByChild("correo").equalTo(correo)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Si encuentra en Admin.
                            restablecer(correo)
                        } else {
                            // Si no es Admin.
                            // Verificar si el correo está registrado en Abogados
                            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                            query = mDbRef.orderByChild("correo").equalTo(correo)
                            query.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // Si encuentra en Abogados
                                        restablecer(correo)
                                    } else {
                                        // Si no es Abogado
                                        // Verificar si el correo está registrado en Cliente
                                        mDbRef = FirebaseDatabase.getInstance().getReference("userData")
                                        query = mDbRef.orderByChild("correo").equalTo(correo)
                                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    restablecer(correo)
                                                } else {
                                                    Toast.makeText(
                                                        this@RecuperarCorreo,
                                                        "Correo no registrado",
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

    private fun restablecer(correo: String) {
        mAuth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email sent
                    Toast.makeText(this, "Correo enviado. Revise su bandeja de entrada o su carpeta de 'No Deseados'", Toast.LENGTH_LONG).show()
                } else {
                    // Email not sent
                    Toast.makeText(this, "Error al enviar correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}