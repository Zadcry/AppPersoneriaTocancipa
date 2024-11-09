package com.personeriatocancipa.app

class Cita {
    var id: Int? = null // Autonumérico
    var descripcion: String? = null
    var fecha: String? = null
    var hora: String? = null
    var correoAbogado: String? = null
    var correoCliente: String? = null // A dónde se notifica
    var tipo: String? = null // Consultorio jurídico, tutela, otro, etc.
    var estado: String? = null // Pendiente, cancelada, realizada, etc.

    constructor()

    constructor(
        id: Int?,
        descripcion: String?,
        fecha: String?,
        hora: String?,
        correoAbogado: String?,
        correoCliente: String?,
        tipo: String?,
        estado: String?
    ) {
        this.id = id
        this.descripcion = descripcion
        this.fecha = fecha
        this.hora = hora
        this.correoAbogado = correoAbogado
        this.correoCliente = correoCliente
        this.tipo = tipo
        this.estado = estado
    }

}