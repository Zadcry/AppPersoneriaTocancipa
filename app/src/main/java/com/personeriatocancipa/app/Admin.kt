package com.personeriatocancipa.app

class Admin {
    var cedula: String? = null // LLave primaria
    var nombreCompleto: String? = null // LLave primaria
    var correo: String? = null
    var estado: String? = null // Activo, inactivo
    var rol: String? ="2"

    constructor()

    constructor(
        cedula: String?,
        nombreCompleto: String?,
        correo: String?,
        estado: String?,
        uid: String?
    ) {
        this.cedula = cedula
        this.nombreCompleto = nombreCompleto
        this.correo = correo
        this.estado = estado
        this.rol="2"
    }

}