package com.personeriatocancipa.app

class Admin {
    var cedula: String? = null // LLave primaria
    var nombreCompleto: String? = null // LLave primaria
    var correo: String? = null
    var estado: String? = null // Activo, inactivo

    constructor()

    constructor(
        cedula: String?,
        nombreCompleto: String?,
        correo: String?,
        estado: String?,
    ) {
        this.cedula = cedula
        this.nombreCompleto = nombreCompleto
        this.correo = correo
        this.estado = estado
    }

}