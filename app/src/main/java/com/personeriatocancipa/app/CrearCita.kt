package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CrearCita : AppCompatActivity() {

    private lateinit var txtConsultar: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var spAbogado: Spinner
    private lateinit var spTipo: Spinner
    private lateinit var btnHorarios: Button
    private lateinit var btnSeleccionar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnCrear: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var txtFecha: TextView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cita)

        // Buscar elementos de layout
        txtConsultar = findViewById(R.id.txtConsultar)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        btnHorarios = findViewById(R.id.btnHorarios)
        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnSalir = findViewById(R.id.btnSalir)
        btnCrear = findViewById(R.id.btnCrear)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        txtFecha = findViewById(R.id.txtFecha)

        // Tipo de cita
        spTipo = findViewById(R.id.spTipo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTipoCita,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTipo.adapter = adapter
        }

        // Abogado
        spAbogado = findViewById(R.id.spAbogado)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesConsultorio,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spAbogado.adapter = adapter
        }

        btnSalir.setOnClickListener{
            finish()
        }
    }
}