package com.personeriatocancipa.app

class Abogado {
    var documento: String? = null // Llave primaria
    var nombreCompleto: String? = null // Llave primaria
    var cargo: String? = null
    var tema: String? = null
    var correo: String? = null
    var estado: String? = null // Activo, inactivo

    constructor()

    constructor(
        documento: String?,
        nombreCompleto: String?,
        cargo: String?,
        tema: String?,
        correo: String?,
        estado: String?
    ) {
        this.documento = documento
        this.nombreCompleto = nombreCompleto
        this.cargo = cargo
        this.tema = tema
        this.correo = correo
        this.estado = estado
    }

}