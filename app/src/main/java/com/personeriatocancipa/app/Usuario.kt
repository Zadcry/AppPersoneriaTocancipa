package com.personeriatocancipa.app

// Nota: Los valores que se agregan a los spinners se encuentran en la carpeta "res"
// y luego dentro se selecciona "values" y en "strings.xml" se puede editar los valores.

// Clase que representa un usuario con información personal y socioeconómica
class Usuario {

    var nombreCompleto: String? = null //Nombre completo del usuario
    var tipoDocumento: String? = null // Tipo de documento del usuario (CC, CE, NIT, Pasaporte, PEP, PTP, RC, TI)
    var documento: String? = null // Número de documento del usuario
    var fechaNacimiento: String? = null // Fecha de nacimiento del usuario (formato: dd-mm-yyyy)
    var grupoEtario: String? = null // Grupo etario al que pertenece el usuario (Primera infancia, infancia, adolescencia, juventud, adultez, persona mayor
    var edad: Int? = null // Edad del usuario (Se calcula a partir de la fecha de nacimiento)
    var direccion: String? = null // Dirección de residencia del usuario
    var sector: String? = null // Sector o barrio donde reside el usuario (Betania, Bohio, La Aurora...)
    var telefono: String? = null // Número de teléfono del usuario
    var correo: String? = null // Correo electrónico del usuario
    var sexo: String? = null // Sexo del usuario (Hombre, Mujer, Intersexual)
    var identidad: String? = null // Identidad de género del usuario (Femenino, Masculino, Transgénero, No deseo informar)
    var orientacion: String? = null // Orientación sexual del usuario (Bisexual, Heterosexual, Homosexual, No deseo informar)
    var nacionalidad: String? = null // Nacionalidad del usuario
    var escolaridad: String? = null // Nivel de escolaridad alcanzado por el usuario (Primaria, Secundaria, etc)
    var grupoEtnico: String? = null // Grupo étnico al que pertenece el usuario (Afrocolombiano, indígena, ROM, raizal, palenquero, gitano, no pertenece a ninguno)
    var discapacidad: String? = null // Tipo de discapacidad del usuario (Auditiva, física, intelectual, visual, sordoceguera, psicosocial, múltiple, ninguna)
    var estrato: String? = null // Estrato socioeconómico del usuario (1, 2, 3,..., No informa)
    var comunidad: String? = null // Comunidad específica a la que pertenece el usuario (Madre Cabeza de Familia, Víctima del conflicto armado, Discapacidad, Adulto Mayor, LGBTIQ+, Ninguna, Otros)
    var estado: String? = null // Estado del usuario (Activo, inactivo)
    var uid: String? = null  // Identificador único del usuario


    // Constructor vacío para inicializar un objeto Usuario sin parámetros
    constructor() {

    }

    // Constructor con parámetros para inicializar un objeto Usuario con valores específicos
    constructor(
        nombreCompleto: String?,
        tipoDocumento: String?,
        documento: String?,
        fechaNacimiento: String?,
        grupoEtario: String?,
        edad: Int?,
        direccion: String?,
        sector: String?,
        telefono: String?,
        correo: String?,
        sexo: String?,
        identidad: String?,
        orientacion: String?,
        nacionalidad: String?,
        escolaridad: String?,
        grupoEtnico: String?,
        discapacidad: String?,
        estrato: String?,
        comunidad: String?,
        estado: String?,
        uid: String?
    ) {
        // Asignación de valores proporcionados a las propiedades de la clase
        this.nombreCompleto = nombreCompleto
        this.tipoDocumento = tipoDocumento
        this.documento = documento
        this.fechaNacimiento = fechaNacimiento
        this.grupoEtario = grupoEtario
        this.edad = edad
        this.direccion = direccion
        this.sector = sector
        this.telefono = telefono
        this.correo = correo
        this.sexo = sexo
        this.identidad = identidad
        this.orientacion = orientacion
        this.nacionalidad = nacionalidad
        this.escolaridad = escolaridad
        this.grupoEtnico = grupoEtnico
        this.discapacidad = discapacidad
        this.estrato = estrato
        this.comunidad = comunidad
        this.estado = estado
        this.uid = uid
    }


}