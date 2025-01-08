package com.personeriatocancipa.app

// Nota: Los valores que se agregan a los spinners se encuentran en la carpeta "res"
// y luego dentro se selecciona "values" y en "strings.xml" se puede editar los valores.

// Clase que representa a un abogado con información personal y profesional

class Abogado {
    var documento: String? = null // Documento de identificación del abogado (Llave primaria)
    var nombreCompleto: String? = null // Nombre completo del abogado
    var cargo: String? = null //Cargo del abogado dentro de la entidad
    var tema: String? = null  //Tema que el abogado aborda especificamente
    var correo: String? = null //Correo electrónico del abogado
    var estado: String? = null // Estado del abogado (Activo, inactivo)

    // Constructor vacío para inicializar un objeto Abogado sin parámetros
    constructor() {}

    // Constructor con parámetros para inicializar un objeto Abogado con valores específicos
    constructor(
        documento: String?,
        nombreCompleto: String?,
        cargo: String?,
        tema: String?,
        correo: String?,
        estado: String?
    ) {
        // Asignación de valores proporcionados a las propiedades de la clase
        this.documento = documento
        this.nombreCompleto = nombreCompleto
        this.cargo = cargo
        this.tema = tema
        this.correo = correo
        this.estado = estado
    }

}