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
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Clase que se encarga de adaptar los datos de las citas para mostrarlos en el RecyclerView
class CitaAdapterCliente(private var citas: List<Cita>) :
    RecyclerView.Adapter<CitaAdapterCliente.CitaViewHolder>() {

    // Método para actualizar las citas y recargar el RecyclerView
    fun actualizarCitas(citasActualizadas: List<Cita>) {
        citas = citasActualizadas
        notifyDataSetChanged()  // Notifica que los datos han cambiado para actualizar el RecyclerView
    }

    // Clase interna que representa la vista de cada item en el RecyclerView
    inner class CitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Referencias a los elementos de cada ítem
        val tvTema: TextView = view.findViewById(R.id.tvTema) // TextView del tema
        val tvId: TextView = view.findViewById(R.id.tvID) // TextView del ID
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora) // TextView de la fecha y hora
        val tvCorreoAbogado: TextView = view.findViewById(R.id.tvCorreoAbogado) // TextView del correo del abogado
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion) // TextView de la descripción
        val tvEstado: TextView = view.findViewById(R.id.tvEstado) // TextView del estado
        val itemContainer: View = view.findViewById(R.id.itemContainer) // Contenedor del ítem
    }

    // Método para crear e inflar un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        // Inflar el layout del ítem
        val view = LayoutInflater.from(parent.context)
            // Cargar el layout de cada item de cita
            .inflate(R.layout.item_cita_cliente, parent, false)

        return CitaViewHolder(view) // Devuelve un ViewHolder con la vista inflada
    }

    // Método para enlazar los datos de una cita con la vista de un ViewHolder
    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position] // Obtiene la cita en la posición indicada

        // Inicializar referencia a base de datros para obtener el nombre del abogado a partir del correo
        val mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        var nombreAbogado = ""
        val query = mDbRef.orderByChild("correo").equalTo(cita.correoAbogado)
        // Consultar el nombre del abogado
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) { // Iterar sobre los resultados
                    nombreAbogado = snap.child("nombreCompleto").value.toString()
                    // Aplicar estilo negrita al nombre del abogado
                    holder.tvCorreoAbogado.text = applyBoldStyle("Nombre Abogado: ", nombreAbogado)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores en la consulta a la base de datos
                Log.e("CitaAdapterAbogado", "Error al obtener el nombre del cliente: ${error.message}")
            }
        })

        // Aplica estilo negrita a las etiquetas
        holder.tvTema.text = applyBoldStyle("Tema: ", cita.tema.toString())
        holder.tvId.text = applyBoldStyle("ID: ", cita.id.toString())
        holder.tvFechaHora.text = applyBoldStyle("Fecha y hora: ", "${cita.fecha} a las ${cita.hora}")
        holder.tvDescripcion.text = applyBoldStyle("Descripción: ", cita.descripcion.toString())
        holder.tvEstado.text = applyBoldStyle("Estado: ", cita.estado.toString())

        // Cambiar el color basado en el estado
        val estadoColor = when (cita.estado?.toLowerCase()) {
            "cancelada" -> holder.itemView.context.getColor(R.color.Rojo) // Color Rojo para "Cancelada"
            "asistió" -> holder.itemView.context.getColor(R.color.verde) // Color Verde para "Asistió"
            "no asistió" -> holder.itemView.context.getColor(R.color.grisClaro) // Color Gris para "No asistió"
            "pendiente" -> holder.itemView.context.getColor(R.color.azul) // Color Azul para "Pendiente"
            else -> holder.itemView.context.getColor(android.R.color.black) // Color por defecto
        }
        // Aplica color y negrita al estado
        holder.tvEstado.text = applyBoldStyleWithColor("Estado: ", cita.estado.toString(), estadoColor)
        // Cambiar el color del contorno del ítem (cita) según el estado
        val background = holder.itemContainer.background
        if (background is GradientDrawable) {
            background.setStroke(7, estadoColor) // Cambia el color y el grosor del contorno según el estado
        }
    }

    // Método para obtener la cantidad de elementos en la lista de citas
    override fun getItemCount(): Int = citas.size

    // Método para aplicar formato en negrita a las etiquetas de texto
    private fun applyBoldStyle(label: String, value: String): SpannableString {
        val fullText = "$label$value" // Concatenar la etiqueta y el valor
        val spannable = SpannableString(fullText) // Crear un objeto SpannableString para aplicar estilos
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        ) // Aplica negrita al texto de la etiqueta
        return spannable
    }

    // Método para aplicar formato en negrita y cambiar el color a las etiquetas de texto
    private fun applyBoldStyleWithColor(label: String, value: String, color: Int): SpannableString {
        val fullText = "$label$value" // Concatenar la etiqueta y el valor
        val spannable = SpannableString(fullText) // Crear un objeto SpannableString para aplicar estilos

        // Aplicar negrita a la etiqueta
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
