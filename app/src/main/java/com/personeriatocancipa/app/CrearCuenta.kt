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
import com.google.firebase.auth.FirebaseAuth

class CrearCuenta : AppCompatActivity() {
    //Crea variables de Layout

    private lateinit var txtNombre: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var txtEdad: EditText
    private lateinit var txtDireccion: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var spSexo: Spinner
    private lateinit var spEscolaridad: Spinner
    private lateinit var spGrupo: Spinner
    private lateinit var btnSalir: Button
    private lateinit var mAuth: FirebaseAuth


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)
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
        txtDocumento = findViewById(R.id.txtDocumento)
        txtEdad = findViewById(R.id.txtEdad)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtCorreo = findViewById(R.id.txtCorreo)
        btnSalir = findViewById(R.id.btnSalir)

        btnSalir.setOnClickListener(){
            saltarBienvenida()
        }

    }

    private fun saltarBienvenida() {
        val intent = Intent(this@CrearCuenta, Bienvenida::class.java)
        finish()
        startActivity(intent)
    }
}