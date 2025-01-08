package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CrearAbogado : AppCompatActivity() {

    // Variables globales que representan los elementos de la vista
    private lateinit var gridConsultar: LinearLayout
    private lateinit var txtConsultar: EditText
    private lateinit var txtAnuncio: TextView
    private lateinit var txtNombre: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtConfirmarClave: EditText
    private lateinit var txtDocumento: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var spCargo: Spinner
    private lateinit var spTema: Spinner
    private lateinit var spEstado: Spinner
    private lateinit var btnConsultar: Button
    private lateinit var btnSalir: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnTogglePassword: Button
    private lateinit var btnToggleCheckPassword: Button
    private lateinit var btnModificar: Button
    private lateinit var btnEliminar: Button
    private lateinit var tvClave: TextView
    private lateinit var tvConfirmarClave: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvCargo: TextView
    private lateinit var tvTema: TextView

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var tarea: String = ""
    private var sujeto: String = ""
    private var uidConsultado: String = ""

    // Elementos de la vista del Horario
    private lateinit var btnVerHorario: Button
    private lateinit var gridHorario: LinearLayout

    private lateinit var tvLunes: TextView
    private lateinit var tvMartes: TextView
    private lateinit var tvMiercoles: TextView
    private lateinit var tvJueves: TextView
    private lateinit var tvViernes: TextView
    private lateinit var tvDocumento: TextView

    private lateinit var spLunesInicio: Spinner
    private lateinit var spLunesFin: Spinner
    private lateinit var spMartesInicio: Spinner
    private lateinit var spMartesFin: Spinner
    private lateinit var spMiercolesInicio: Spinner
    private lateinit var spMiercolesFin: Spinner
    private lateinit var spJuevesInicio: Spinner
    private lateinit var spJuevesFin: Spinner
    private lateinit var spViernesInicio: Spinner
    private lateinit var spViernesFin: Spinner

    private lateinit var chkLunes: CheckBox
    private lateinit var chkMartes: CheckBox
    private lateinit var chkMiercoles: CheckBox
    private lateinit var chkJueves: CheckBox
    private lateinit var chkViernes: CheckBox

    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_abogado)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        // Obtener los valores del Intent
        tarea = intent.getStringExtra("tarea").toString()
        sujeto = intent.getStringExtra("sujeto").toString()

        // Asignar los elementos de la vista a las variables
        gridConsultar = findViewById(R.id.gridConsultar)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtNombre = findViewById(R.id.txtNombre)
        txtClave = findViewById(R.id.txtClave)
        txtConfirmarClave = findViewById(R.id.txtConfirmarClave)
        txtDocumento = findViewById(R.id.txtDocumento)
        txtCorreo = findViewById(R.id.txtCorreo)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnSalir = findViewById(R.id.btnSalir)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnModificar = findViewById(R.id.btnModificar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleCheckPassword = findViewById(R.id.btnToggleCheckPassword)
        tvClave = findViewById(R.id.tvClave)
        tvConfirmarClave = findViewById(R.id.tvConfirmarClave)
        tvEstado = findViewById(R.id.tvEstado)
        tvCargo = findViewById(R.id.tvCargo)
        tvTema = findViewById(R.id.tvTema)
        tvDocumento = findViewById(R.id.tvDocumento)

        // Horario
        btnVerHorario = findViewById(R.id.btnVerHorario)
        gridHorario = findViewById(R.id.gridHorario)

        // TextView
        tvLunes = findViewById(R.id.tvLunes)
        tvMartes = findViewById(R.id.tvMartes)
        tvMiercoles = findViewById(R.id.tvMiercoles)
        tvJueves = findViewById(R.id.tvJueves)
        tvViernes = findViewById(R.id.tvViernes)

        // CheckBox
        chkLunes = findViewById(R.id.chkLunes)
        chkMartes = findViewById(R.id.chkMartes)
        chkMiercoles = findViewById(R.id.chkMiercoles)
        chkJueves = findViewById(R.id.chkJueves)
        chkViernes = findViewById(R.id.chkViernes)

        // Spinner
        spLunesInicio = findViewById(R.id.spLunesInicio)
        spLunesFin = findViewById(R.id.spLunesFin)
        spMartesInicio = findViewById(R.id.spMartesInicio)
        spMartesFin = findViewById(R.id.spMartesFin)
        spMiercolesInicio = findViewById(R.id.spMiercolesInicio)
        spMiercolesFin = findViewById(R.id.spMiercolesFin)
        spJuevesInicio = findViewById(R.id.spJuevesInicio)
        spJuevesFin = findViewById(R.id.spJuevesFin)
        spViernesInicio = findViewById(R.id.spViernesInicio)
        spViernesFin = findViewById(R.id.spViernesFin)

        // Array con el nombre de los spinner
        val spinnerDiasArray = arrayOf(spLunesInicio, spLunesFin, spMartesInicio, spMartesFin, spMiercolesInicio, spMiercolesFin, spJuevesInicio, spJuevesFin, spViernesInicio, spViernesFin)
        // Configurar los spinner de horario
        for (spinner in spinnerDiasArray) {
            ArrayAdapter.createFromResource(
                this,
                R.array.horasLaborales,
                R.drawable.spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
                spinner.adapter = adapter
            }
            spinner.visibility = Spinner.GONE
        }

        // Iniciar y Ocultar TextViews de días
        val tvDiasArray = arrayOf(tvLunes, tvMartes, tvMiercoles, tvJueves, tvViernes)
        for (tv in tvDiasArray) {
            tv.visibility = TextView.GONE
        }

        // Configurar CheckBoxes
        val checkBoxesArray = arrayOf(chkLunes, chkMartes, chkMiercoles, chkJueves, chkViernes)
        for (checkBox in checkBoxesArray) {
            checkBox.setOnClickListener {
                // Mostrar u ocultar los spinners de horario al seleccionar o deseleccionar un día
                if (checkBox.isChecked) {
                    val index = checkBoxesArray.indexOf(checkBox)
                    tvDiasArray[index].visibility = TextView.VISIBLE
                    spinnerDiasArray[index * 2].visibility = Spinner.VISIBLE
                    spinnerDiasArray[(index * 2) + 1].visibility = Spinner.VISIBLE
                } else {
                    val index = checkBoxesArray.indexOf(checkBox)
                    tvDiasArray[index].visibility = TextView.GONE
                    spinnerDiasArray[index * 2].visibility = Spinner.GONE
                    spinnerDiasArray[(index * 2) + 1].visibility = Spinner.GONE
                }
            }
        }

        // Configurar Spinner de Cargo
        spCargo = findViewById(R.id.spCargo)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesCargoAbogado,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spCargo.adapter = adapter
        }

        // Configurar Spinner de Tema
        spTema = findViewById(R.id.spTema)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesTema,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spTema.adapter = adapter
        }

        // Configurar Spinner de Estado
        spEstado = findViewById(R.id.spEstado)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesEstadoCargo,
            R.drawable.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spEstado.adapter = adapter
        }

        // Botón Ver Contraseña
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnTogglePassword.setOnClickListener { v: View? ->
            if (txtClave.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                txtClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtClave.setSelection(txtClave.text.length) // Mantener cursor al final
        }

        // Botón Ver Confirmar Contraseña
        btnToggleCheckPassword = findViewById(R.id.btnToggleCheckPassword)
        btnToggleCheckPassword.setOnClickListener { v: View? ->
            if (txtConfirmarClave.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                txtConfirmarClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                txtConfirmarClave.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtConfirmarClave.setSelection(txtConfirmarClave.text.length) // Mantener cursor al final
        }

        // Configurar la vista según la tarea y el sujeto
        // Algunos elementos se ocultan o se deshabilitan según la tarea
        if(tarea == "crear"){
            txtAnuncio.text = "Crear Abogado"
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
            gridHorario.visibility = LinearLayout.GONE
        }else {
            txtAnuncio.text = "Gestión de Abogado"
            gridConsultar.visibility = View.VISIBLE
            btnVerHorario.text = "Ocultar Horario"
            gridHorario.visibility = LinearLayout.VISIBLE
            tvDocumento.visibility = TextView.GONE
            txtDocumento.visibility = EditText.GONE
            txtDocumento.isEnabled = false
            when (tarea) {
                "consultar" -> {
                    btnSignUp.visibility = Button.GONE
                    btnModificar.visibility = Button.GONE
                    btnEliminar.visibility = Button.GONE
                    txtClave.visibility = EditText.GONE
                    btnTogglePassword.visibility = Button.GONE
                    txtConfirmarClave.visibility = EditText.GONE
                    btnToggleCheckPassword.visibility = Button.GONE
                    tvClave.visibility = TextView.GONE
                    tvConfirmarClave.visibility = TextView.GONE
                    disableFields()
                }
                "modificar" -> {
                    if(sujeto == "propio"){
                        gridConsultar.visibility = GridLayout.GONE
                        tvEstado.visibility = TextView.GONE
                        spEstado.visibility = Spinner.GONE
                        tvTema.visibility = TextView.GONE
                        spTema.visibility = Spinner.GONE
                        tvCargo.visibility = TextView.GONE
                        spCargo.visibility = Spinner.GONE
                    }
                    btnSignUp.visibility = Button.GONE
                    btnModificar.visibility = Button.GONE
                    btnEliminar.visibility = Button.GONE
                    txtClave.visibility = EditText.GONE
                    btnTogglePassword.visibility = Button.GONE
                    txtConfirmarClave.visibility = EditText.GONE
                    btnToggleCheckPassword.visibility = Button.GONE
                    tvClave.visibility = TextView.GONE
                    tvConfirmarClave.visibility = TextView.GONE
                }
                else -> {
                    btnSignUp.visibility = Button.GONE
                    btnModificar.visibility = Button.GONE
                    btnEliminar.visibility = Button.GONE
                    txtClave.visibility = EditText.GONE
                    btnTogglePassword.visibility = Button.GONE
                    txtConfirmarClave.visibility = EditText.GONE
                    btnToggleCheckPassword.visibility = Button.GONE
                    tvClave.visibility = TextView.GONE
                    tvConfirmarClave.visibility = TextView.GONE
                    disableFields()
                }
            }
        }

        // Configurar el botón Ver Horario
        btnVerHorario.setOnClickListener {
            if (gridHorario.visibility == LinearLayout.GONE) {
                btnVerHorario.text = "Ocultar Horario"
                gridHorario.visibility = LinearLayout.VISIBLE
            } else {
                btnVerHorario.text = "Mostrar Horario"
                gridHorario.visibility = LinearLayout.GONE
            }
        }

        btnSalir.setOnClickListener {
            finish()
        }

        btnConsultar.setOnClickListener {
            consultarPorCedula()
        }

        // Preliminares de la creacion de cuenta
        btnSignUp.setOnClickListener {
            // Crear y configurar el diálogo inicial
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Política de Tratamiento de Datos Personales")
            builder.setMessage("Señor(a) usuario(a), autoriza a la Personería de Tocancipá, para la recolección, consulta, almacenamiento, uso, traslado o eliminación de sus datos personales, con el fin de: adelantar las gestiones, actuaciones e intervenciones que permitan el restablecimiento y goce de sus derechos, invitar a eventos de participación ciudadana u organizados por la entidad,  información con fines estadísticos, enviar información a entidades autorizadas cuando la solicitud lo amerite, evaluar la calidad del servicio y contactar al titular en los casos que se considere necesario. \n" +
                    "\n" +
                    "No es obligatorio para la prestación del servicio, suministrar los datos personales de carácter sensible o de niños, niñas y adolescentes. Se exime el tratamiento de datos de niños, niñas y adolescentes, salvo aquellos datos que sean de naturaleza pública. \n" +
                    "Como titular de la información tiene derecho a conocer, actualizar y rectificar sus datos personales, así como  suprimir o revocar la autorización otorgada para su tratamiento, pedir prueba de la autorización otorgada al responsable del tratamiento y ser informado sobre el uso que le han dado a los mismos, presentar quejas ante la Superintendencia de Industria y Comercio SIC por infracción a la ley y acceder en forma gratuita a sus datos personales, acorde al o establecido en los artículos 15, 20 y 74 de la Constitución Política, , la Ley 1581 del 2012, el Decreto Reglamentario 1377 de 2013, la Ley 1266 del 31 de 2008, el Decreto Reglamentario 1727 de  2009, y demás normatividad vigente relacionada. \n" +
                    "Consulte la Política de Tratamiento de Datos Personales Personería Municipal de Tocancipá adoptada mediante Resolución Administrativa 051 de 2020 en el siguiente link https://www.personeria-tocancipa.gov.co/politicas-y-lineamientos/politica-de-tratamiento-de-datos\n" +
                    "¿Acepta términos y condiciones?\n")

            // Agregar botones de acción
            builder.setPositiveButton("Sí") { dialog, which ->
                // Continuar con la creación de la cuenta
                crearAbogado()
            }

            builder.setNegativeButton("No") { dialog, which ->
                // Cancelar el flujo
                dialog.dismiss()
                Toast.makeText(
                    this@CrearAbogado,
                    "Debe aceptar la política de tratamiento de datos personales para continuar",
                    Toast.LENGTH_SHORT,
                ).show()
            }

            // Mostrar el diálogo de permiso de habeas data
            builder.create().show()

        }

        btnModificar.setOnClickListener {
            modificarAbogado()
        }

        btnEliminar.setOnClickListener {
            eliminarAbogado()
        }

        // Cargar datos del usuario actual si entra a modificar su propio perfil
        if (sujeto == "propio"){
            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
            val query = mDbRef.orderByChild("correo").equalTo(mAuth.currentUser?.email)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    println(snapshot)
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            uidConsultado = it.key.toString()
                            println(it)
                            val nombre = it.child("nombreCompleto").value.toString()
                            val documento = it.child("documento").value.toString()
                            val correo = it.child("correo").value.toString()
                            val cargo = it.child("cargo").value.toString()
                            val tema = it.child("tema").value.toString()

                            txtNombre.setText(nombre)
                            txtDocumento.setText(documento)
                            txtCorreo.setText(correo)
                            spCargo.setSelection((spCargo.adapter as ArrayAdapter<String>).getPosition(cargo))
                            spTema.setSelection((spTema.adapter as ArrayAdapter<String>).getPosition(tema))
                        }
                        if(tarea == "modificar"){
                            btnModificar.visibility = Button.VISIBLE
                        }
                    } else {
                        Toast.makeText(
                            this@CrearAbogado,
                            "No se encontró un abogado con la cédula ingresada",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@CrearAbogado,
                        "Error al consultar la base de datos",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            })
        }

    }

    private fun disableFields(){
        txtNombre.isEnabled = false
        txtClave.isEnabled = false
        txtConfirmarClave.isEnabled = false
        btnTogglePassword.isEnabled = false
        btnToggleCheckPassword.isEnabled = false
        txtDocumento.isEnabled = false
        txtCorreo.isEnabled = false
        spCargo.isEnabled = false
        spTema.isEnabled = false
        spEstado.isEnabled = false

        val spinnerDiasArray = arrayOf(spLunesInicio, spLunesFin, spMartesInicio, spMartesFin, spMiercolesInicio, spMiercolesFin, spJuevesInicio, spJuevesFin, spViernesInicio, spViernesFin)
        val checkBoxesArray = arrayOf(chkLunes, chkMartes, chkMiercoles, chkJueves, chkViernes)
        for (spinner in spinnerDiasArray) {
            spinner.isEnabled = false
        }
        for (checkBox in checkBoxesArray) {
            checkBox.isEnabled = false
        }

    }

    // Función para consultar un abogado por su cédula
    private fun consultarPorCedula() {
        val cedula = txtConsultar.text.toString()
        if (cedula.isEmpty()) {
            Toast.makeText(
                this@CrearAbogado,
                "Ingrese cédula para consultar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")

        val query = mDbRef.orderByChild("documento").equalTo(cedula)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        // Si se encuentra un abogado en Firebase,
                        // Mostrar los datos del abogado en los campos de texto
                        uidConsultado = it.key.toString()
                        val nombre = it.child("nombreCompleto").value.toString()
                        val documento = it.child("documento").value.toString()
                        val correo = it.child("correo").value.toString()
                        val cargo = it.child("cargo").value.toString()
                        val tema = it.child("tema").value.toString()
                        val estado = it.child("estado").value.toString()

                        // Mostrar los datos del abogado en los campos de texto
                        txtNombre.setText(nombre)
                        txtClave.setText("********")
                        txtDocumento.setText(documento)
                        txtCorreo.setText(correo)
                        spCargo.setSelection((spCargo.adapter as ArrayAdapter<String>).getPosition(cargo))
                        spTema.setSelection((spTema.adapter as ArrayAdapter<String>).getPosition(tema))
                        spEstado.setSelection((spEstado.adapter as ArrayAdapter<String>).getPosition(estado))

                        // Utilizar la funcion para obtener el horario del abogado
                        conseguirHorarioDeFirebase(nombre)
                    }
                    // Mostrar los botones de modificar o eliminar según la tarea despues de haber consultado
                    if(tarea == "modificar"){
                        btnModificar.visibility = Button.VISIBLE
                    }else if(tarea == "eliminar"){
                        btnEliminar.visibility = Button.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        this@CrearAbogado,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }

    // Función para obtener el horario de un abogado desde Firebase
    private fun conseguirHorarioDeFirebase(nombre: String) {
        val spinnerDiasArray = arrayOf(spLunesInicio, spLunesFin, spMartesInicio, spMartesFin, spMiercolesInicio, spMiercolesFin, spJuevesInicio, spJuevesFin, spViernesInicio, spViernesFin)
        val checkBoxesArray = arrayOf(chkLunes, chkMartes, chkMiercoles, chkJueves, chkViernes)
        mDbRef = FirebaseDatabase.getInstance().getReference("horarioAbogados")
        val query = mDbRef.orderByKey().equalTo(nombre)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        // Si se encuentra un horario en Firebase,
                        // Mostrar los datos del horario en los spinners
                        val dias = it.children
                        dias.forEach {
                            val dia = it.key.toString()
                            val inicio = it.child("inicio").value.toString()
                            val fin = it.child("fin").value.toString()
                            val index = when (dia) {
                                "Lunes" -> 0
                                "Martes" -> 1
                                "Miércoles" -> 2
                                "Jueves" -> 3
                                "Viernes" -> 4
                                else -> -1
                            }
                            if (index != -1) {
                                // Si ese día tiene un horario, marcar el checkbox y mostrar los spinners
                                // de lo contrario, desmarcar el checkbox y ocultar los spinners
                                if (inicio != "" && fin != "") {
                                    checkBoxesArray[index].isChecked = true
                                    spinnerDiasArray[index * 2].visibility = Spinner.VISIBLE
                                    spinnerDiasArray[(index * 2) + 1].visibility = Spinner.VISIBLE
                                    spinnerDiasArray[index * 2].setSelection((spinnerDiasArray[index * 2].adapter as ArrayAdapter<String>).getPosition(inicio))
                                    spinnerDiasArray[(index * 2) + 1].setSelection((spinnerDiasArray[(index * 2) + 1].adapter as ArrayAdapter<String>).getPosition(fin))
                                } else {
                                    checkBoxesArray[index].isChecked = false
                                    spinnerDiasArray[index * 2].visibility = Spinner.GONE
                                    spinnerDiasArray[(index * 2) + 1].visibility = Spinner.GONE
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@CrearAbogado,
                        "No se encontró un horario para el abogado",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }

    // Función para obtener el horario de los spinners y retornarlo como un array de pares
    private fun getHorario(): Array<Pair<String, String>> {
        val spinnerDiasArray = arrayOf(spLunesInicio, spLunesFin, spMartesInicio, spMartesFin, spMiercolesInicio, spMiercolesFin, spJuevesInicio, spJuevesFin, spViernesInicio, spViernesFin)
        val horario = arrayOf(Pair("", ""), Pair("", ""), Pair("", ""), Pair("", ""), Pair("", ""))
        for (i in 0..4) {
            if (spinnerDiasArray[i * 2].visibility == Spinner.VISIBLE) {
                val inicio = spinnerDiasArray[i * 2].selectedItem.toString()
                val fin = spinnerDiasArray[(i * 2) + 1].selectedItem.toString()
                horario[i] = Pair(inicio, fin)
            }
        }

        return horario
    }

    // Función para agregar el horario de un abogado a Firebase
    private fun agregarHorarioAFirebase(nombre: String, horario: Array<Pair<String, String>>) {
        mDbRef = FirebaseDatabase.getInstance().getReference("horarioAbogados")
        val horarioRef = mDbRef.child(nombre)
        val dias = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        var inicioDia = ""
        var finDia = ""
        for (i in 0..4) {
            // Añadir a firebase el horario de cada día
            inicioDia = horario[i].first
            finDia = horario[i].second
            horarioRef.child(dias[i]).setValue(
                mapOf(
                    "inicio" to inicioDia,
                    "fin" to finDia
                )
            )
        }

    }

    // Función para crear un abogado en Firebase
    private fun crearAbogado() {
        // Obtener los valores de los campos de texto
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val confirmarClave = txtConfirmarClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val cargo = spCargo.selectedItem.toString()
        val tema = spTema.selectedItem.toString()
        val estado = spEstado.selectedItem.toString()

        // Validacion de campos vacios
        if (nombre.isEmpty() || clave.isEmpty() || documento.isEmpty() || correo.isEmpty()) {
            Toast.makeText(
                this@CrearAbogado,
                "Diligencie todos los datos",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        // Verificar contraseñas iguales
        if (clave != confirmarClave) {
            Toast.makeText(
                this@CrearAbogado,
                "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        // Buscar en RealtimeDatabase si ya existe un usuario con el mismo documento

        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        val query = mDbRef.orderByChild("documento").equalTo(documento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(
                        this@CrearAbogado,
                        "Ya existe un abogado con el mismo documento",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    // Si no existe un usuario con el mismo documento, crear el usuario
                    var horario = Array<Pair<String, String>>(5) { Pair("", "") }
                    horario = getHorario()
                    // Validaciones Horario
                    // Validacion al menos 1 dia seleccionado
                    val checkBoxesArray = arrayOf(chkLunes, chkMartes, chkMiercoles, chkJueves, chkViernes)
                    val spinnerDiasArray = arrayOf(spLunesInicio, spLunesFin, spMartesInicio, spMartesFin, spMiercolesInicio, spMiercolesFin, spJuevesInicio, spJuevesFin, spViernesInicio, spViernesFin)
                    if (checkBoxesArray.all { !it.isChecked }) {
                        Toast.makeText(
                            this@CrearAbogado,
                            "Seleccione al menos un día de trabajo",
                            Toast.LENGTH_SHORT,
                        ).show()
                        return
                    }
                    // Validacion de horas dia, inicio < fin
                    for (i in 0..4) {
                        if (checkBoxesArray[i].isChecked) {
                            val inicio = spinnerDiasArray[i * 2].selectedItemPosition
                            val fin = spinnerDiasArray[(i * 2) + 1].selectedItemPosition
                            if (inicio >= fin) {
                                Toast.makeText(
                                    this@CrearAbogado,
                                    "Seleccione un horario válido para el día ${checkBoxesArray[i].text}",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                return
                            }
                        }
                    }
                    // Si las validaciones de horario son correctas, crear el usuario
                    mAuth.createUserWithEmailAndPassword(correo, clave).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = mAuth.currentUser
                            val uid = user?.uid
                            val abogado = Abogado(documento, nombre, cargo, tema, correo, estado)
                            agregarHorarioAFirebase(nombre, horario)

                            mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
                            mDbRef.child(uid!!).setValue(abogado).addOnSuccessListener {
                                Toast.makeText(
                                    this@CrearAbogado,
                                    "Abogado creado exitosamente",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@CrearAbogado,
                                    "Error al crear el abogado",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CrearAbogado,
                                "Error al crear el abogado",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })
    }

    // Función para modificar un abogado en Firebase
    private fun modificarAbogado() {
        // Obtener los valores de los campos de texto
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val cargo = spCargo.selectedItem.toString()
        val tema = spTema.selectedItem.toString()
        val estado = spEstado.selectedItem.toString()

        // Validacion de campos vacios
        if(documento.isEmpty()){
            Toast.makeText(
                this@CrearAbogado,
                "Ingrese cédula para modificar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }else{
            if (((sujeto != "propio") && (nombre.isEmpty() || clave.isEmpty() || correo.isEmpty())) || ((sujeto == "propio") && (nombre.isEmpty() || correo.isEmpty()))) {
                Toast.makeText(
                    this@CrearAbogado,
                    "Diligencie todos los datos",
                    Toast.LENGTH_SHORT,
                ).show()
                return
            }
        }

        var horario = Array<Pair<String, String>>(5) { Pair("", "") }
        horario = getHorario()
        // Validaciones Horario
        // Validacion al menos 1 dia seleccionado
        val checkBoxesArray = arrayOf(chkLunes, chkMartes, chkMiercoles, chkJueves, chkViernes)
        val spinnerDiasArray = arrayOf(spLunesInicio, spLunesFin, spMartesInicio, spMartesFin, spMiercolesInicio, spMiercolesFin, spJuevesInicio, spJuevesFin, spViernesInicio, spViernesFin)
        if (checkBoxesArray.all { !it.isChecked }) {
            Toast.makeText(
                this@CrearAbogado,
                "Seleccione al menos un día de trabajo",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        // Validacion de horas dia, inicio < fin
        for (i in 0..4) {
            if (checkBoxesArray[i].isChecked) {
                val inicio = spinnerDiasArray[i * 2].selectedItemPosition
                val fin = spinnerDiasArray[(i * 2) + 1].selectedItemPosition
                if (inicio >= fin) {
                    Toast.makeText(
                        this@CrearAbogado,
                        "Seleccione un horario válido para el día ${checkBoxesArray[i].text}",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return
                }
            }
        }

        // Si las validaciones de horario son correctas, modificar el usuario
        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        val abogado = Abogado(documento, nombre, cargo, tema, correo, estado)
        agregarHorarioAFirebase(nombre, horario)

        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        mDbRef.child(uidConsultado).setValue(abogado).addOnSuccessListener {
            Toast.makeText(
                this@CrearAbogado,
                "Abogado modificado exitosamente",
                Toast.LENGTH_SHORT,
            ).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@CrearAbogado,
                "Error al modificar el abogado",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    // Función para eliminar un abogado en Firebase
    private fun eliminarAbogado() {
        // Validacion de ID consultado
        if(uidConsultado.isEmpty()){
            Toast.makeText(
                this@CrearAbogado,
                "Ingrese cédula para eliminar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        // Eliminar el abogado de Firebase una vez confirmado
        mDbRef = FirebaseDatabase.getInstance().getReference("abogadoData")
        mDbRef.child(uidConsultado).removeValue().addOnSuccessListener {
            Toast.makeText(
                this@CrearAbogado,
                "Abogado eliminado exitosamente",
                Toast.LENGTH_SHORT,
            ).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@CrearAbogado,
                "Error al eliminar el abogado",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}