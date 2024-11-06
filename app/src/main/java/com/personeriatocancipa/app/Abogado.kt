package com.personeriatocancipa.app

class Abogado {
    var cedula: String? = null // LLave primaria
    var seccion: String? = null // Consultorio jurídico, tutela, otro, etc.
    var correo: String? = null
    var contrasena: String? = null
    var estado: String? = null // Activo, inactivo

    constructor()

    constructor(
        cedula: String?,
        seccion: String?,
        correo: String?,
        contrasena: String?,
        estado: String?
    ) {
        this.cedula = cedula
        this.seccion = seccion
        this.correo = correo
        this.contrasena = contrasena
        this.estado = estado
    }

}