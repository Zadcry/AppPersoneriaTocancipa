package com.personeriatocancipa.app

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.GradientDrawable

class CitaAdapterCliente(private var citas: List<Cita>) :
    RecyclerView.Adapter<CitaAdapterCliente.CitaViewHolder>() {

    // Método para actualizar las citas
    fun actualizarCitas(citasActualizadas: List<Cita>) {
        citas = citasActualizadas
        notifyDataSetChanged()  // Notifica que los datos han cambiado para actualizar el RecyclerView
    }

    inner class CitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTema: TextView = view.findViewById(R.id.tvTema)
        val tvId: TextView = view.findViewById(R.id.tvID)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)
        val tvCorreoAbogado: TextView = view.findViewById(R.id.tvCorreoAbogado)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val itemContainer: View = view.findViewById(R.id.itemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita_cliente, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]

        // Aplica estilo negrita a las etiquetas
        holder.tvTema.text = applyBoldStyle("Tema: ", cita.tema.toString())
        holder.tvId.text = applyBoldStyle("ID: ", cita.id.toString())
        holder.tvFechaHora.text = applyBoldStyle("Fecha y hora: ", "${cita.fecha} a las ${cita.hora}")
        holder.tvCorreoAbogado.text = applyBoldStyle("Correo Abogado: ", cita.correoAbogado.toString())
        holder.tvDescripcion.text = applyBoldStyle("Descripción: ", cita.descripcion.toString())
        holder.tvEstado.text = applyBoldStyle("Estado: ", cita.estado.toString())

        // Cambiar el color basado en el estado
        val estadoColor = when (cita.estado?.toLowerCase()) {
            "cancelada" -> holder.itemView.context.getColor(R.color.Rojo)
            "asistió" -> holder.itemView.context.getColor(R.color.verde)
            "no asistió" -> holder.itemView.context.getColor(R.color.grisClaro)
            "pendiente" -> holder.itemView.context.getColor(R.color.azul)
            else -> holder.itemView.context.getColor(android.R.color.black) // Color por defecto
        }
        holder.tvEstado.text = applyBoldStyleWithColor("Estado: ", cita.estado.toString(), estadoColor)
        // Cambiar el color del contorno del drawable
        val background = holder.itemContainer.background
        if (background is GradientDrawable) {
            background.setStroke(7, estadoColor) // Cambia el color y el grosor del contorno
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

    private fun applyBoldStyleWithColor(label: String, value: String, color: Int): SpannableString {
        val fullText = "$label$value"
        val spannable = SpannableString(fullText)

        // Aplicar negrita al label
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Cambiar el color del valor
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(color),
            label.length, fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}
