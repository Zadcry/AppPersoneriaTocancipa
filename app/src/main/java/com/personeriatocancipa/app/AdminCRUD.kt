package com.personeriatocancipa.app

import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminCRUD : AppCompatActivity() {
    private lateinit var spCRUD: Spinner
    private lateinit var spUsuario: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var button: Button
    private lateinit var userList: ArrayList<Usuario>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_crud)

        // Inicializar componentes
        spCRUD = findViewById(R.id.spCrud)
        spUsuario = findViewById(R.id.spRol)
        button = findViewById(R.id.btnCrear)
        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        userList= ArrayList()
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        setupMainSpinner()
    }

    private fun setupMainSpinner() {
        // Opciones del primer Spinner
        val options = arrayOf("Crear", "Leer", "Actualizar", "Borrar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCRUD.adapter = adapter

        // Escucha selecciones en el primer Spinner
        spCRUD.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (options[position]) {
                    "Crear" -> CrearMenu()
                    "Actualizar" -> ActualizarMenu()
                    "Borrar" -> BorrarMenu()
                    "Leer" -> LeerMenu()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    // Opción 1: Mostrar Spinner 2(prueba) y botón
    private fun CrearMenu() {
        spUsuario.visibility = View.VISIBLE
        button.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // spinner 2, cosas en el array(TODO no se donde pusieron los de los otros spinners, cambienlo a donde sea que pusieron los demas)
        val secondOptions = arrayOf("Usuario", "Abogado", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, secondOptions)
        spUsuario.adapter = adapter

        button.setOnClickListener {
            val selectedLayout = spUsuario.selectedItem.toString()
            when (selectedLayout) {
                "Usuario" -> {
                    val intent = Intent(this@AdminCRUD, CrearCuenta::class.java)
                    finish()
                    startActivity(intent)}
                "Abogado" -> {//TODO crear de crear abogado y admin(yo me entiendo)
                    val intent = Intent(this@AdminCRUD, CrearCuenta::class.java)
                    finish()
                    startActivity(intent)}
                "Admin" -> {
                    val intent = Intent(this@AdminCRUD, CrearCuenta::class.java)
                    finish()
                    startActivity(intent)}
            }
        }
    }

    // Opción 2: Mostrar Recycler de actualizar
    private fun ActualizarMenu() {
        spUsuario.visibility = View.GONE
        button.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE


        val adapter = UsuariosAdapter(this@AdminCRUD,userList)
        recyclerView.adapter = adapter
        //TODO: cambiar "users" si se cambia el nombre en el firebase y talvez hacer otro adapter
        mDbRef.child("users").addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                userList.clear()
                for (postsnapshot in snapshot.children)
                {
                    val currentUser = postsnapshot.getValue(Usuario::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid)
                    {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun BorrarMenu() {
        spUsuario.visibility = View.GONE
        button.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val adapter = UsuariosAdapter(this  ,userList)
        recyclerView.adapter = adapter
        //TODO: cambiar "users" si se cambia el nombre en el firebase y talvez hacer otro adapter
        mDbRef.child("users").addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                userList.clear()
                for (postsnapshot in snapshot.children)
                {
                    val currentUser = postsnapshot.getValue(Usuario::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid)
                    {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // TODO poner lo de ver
    private fun LeerMenu() {
        spUsuario.visibility = View.GONE
        button.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }
}