package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CRUD : AppCompatActivity() {

    private var tipo: String = ""
    private lateinit var btnCrear: Button
    private lateinit var btnConsultar: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnSalir: Button
    private lateinit var txtTitulo: TextView
    private lateinit var spTipoCuenta: Spinner

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        btnCrear = findViewById(R.id.btnCrear)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnSalir = findViewById(R.id.btnSalir)
        txtTitulo = findViewById(R.id.txtTitulo)

        spTipoCuenta = findViewById(R.id.spTipoCuenta)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTipoCuenta,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTipoCuenta.adapter = adapter
        }

        tipo = intent.getStringExtra("tipo").toString()

        if (tipo == "usuario"){
            txtTitulo.text = "Gestión de Usuarios"
            spTipoCuenta.visibility = Spinner.VISIBLE
        }else{
            txtTitulo.text = "Gestión de Citas"
            spTipoCuenta.visibility = Spinner.INVISIBLE
        }

        btnCrear.setOnClickListener{
            if(tipo.equals("usuario")){
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin")
                } else {
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }

            }else{
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "crear")
            startActivity(intent)
        }

        btnConsultar.setOnClickListener{
            if(tipo == "usuario"){
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin")
                } else {
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }

            }else{
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "consultar")
            startActivity(intent)
        }

        btnModificar.setOnClickListener{
            if(tipo == "usuario"){
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin")
                } else {
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }

            } else{
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "modificar")
            startActivity(intent)
        }

        btnEliminar.setOnClickListener{
            if(tipo == "usuario"){
                if (spTipoCuenta.selectedItem.toString() == "Administrador"){
                    intent = Intent(this@CRUD, CrearAdmin::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Abogado"){
                    intent = Intent(this@CRUD, CrearAbogado::class.java)
                }else if (spTipoCuenta.selectedItem.toString() == "Cliente"){
                    intent = Intent(this@CRUD, CrearCuenta::class.java)
                    intent.putExtra("usuario", "admin")
                } else {
                    Toast.makeText(this, "Seleccione un tipo de cuenta", Toast.LENGTH_SHORT).show()
                }

            }else{
                intent = Intent(this@CRUD, CrearCita::class.java)
            }
            intent.putExtra("tarea", "eliminar")
            startActivity(intent)
        }

        btnSalir.setOnClickListener{
            intent = Intent(this@CRUD, InterfazAdmin::class.java)
            finish()
            startActivity(intent)
        }



    }
}