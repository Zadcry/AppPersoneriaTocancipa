package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.graphics.drawable.GradientDrawable
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// Adaptador para el RecyclerView de citas del abogado
class CitaAdapterAbogado(private var citas: List<Cita>) :
    RecyclerView.Adapter<CitaAdapterAbogado.CitaViewHolder>() {

    // Método para actualizar las citas y refrescar el RecyclerView
    fun actualizarCitas(citasActualizadas: List<Cita>) {
        citas = citasActualizadas
        notifyDataSetChanged()  // Notifica que los datos han cambiado para actualizar el RecyclerView
    }

    // ViewHolder que representa cada cita
    inner class CitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Referencias a los elementos de la vista para cada cita
        val tvTema: TextView = view.findViewById(R.id.tvTema) // Tema de la cita
        val tvId: TextView = view.findViewById(R.id.tvID) // ID de la cita
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora) // Fecha y hora de la cita
        val tvCorreoCliente: TextView = view.findViewById(R.id.tvCorreoCliente) // Correo del cliente
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion) // Descripción de la cita
        val spEstado: Spinner = view.findViewById(R.id.spEstado) // Spinner para el estado de la cita
        val itemContainer: View = view.findViewById(R.id.itemContainer) // Contenedor de la vista
    }

    // Crea un nuevo ViewHolder para cada cita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita_abogado, parent, false) // Inflar la vista del ítem
        return CitaViewHolder(view) // Crear un nuevo ViewHolder con la vista inflada
    }

    // Método que se llama para asignar los datos a cada elemento del RecyclerView
    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position] // Obtener la cita en la posición actual

        // Obtener el nombre del cliente desde Firebase usando el correo
        val mDbRef = FirebaseDatabase.getInstance().getReference("userData")
        var nombreCliente = ""
        val query = mDbRef.orderByChild("correo").equalTo(cita.correoCliente) // Consulta para obtener el cliente mediante su correo
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Recorre los resultados de la consulta
                for (snap in snapshot.children) { // Solo se espera un resultado
                    nombreCliente = snap.child("nombreCompleto").value.toString() // Obtener el nombre
                    holder.tvCorreoCliente.text = applyBoldStyle("Nombre Cliente: ", nombreCliente) // Mostrar el nombre
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
                Log.e("CitaAdapterAbogado", "Error al obtener el nombre del cliente: ${error.message}")
            }
        })

        // Asignar los valores de la cita a los elementos de la vista
        holder.tvTema.text = applyBoldStyle("Tema: ", cita.tema.toString()) // Tema de la cita
        holder.tvId.text = applyBoldStyle("ID: ", cita.id.toString()) // ID de la cita
        holder.tvFechaHora.text = applyBoldStyle("Fecha y hora: ", "${cita.fecha} a las ${cita.hora}") // Fecha y hora
        holder.tvDescripcion.text = applyBoldStyle("Descripción: ", cita.descripcion.toString()) // Descripción

        // Configura el adaptador del Spinner con estilo y muestra las opciones de estado
        val estados = holder.itemView.context.resources.getStringArray(R.array.opcionesEstado)
        val adapter = ArrayAdapter.createFromResource(
            holder.itemView.context,
            R.array.opcionesEstado, // Array de opciones
            R.drawable.spinner_itemestadocita // Estilo del Spinner
        )
        adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item) // Estilo del menú desplegable
        holder.spEstado.adapter = adapter

        // Seleccionar el estado actual de la cita en el Spinner
        val estadoIndex = estados.indexOf(cita.estado)
        if (estadoIndex >= 0) {
            holder.spEstado.setSelection(estadoIndex) // Seleccionar el estado actual
        }

        // Actualiza el contorno de la vista según el estado actual de la cita
        actualizarContorno(holder.itemContainer, cita.estado, holder)
        actualizarContornoSpinner(holder.spEstado, cita.estado, holder)

        // Listener que maneja el cambio de estado en el Spinner
        holder.spEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val nuevoEstado = estados[position] // Obtiene el nuevo estado seleccionado en el Spinner

                // Cambiar el color del texto seleccionado según el estado seleccionado
                val textView = holder.spEstado.selectedView as? TextView
                textView?.setTextColor(getColorForEstado(nuevoEstado, holder.itemView.context))

                // Si el estado ha cambiado, actualiza la cita en Firebase y el contorno del item
                if (cita.estado != nuevoEstado) {
                    cita.estado = nuevoEstado
                    actualizarEstadoEnFirebase(cita)

                    // Actualiza el contorno del item con el nuevo estado
                    actualizarContorno(holder.itemContainer, nuevoEstado, holder)

                    // Muestra un mensaje indicando que el estado ha sido actualizado
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

    // Devuelve la cantidad de elementos en el RecyclerView
    override fun getItemCount(): Int = citas.size

    // Método para aplicar estilo en negrita a un texto
    private fun applyBoldStyle(label: String, value: String): SpannableString {
        val fullText = "$label$value"
        val spannable = SpannableString(fullText) // Aplica la negrita sólo a la etiqueta
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    // Método para actualizar el estado de la cita en Firebase
    private fun actualizarEstadoEnFirebase(cita: Cita) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("citas")
        databaseReference.child(cita.id.toString()).child("estado").setValue(cita.estado)
    }

    private fun getColorForEstado(estado: String, context: android.content.Context): Int {
        // Devuelve el color correspondiente al estado de la cita
        return when (estado.toLowerCase()) {
            "cancelada" -> context.getColor(R.color.Rojo) // Color Rojo para "Cancelada"
            "asistió" -> context.getColor(R.color.verde) // Color Verde para "Asistió"
            "no asistió" -> context.getColor(R.color.grisClaro) // Color Gris para "No asistió"
            "pendiente" -> context.getColor(R.color.azul) // Color Azul para "Pendiente"
            else -> context.getColor(android.R.color.black) // Color por defecto
        }
    }

    // Método para actualizar el contorno del item según el estado de la cita
    private fun actualizarContorno(itemContainer: View, estado: String?, holder: CitaViewHolder) {
        val color = getColorForEstado(estado ?: "", holder.itemView.context)
        val background = itemContainer.background
        if (background is GradientDrawable) { // Verifica si el fondo es un GradientDrawable
            background.setStroke(7, color) // Cambiar grosor y color del contorno
        }
    }

    // Método para actualizar el contorno del Spinner según el estado de la cita
    private fun actualizarContornoSpinner(spinner: Spinner, estado: String?, holder: CitaViewHolder) {
        val color = getColorForEstado(estado ?: "", holder.itemView.context)
        val background = spinner.background
        // Verifica si el fondo es un GradientDrawable
        if (background is GradientDrawable) {
            background.setStroke(5, color) // Cambiar grosor y color del contorno del Spinner
        }
    }

}
