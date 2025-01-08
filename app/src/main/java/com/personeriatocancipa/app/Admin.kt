package com.personeriatocancipa.app
// Nota: Los valores que se agregan a los spinners se encuentran en la carpeta "res"
// y luego dentro se selecciona "values" y en "strings.xml" se puede editar los valores.

// Clase que representa un administrador con información personal y de contacto
class Admin {
    var cedula: String? = null // Documento de identificación del administrador (LLave primaria)
    var nombreCompleto: String? = null // Nombre completo del administrador
    var correo: String? = null //Correo electrónico del administrador
    var estado: String? = null // Estado del abogado (Activo, inactivo)

    // Constructor vacío para inicializar un objeto Admin sin parámetros
    constructor()

    // Constructor con parámetros para inicializar un objeto Admin con valores específicos
    constructor(
        cedula: String?,
        nombreCompleto: String?,
        correo: String?,
        estado: String?,
    ) {
        // Asignación de valores proporcionados a las propiedades de la clase
        this.cedula = cedula
        this.nombreCompleto = nombreCompleto
        this.correo = correo
        this.estado = estado
    }

}