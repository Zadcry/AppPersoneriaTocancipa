package com.personeriatocancipa.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Clase dedicada a la creación y gestión de administradores
class CrearAdmin : AppCompatActivity() {

    // Declaración de variables para elementos de la interfaz
    private lateinit var gridConsultar: LinearLayout // LinearLayout para consultar administradores existentes
    private lateinit var txtAnuncio:TextView // Texto para mostrar el título de la interfaz
    private lateinit var txtNombre: EditText // Campo de texto para ingresar el nombre del administrador
    private lateinit var txtConsultar: EditText // Campo de texto para ingresar la cédula del administrador a consultar
    private lateinit var txtClave: EditText // Campo de texto para ingresar la contraseña del administrador
    private lateinit var txtConfirmarClave: EditText // Campo de texto para confirmar la contraseña del administrador
    private lateinit var txtDocumento: EditText // Campo de texto para ingresar la cédula del administrador
    private lateinit var spEstado: Spinner // Spinner para seleccionar el estado del administrador
    private lateinit var txtCorreo: EditText // Campo de texto para ingresar el correo del administrador
    private lateinit var btnConsultar: Button // Botón para consultar un administrador
    private lateinit var btnSalir: Button // Botón para salir de la interfaz
    private lateinit var btnSignUp: Button // Botón para crear un nuevo administrador
    private lateinit var btnModificar: Button // Botón para modificar un administrador
    private lateinit var btnEliminar: Button // Botón para eliminar un administrador
    private lateinit var btnTogglePassword: Button // Botón para mostrar u ocultar la contraseña
    private lateinit var btnToggleCheckPassword: Button // Botón para mostrar u ocultar la información en confirmación de la contraseña
    private lateinit var tvClave: TextView // Texto para mostrar la contraseña
    private lateinit var tvConfirmarClave: TextView // Texto para mostrar la confirmación de la contraseña
    private lateinit var tvDocumento: TextView // Texto para mostrar la cédula del administrador
    private lateinit var mAuth: FirebaseAuth // Variable para autenticación de Firebase
    private lateinit var mDbRef: DatabaseReference // Variable para referencia a la base de datos de Firebase
    private var tarea: String = "" // Variable para almacenar la tarea a realizar
    private var uidConsultado: String = "" // Variable para almacenar el uid del administrador consultado

    // Función que se ejecuta al crear la interfaz
    @SuppressLint("ResourceType", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_admin)

        supportActionBar?.hide()

        // Inicialización de Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Inicialización de valores para Spinner de estado
        spEstado = findViewById(R.id.spEstado)
        ArrayAdapter.createFromResource(
            this,
            R.array.opcionesEstadoCargo,
            R.drawable.spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item)
            spEstado.adapter = adapter
        }

        // Inicialización de variables para elementos de la interfaz
        gridConsultar = findViewById(R.id.gridConsultar)
        txtAnuncio = findViewById(R.id.txtAnuncio)
        txtConsultar = findViewById(R.id.txtConsultar)
        txtNombre = findViewById(R.id.txtNombre)
        txtClave = findViewById(R.id.txtClave)
        txtConfirmarClave = findViewById(R.id.txtConfirmarClave)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtDocumento = findViewById(R.id.txtDocumento)
        btnSalir = findViewById(R.id.btnSalir)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnModificar = findViewById(R.id.btnModificar)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleCheckPassword = findViewById(R.id.btnToggleCheckPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvClave = findViewById(R.id.tvClave)
        tvConfirmarClave = findViewById(R.id.tvConfirmarClave)
        tvDocumento = findViewById(R.id.tvDocumento)
        tarea = intent.getStringExtra("tarea").toString()

        // Mostrar u ocultar contraseña según el botón
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

        // Mostrar u ocultar información en confirmar contraseña según el botón
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

        if(tarea == "crear"){ // Si se crea un administrador, mostrar elementos correspondientes
            txtAnuncio.text = "Crear Administrador"
            gridConsultar.visibility = GridLayout.GONE
            btnSignUp.visibility = Button.VISIBLE
            btnModificar.visibility = Button.GONE
            btnEliminar.visibility = Button.GONE
        }else { // Si se consulta, modifica o elimina un administrador, mostrar elementos correspondientes
            txtAnuncio.text = "Gestión de Administrador"
            gridConsultar.visibility = View.VISIBLE
            tvDocumento.visibility = TextView.GONE
            tvDocumento.isEnabled = false
            txtDocumento.visibility = EditText.GONE
            txtDocumento.isEnabled = false
            txtCorreo.isEnabled = false
            when (tarea) { // Mostrar u ocultar elementos según la tarea
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
                    disableFields() // Deshabilitar campos
                }
                "modificar" -> {
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
                    disableFields() // Deshabilitar campos
                }
            }
        }
        // Evento del botón salir
        btnSalir.setOnClickListener {
            finish() // Finalizar la actividad y volver a la anterior
        }

        // Evento del botón crear administrador
        btnSignUp.setOnClickListener {
            // Crear y configurar ventana emergente
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Política de Tratamiento de Datos Personales") // Título de la ventana
            builder.setMessage("Señor(a) usuario(a), autoriza a la Personería de Tocancipá, para la recolección, consulta, almacenamiento, uso, traslado o eliminación de sus datos personales, con el fin de: adelantar las gestiones, actuaciones e intervenciones que permitan el restablecimiento y goce de sus derechos, invitar a eventos de participación ciudadana u organizados por la entidad,  información con fines estadísticos, enviar información a entidades autorizadas cuando la solicitud lo amerite, evaluar la calidad del servicio y contactar al titular en los casos que se considere necesario. \n" +
                    "\n" +
                    "No es obligatorio para la prestación del servicio, suministrar los datos personales de carácter sensible o de niños, niñas y adolescentes. Se exime el tratamiento de datos de niños, niñas y adolescentes, salvo aquellos datos que sean de naturaleza pública. \n" +
                    "Como titular de la información tiene derecho a conocer, actualizar y rectificar sus datos personales, así como  suprimir o revocar la autorización otorgada para su tratamiento, pedir prueba de la autorización otorgada al responsable del tratamiento y ser informado sobre el uso que le han dado a los mismos, presentar quejas ante la Superintendencia de Industria y Comercio SIC por infracción a la ley y acceder en forma gratuita a sus datos personales, acorde al o establecido en los artículos 15, 20 y 74 de la Constitución Política, , la Ley 1581 del 2012, el Decreto Reglamentario 1377 de 2013, la Ley 1266 del 31 de 2008, el Decreto Reglamentario 1727 de  2009, y demás normatividad vigente relacionada. \n" +
                    "Consulte la Política de Tratamiento de Datos Personales Personería Municipal de Tocancipá adoptada mediante Resolución Administrativa 051 de 2020 en el siguiente link https://www.personeria-tocancipa.gov.co/politicas-y-lineamientos/politica-de-tratamiento-de-datos\n" +
                    "¿Acepta términos y condiciones?\n") // Mensaje de la ventana

            // Agregar botones de acción
            builder.setPositiveButton("Sí") { dialog, which -> // Si se acepta la política
                // Continuar con la creación de la cuenta
                val campos = conseguirCampos() // Obtener valores de los campos
                if (!verificarCampos(campos)) { // Verificar que los campos estén diligenciados
                    Toast.makeText( // Mostrar mensaje a usuario
                        this@CrearAdmin,
                        "Diligencie todos los datos",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setPositiveButton
                } else{
                    // Obtener valores de los campos
                    val nombre = campos[0]
                    val clave = campos[1]
                    val confirmarClave = campos[2]
                    val documento = campos[3]
                    val correo = campos[4]
                    val estado = campos[5]

                    if(clave != confirmarClave){ // Verificar que las contraseñas coincidan
                        Toast.makeText( // Mostrar mensaje a usuario
                            this@CrearAdmin,
                            "Las contraseñas no coinciden",
                            Toast.LENGTH_SHORT,
                        ).show()
                        return@setPositiveButton
                    }else{ // Si las contraseñas coinciden
                        // Crear cuenta de administrador
                        signUp(nombre, clave, documento,
                            correo, estado)
                    }
                }
            }

            builder.setNegativeButton("No") { dialog, which -> // Si se rechaza la política
                // Cancelar el flujo
                dialog.dismiss()
                Toast.makeText( // Mostrar mensaje a usuario
                    this@CrearAdmin,
                    "Debe aceptar la política de tratamiento de datos personales para continuar",
                    Toast.LENGTH_SHORT,
                ).show()
            }

            // Mostrar el diálogo de permiso de habeas data
            builder.create().show()
        }

        // Evento del botón consultar
        btnConsultar.setOnClickListener{
            consultarPorCedula() // Consultar administrador por cédula
        }

        // Evento del botón modificar
        btnModificar.setOnClickListener{
            val campos = conseguirCampos() // Obtener valores de los campos en forma de array
            if(campos[3].isEmpty()){ // Si no se ingresa cédula para modificar
                Toast.makeText( // Mostrar mensaje a usuario
                    this@CrearAdmin,
                    "Ingrese cédula para modificar",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }else{ // Si se ingresa cédula para modificar
                if (!verificarCampos(campos)) { // Verificar que los campos estén diligenciados
                    Toast.makeText( // Mostrar mensaje a usuario
                        this@CrearAdmin,
                        "Diligencie todos los datos",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@setOnClickListener
                } else{ // Si los campos están diligenciados
                    val nombre = campos[0]
                    val documento = campos[3]
                    val correo = campos[4]
                    val estado = campos[5]

                    // Modificar administrador en la base de datos

                    // Buscar en RealtimeDatabase la sección de administradores
                    mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                    // Asignar nuevos valores al administrador
                    mDbRef.child(uidConsultado).setValue(
                        Admin(documento,nombre,correo,
                            estado))
                    Toast.makeText( // Mostrar mensaje a usuario
                        this@CrearAdmin,
                        "Administrador modificado exitosamente",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }

        // Evento del botón eliminar
        btnEliminar.setOnClickListener{
            if(uidConsultado.isEmpty()){ // Si no se ha consultado ningún administrador antes de oprimir botón eliminar
                Toast.makeText( // Mostrar mensaje a usuario
                    this@CrearAdmin,
                    "Ingrese cédula para eliminar",
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }else{ // Si se ha consultado un administrador antes de oprimir botón eliminar
                // Buscar en RealtimeDatabase la sección de administradores
                mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
                // Eliminar administrador de la base de datos
                mDbRef.child(uidConsultado).removeValue()
                Toast.makeText( // Mostrar mensaje a usuario
                    this@CrearAdmin,
                    "Administrador eliminado exitosamente",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    // Función para deshabilitar campos
    private fun disableFields(){
        txtNombre.isEnabled = false
        txtClave.isEnabled = false
        txtConfirmarClave.isEnabled = false
        btnTogglePassword.isEnabled = false
        btnToggleCheckPassword.isEnabled = false
        txtDocumento.isEnabled = false
        txtCorreo.isEnabled = false
        spEstado.isEnabled = false
    }

    // Función para crear un administrador
    private fun signUp(
        nombre: String,
        clave:String,
        documento:String,
        correo:String,
        estado:String
    )
    {
        // Buscar en RealtimeDatabase si ya existe un administrador con el mismo documento
        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")
        val query = mDbRef.orderByChild("cedula").equalTo(documento)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) { // Si ya existe un administrador con el mismo documento
                    Toast.makeText( // Mostrar mensaje a usuario
                        this@CrearAdmin,
                        "Ya existe un usuario con la cédula ingresada",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else { // Si no existe un administrador con el mismo documento
                    // Crear cuenta de administrador en Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(correo,clave)
                        .addOnCompleteListener(this@CrearAdmin){
                                task ->
                            if (task.isSuccessful){ // Si la creación en Firebase Authentication es exitosa
                                // Crear cuenta de administrador en RealtimeDatabase
                                addUserToDatabase(nombre, documento, correo, estado, mAuth.currentUser?.uid!!)
                                Toast.makeText( // Mostrar mensaje a usuario
                                    this@CrearAdmin,
                                    "Administrador creado exitosamente",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                // Redirigir a la interfaz de administrador con el nuevo administrador
                                val intent = Intent(this@CrearAdmin, InterfazAdmin::class.java)
                                finish()
                                startActivity(intent)
                            }
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText( // Mostrar mensaje a usuario
                    this@CrearAdmin,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
                println(error)
            }
        })
    }

    // Función para obtener valores de los campos en forma de Array
    private fun conseguirCampos(): Array<String> {
        val nombre = txtNombre.text.toString()
        val clave = txtClave.text.toString()
        val confirmarClave = txtConfirmarClave.text.toString()
        val documento = txtDocumento.text.toString()
        val correo = txtCorreo.text.toString()
        val estado = spEstado.selectedItem.toString()

        return arrayOf(nombre, clave, confirmarClave, documento, correo, estado)
    }

    // Función para verificar que los campos estén diligenciados
    private fun verificarCampos(campos: Array<String>): Boolean {
        //Verifica que todos los campos estén diligenciados
        return if(tarea != "crear"){ // Si no se está creando un administrador
            // Verifica nombre y correo
            !(campos[0].isEmpty() || campos[4].isEmpty())
        }else{ // Si se está creando un administrador
            // Verifica todos los campos
            !(campos[0].isEmpty() || campos[1].isEmpty() || campos[2].isEmpty() || campos[3].isEmpty() || campos[4].isEmpty())
        }
    }

    // Función para agregar un administrador a la base de datos
    private fun addUserToDatabase(nombre: String, documento: String, correo: String, estado: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        // Agregar objeto tipo Admin. a la base de datos
        mDbRef.child("AdminData").child(uid).setValue(
            Admin(documento, nombre, correo, estado))
        Toast.makeText( // Mostrar mensaje a usuario
            this@CrearAdmin,
            "Cuenta creada exitosamente",
            Toast.LENGTH_SHORT,
        ).show()

    }

    // Función para consultar un administrador por cédula
    private fun consultarPorCedula() {
        val cedula = txtConsultar.text.toString() // Obtener cédula ingresada
        if (cedula.isEmpty()) { // Si no se ingresa cédula para consultar
            Toast.makeText( // Mostrar mensaje a usuario
                this@CrearAdmin,
                "Ingrese cédula para consultar",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        // Buscar en RealtimeDatabase la sección de administradores
        mDbRef = FirebaseDatabase.getInstance().getReference("AdminData")

        // Buscar en RealtimeDatabase el administrador con la cédula ingresada
        val query = mDbRef.orderByChild("cedula").equalTo(cedula)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot)
                if (snapshot.exists()) { // Si se encuentra un administrador con la cédula ingresada
                    snapshot.children.forEach {
                        uidConsultado = it.key.toString() // Guardar uid del administrador consultado en variable global
                        println(it)
                        val nombre = it.child("nombreCompleto").value.toString() // Obtener nombre del administrador
                        println(nombre)
                        val documento = it.child("cedula").value.toString() // Obtener cédula del administrador
                        val estado = it.child("estado").value.toString() // Obtener estado del administrador
                        val correo = it.child("correo").value.toString() // Obtener correo del administrador

                        txtNombre.setText(nombre) // Mostrar nombre del administrador
                        txtClave.setText("********") // Mostrar asteriscos en campo de contraseña por privacidad
                        txtDocumento.setText(documento) // Mostrar cédula del administrador
                        txtCorreo.setText(correo) // Mostrar correo del administrador
                        spEstado.setSelection((spEstado.adapter as ArrayAdapter<String>).getPosition(estado)) // Mostrar estado del administrador
                    }
                    if(tarea == "modificar"){ // Si se va a modificar un administrador
                        // Mostrar botón de modificar
                        btnModificar.visibility = Button.VISIBLE
                    }else if(tarea == "eliminar"){ // Si se va a eliminar un administrador
                        // Mostrar botón de eliminar
                        btnEliminar.visibility = Button.VISIBLE
                    }
                } else { // Si no se encuentra un administrador con la cédula ingresada
                    Toast.makeText( // Mostrar mensaje a usuario
                        this@CrearAdmin,
                        "No se encontró un usuario con la cédula ingresada",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText( // Mostrar mensaje a usuario
                    this@CrearAdmin,
                    "Error al consultar la base de datos",
                    Toast.LENGTH_SHORT,
                ).show()
                println(error) // Imprimir error en consola
            }
        })
    }
}