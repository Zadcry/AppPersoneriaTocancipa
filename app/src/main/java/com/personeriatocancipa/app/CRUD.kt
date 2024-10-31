package com.personeriatocancipa.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CRUD : AppCompatActivity() {

    private var tipo: String = ""
    private var tarea: String = ""
    private lateinit var btnCrear: Button
    private lateinit var btnConsultar: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnSalir: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        btnCrear = findViewById(R.id.btnCrear)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnSalir = findViewById(R.id.btnSalir)

        tipo = intent.getStringExtra("tipo").toString()


        btnCrear.setOnClickListener{
            if(tipo.equals("usuario")){
                intent.putExtra("tarea", "crear")
                intent = Intent(this@CRUD, CrearCuenta::class.java)
            }else{
                intent.putExtra("tarea", "gestionar")
                intent = Intent(this@CRUD, GestionarCita::class.java)
            }
            finish()
            startActivity(intent)
        }

        btnConsultar.setOnClickListener{
            if(tipo.equals("usuario")){
                intent.putExtra("tarea", "consultar")
                intent = Intent(this@CRUD, CrearCuenta::class.java)
            }else{
                intent.putExtra("tarea", "gestionar")
                intent = Intent(this@CRUD, GestionarCita::class.java)
            }
            finish()
            startActivity(intent)
        }

        btnModificar.setOnClickListener{
            if(tipo.equals("usuario")){
                intent.putExtra("tarea", "modificar")
                intent = Intent(this@CRUD, CrearCuenta::class.java)
            }else{
                intent.putExtra("tarea", "gestionar")
                intent = Intent(this@CRUD, GestionarCita::class.java)
            }
            finish()
            startActivity(intent)
        }

        btnEliminar.setOnClickListener{
            if(tipo.equals("usuario")){
                intent.putExtra("tarea", "eliminar")
                intent = Intent(this@CRUD, CrearCuenta::class.java)
            }else{
                intent.putExtra("tarea", "gestionar")
                intent = Intent(this@CRUD, GestionarCita::class.java)
            }
            finish()
            startActivity(intent)
        }

        btnSalir.setOnClickListener{
            intent = Intent(this@CRUD, Admin::class.java)
            finish()
            startActivity(intent)
        }
    }
}