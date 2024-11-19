package com.personeriatocancipa.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ConsultarCitasCliente : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var citasList: MutableList<Cita>
    private lateinit var adapter: CitaAdapterCliente
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar_citas_cliente)

        recyclerView = findViewById(R.id.recyclerViewCitaCliente)
        recyclerView.layoutManager = LinearLayoutManager(this)

        citasList = mutableListOf()
        adapter = CitaAdapterCliente(citasList)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("citas")

        cargarCitas()
    }

    private fun cargarCitas() {
        val userEmail = auth.currentUser?.email ?: ""
        Log.d("CorreoUsuario", "Correo autenticado: $userEmail")

        databaseReference.orderByChild("correoCliente").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    citasList.clear()
                    for (citaSnapshot in snapshot.children) {
                        val cita = citaSnapshot.getValue(Cita::class.java)
                        if (cita != null) {
                            citasList.add(cita)
                            Log.d("ConsultarCitas", "Cita cargada: ${cita.descripcion} - ${cita.fecha}")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ConsultarCitas", "Error al cargar citas: ${error.message}")
                    Toast.makeText(this@ConsultarCitasCliente, "Error al cargar citas", Toast.LENGTH_SHORT).show()
                }
            })
    }
}