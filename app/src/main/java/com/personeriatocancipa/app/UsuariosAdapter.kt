package com.personeriatocancipa.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuariosAdapter (val context: Context, val userList: ArrayList<Usuario>):
    RecyclerView.Adapter<UsuariosAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.usuario_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.nombreCompleto
        holder.itemView.setOnClickListener {
            val intent = Intent(context,CrearCuenta::class.java)
            intent.putExtra("uid",currentUser.uid)
            //intent.putExtra("uid",FirebaseAuth.getInstance().currentUser?.uid)
            context.startActivity(intent)
            //TODO: de hecho hacer que sirva haciendo un ActualizarCuenta o algo asi y borrar cuenta, no se como hacer que sirva, voy a investigar
        }
    }
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_Name)
    }

}

