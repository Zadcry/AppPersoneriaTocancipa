package com.personeriatocancipa.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuariosAdapter (private val items: List<String>,
private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<UsuariosAdapter.ButtonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(text: String, onClick: (String) -> Unit) {
            button.text = text
            itemView.setOnClickListener {
                onClick(text)
            }
        }
    }
}