package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CRUD : AppCompatActivity() {

    private var tipo: String = ""
    private lateinit var btnCrear: Button
    private lateinit var btnConsultar: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnSalir: Button
    private lateinit var txtTitulo: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        btnCrear = findViewById(R.id.btnCrear)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnSalir = findViewById(R.id.btnSalir)
        txtTitulo = findViewById(R.id.txtTitulo)

        tipo = intent.getStringExtra("tipo").toString()

        if (tipo.equals("usuario")){
            txtTitulo.text = "Gestión de Usuarios"
        }else{
            txtTitulo.text = "Gestión de Citas"
        }

        btnCrear.setOnClickListener{
            if(tipo.equals("usuario")){
                intent = Intent(this@CRUD, CrearCuenta::class.java)
                intent.putExtra("tarea", "crear")
            }else{
                intent = Intent(this@CRUD, GestionarCita::class.java)
                intent.putExtra("tarea", "gestionar")
            }
            startActivity(intent)
        }

        btnConsultar.setOnClickListener{
            if(tipo.equals("usuario")){
                intent = Intent(this@CRUD, CrearCuenta::class.java)
                intent.putExtra("tarea", "consultar")
            }else{
                intent = Intent(this@CRUD, GestionarCita::class.java)
                intent.putExtra("tarea", "gestionar")
            }
            startActivity(intent)
        }

        btnModificar.setOnClickListener{
            if(tipo.equals("usuario")){
                intent = Intent(this@CRUD, CrearCuenta::class.java)
                intent.putExtra("tarea", "modificar")
            }else{
                intent = Intent(this@CRUD, GestionarCita::class.java)
                intent.putExtra("tarea", "gestionar")
            }
            startActivity(intent)
        }

        btnEliminar.setOnClickListener{
            if(tipo.equals("usuario")){
                intent = Intent(this@CRUD, CrearCuenta::class.java)
                intent.putExtra("tarea", "eliminar")
            }else{
                intent = Intent(this@CRUD, GestionarCita::class.java)
                intent.putExtra("tarea", "gestionar")
            }
            startActivity(intent)
        }

        btnSalir.setOnClickListener{
            intent = Intent(this@CRUD, InterfazAdmin::class.java)
            startActivity(intent)
        }
    }
}