package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Clase que gestiona la interfaz de CRUD
class CRUD : AppCompatActivity() {

    // Declaración de variables para elementos de la interfaz
    private var tipo: String = "" // Variable para almacenar el tipo de cuenta
    private lateinit var btnCrear: Button // Botón para crear un nuevo registro
    private lateinit var btnConsultar: Button // Botón para consultar registros
    private lateinit var btnModificar: Button // Botón para modificar cuando sujeto es un usuario
    private lateinit var btnModificarGrid: Button // Botón para modificar cuando sujeto es una cita
    private lateinit var btnEliminar: Button // Botón para eliminar un registro
    private lateinit var btnSalir: Button // Botón para salir de la interfaz
    private lateinit var txtTitulo: TextView // Texto para mostrar el título de la interfaz
    private lateinit var spTipoCuenta: Spinner // Spinner para seleccionar el tipo de cuenta
    private lateinit var gridEliminar: GridLayout // GridLayout para eliminar registros
    private lateinit var gridModificar: GridLayout // GridLayout para modificar registros
    private lateinit var gridModificarGrid: GridLayout // GridLayout para modificar registros de citas

    // Función que se ejecuta al crear la interfaz
    @SuppressLint("ResourceType", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        // Inicialización de variables para elementos de la interfaz
        btnCrear = findViewById(R.id.btnCrear)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnModificar = findViewById(R.id.btnModificar)
        btnModificarGrid = findViewById(R.id.btnModificarGrid)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnSalir = findViewById(R.id.btnSalir)
        txtTitulo = findViewById(R.id.txtTitulo)
        gridEliminar = findViewById(R.id.gridEliminar)
        gridModificar = findViewById(R.id.gridModificar)
        gridModificarGrid = findViewById(R.id.gridModificarGrid)

        // Adaptador para el spinner de tipo de cuenta
        spTipoCuenta = findViewById(R.id.spTipoCuenta)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTipoCuenta, // Array de opciones
            R.drawable.spinner_item // Estilo de los elementos
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item) // Estilo de los elementos desplegables
            spTipoCuenta.adapter = adapter
        }

        tipo = intent.getStringExtra("tipo").toString() // Permite saber si se gestiona un usuario o una cita

        // Se muestra u oculta elementos según el valor de tipo
        if (tipo == "usuario"){
            // Se muestran estos elementos cuando se quiere gestionar usuarios
            txtTitulo.text = "Gestión de Usuarios"
            spTipoCuenta.visibility = Spinner.VISIBLE
            gridEliminar.visibility = GridLayout.GONE
            gridModificar.visibility = GridLayout.VISIBLE
            gridModificarGrid.visibility = GridLayout.GONE
        }else{
            // Se muestran estos elementos cuando se quiere gestionar citas
            txtTitulo.text = "Gestión de Citas"
            spTipoCuenta.visibility = Spinner.INVISIBLE
            gridEliminar.visibility = GridLayout.VISIBLE
            gridModificarGrid.visibility = GridLayout.VISIBLE
            gridModificar.visibility = GridLayout.GONE
        }

        // Evento del botón crear
        btnCrear.setOnClickListener{
            if(tipo == "usuario"){ // Si se gestiona un usuario
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){ // Si se selecciona administrador
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){ // Si se selecciona abogado
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){ // Si se selecciona cliente
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin") // Se envía el tipo de usuario
                } else {
                    // Muestra un mensaje si no se selecciona un tipo de cuenta
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }
            }else{
                // Si se gestiona una cita
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "crear") // Se envía la tarea a realizar
            startActivity(intent)
        }

        // Evento del botón consultar
        btnConsultar.setOnClickListener{
            if(tipo == "usuario"){ // Si se gestiona un usuario
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){ // Si se selecciona administrador
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){ // Si se selecciona abogado
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){ // Si se selecciona cliente
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin")
                } else {
                    // Muestra un mensaje si no se selecciona un tipo de cuenta
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }
            }else{ // Si se gestiona una cita
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "consultar") // Se envía la tarea a realizar
            startActivity(intent)
        }

        // Evento del botón modificar (Cuando se modifica un usuario)
        btnModificar.setOnClickListener{
            modificar() // Se llama a la función modificar
        }

        // Evento del botón modificar (Cuando se modifica una cita)
        btnModificarGrid.setOnClickListener{
            modificar() // Se llama a la función modificar
        }

        // Evento del botón eliminar
        btnEliminar.setOnClickListener{
            if(tipo == "usuario"){ // Si se gestiona un usuario
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){ // Si se selecciona administrador
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){ // Si se selecciona abogado
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){ // Si se selecciona cliente
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin")
                } else { // Si no se selecciona un tipo de cuenta
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }
            }else{ // Si se gestiona una cita
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "eliminar") // Se envía la tarea a realizar
            startActivity(intent)
        }

        // Evento del botón salir
        btnSalir.setOnClickListener{
            // Se redirige a la interfaz de administrador
            intent = Intent(this@CRUD, InterfazAdmin::class.java)
            finish()
            startActivity(intent)
        }
    }

    // Función para modificar un registro
    private fun modificar(){
        if(tipo == "usuario"){ // Si se gestiona un usuario
            if (spTipoCuenta.selectedItem.toString() == "Administrador"){ // Si se selecciona administrador
                intent = Intent(this@CRUD, CrearAdmin::class.java)
            }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){ // Si se selecciona abogado
                intent = Intent(this@CRUD, CrearAbogado::class.java)
            }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){ // Si se selecciona cliente
                intent = Intent(this@CRUD, CrearCuenta::class.java)
                intent.putExtra("usuario", "admin")
            } else { // Si no se selecciona un tipo de cuenta
                Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
            }
        } else{ // Si se gestiona una cita
            intent = Intent(this@CRUD, CrearCita::class.java)
        }
        intent.putExtra("tarea", "modificar") // Se envía la tarea a realizar
        startActivity(intent)
    }
}