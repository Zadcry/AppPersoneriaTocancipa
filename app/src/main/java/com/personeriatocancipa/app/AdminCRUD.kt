package com.personeriatocancipa.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class AdminCRUD : AppCompatActivity() {
    private lateinit var spCRUD: Spinner
    private lateinit var spUsuario: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_crud)

        // Inicializar componentes
        spCRUD = findViewById(R.id.spCrud)
        spUsuario = findViewById(R.id.spRol)
        button = findViewById(R.id.btnCrear)
        recyclerView = findViewById(R.id.recyclerViewUsuarios)

        setupMainSpinner()
    }

    private fun setupMainSpinner() {
        // Opciones del primer Spinner
        val options = arrayOf("1", "2", "3", "4")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCRUD.adapter = adapter

        // Escuchar selecciones en el primer Spinner
        spCRUD.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (options[position]) {
                    "1" -> showSecondarySpinnerAndButton()
                    "2" -> showRecyclerViewForOption2()
                    "3" -> showRecyclerViewForOption3()
                    "4" -> hideAllComponents()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    // Opción 1: Mostrar otro Spinner y botón
    private fun showSecondarySpinnerAndButton() {
        spUsuario.visibility = View.VISIBLE
        button.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // Configurar segundo Spinner
        val secondOptions = arrayOf("Layout A", "Layout B")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, secondOptions)
        spUsuario.adapter = adapter

        button.setOnClickListener {
            val selectedLayout = spUsuario.selectedItem.toString()
            when (selectedLayout) {

            }
        }
    }

    // Opción 2: Mostrar RecyclerView con botones que cambian a un Layout
    private fun showRecyclerViewForOption2() {
        spUsuario.visibility = View.GONE
        button.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val buttonList = listOf("Go to Layout C", "Go to Layout D")
        val adapter = UsuariosAdapter(buttonList) { selectedButton ->
            when (selectedButton) {
            }
        }
        recyclerView.adapter = adapter
    }

    // Opción 3: Mostrar RecyclerView con botones que hacen algo (TODO)
    private fun showRecyclerViewForOption3() {
        spUsuario.visibility = View.GONE
        button.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val buttonList = listOf("Do Something 1", "Do Something 2")
        val adapter = UsuariosAdapter(buttonList) { selectedButton ->
            when (selectedButton) {
                "Do Something 1" -> {
                    // TODO: Agregar acción específica
                    Log.d("Action", "Do Something 1")
                }
                "Do Something 2" -> {
                    // TODO: Agregar acción específica
                    Log.d("Action", "Do Something 2")
                }
            }
        }
        recyclerView.adapter = adapter
    }

    // Método para cargar layouts dinámicamente


    // Opción 4: Ocultar todos los componentes
    private fun hideAllComponents() {
        spUsuario.visibility = View.GONE
        button.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }
}