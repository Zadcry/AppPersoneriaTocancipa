package com.personeriatocancipa.app

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CitaAdapterAbogado(private val citas: List<Cita>) :
    RecyclerView.Adapter<CitaAdapterAbogado.CitaViewHolder>() {

    inner class CitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTema: TextView = view.findViewById(R.id.tvTema)
        val tvId: TextView = view.findViewById(R.id.tvID)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)
        val tvCorreoCliente: TextView = view.findViewById(R.id.tvCorreoCliente)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val spEstado: Spinner = view.findViewById(R.id.spEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita_abogado, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]

        holder.tvTema.text = applyBoldStyle("Tema: ", cita.tema.toString())
        holder.tvId.text = applyBoldStyle("ID: ", cita.id.toString())
        holder.tvFechaHora.text = applyBoldStyle("Fecha y hora: ", "${cita.fecha} a las ${cita.hora}")
        holder.tvCorreoCliente.text = applyBoldStyle("Correo Cliente: ", cita.correoCliente.toString())
        holder.tvDescripcion.text = applyBoldStyle("Descripción: ", cita.descripcion.toString())

        // Configurar el Spinner con el estado actual
        val estados = holder.itemView.context.resources.getStringArray(R.array.opcionesEstado)
        val estadoIndex = estados.indexOf(cita.estado)
        if (estadoIndex >= 0) {
            holder.spEstado.setSelection(estadoIndex)
        }

        // Listener para el cambio de estado
        holder.spEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val nuevoEstado = estados[position]
                if (cita.estado != nuevoEstado) {
                    cita.estado = nuevoEstado
                    actualizarEstadoEnFirebase(cita)
                    Toast.makeText(
                        holder.itemView.context,
                        "Estado actualizado a: $nuevoEstado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun getItemCount(): Int = citas.size

    private fun applyBoldStyle(label: String, value: String): SpannableString {
        val fullText = "$label$value"
        val spannable = SpannableString(fullText)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun actualizarEstadoEnFirebase(cita: Cita) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("citas")
        databaseReference.child(cita.id.toString()).child("estado").setValue(cita.estado)
    }
}
