package com.personeriatocancipa.app

class Usuario {

    var nombreCompleto: String? = null
    var documento: String? = null
    var edad: Int? = null
    var direccion: String? = null
    var telefono: String? = null
    var correo: String? = null
    var sexo: String? = null
    var escolaridad: String? = null
    var grupoSi: String? = null
    var grupo: String? = null
    var comunidad: String? = null
    var uid: String? = null

    constructor(
        nombreCompleto: String?,
        documento: String?,
        edad: Int?,
        direccion: String?,
        telefono: String?,
        correo: String?,
        sexo: String?,
        escolaridad: String?,
        grupoSi: String?,
        grupo: String?,
        comunidad: String?,
        uid: String?
    ) {
        this.nombreCompleto = nombreCompleto
        this.documento = documento
        this.edad = edad
        this.direccion = direccion
        this.telefono = telefono
        this.correo = correo
        this.sexo = sexo
        this.escolaridad = escolaridad
        this.grupoSi = grupoSi
        this.grupo = grupo
        this.comunidad = comunidad
        this.uid = uid
    }

    constructor(){

    }
}