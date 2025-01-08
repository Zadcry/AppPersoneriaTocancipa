package com.personeriatocancipa.app

//Nota: Los valores que se agregan a los spinners se encuentran en la carpeta "res"
// y luego dentro se selecciona "values" y en "strings.xml" se puede editar los valores.

// Clase que representa una cita con información detallada sobre el agendamiento
class Cita {
    var id: Int? = null  // Identificador único para la cita (Autonumérico)
    var descripcion: String? = null // Descripción breve de la cita
    var fecha: String? = null // Fecha programada para la cita (formato: dd-mm-yyyy)
    var hora: String? = null // Hora programada para la cita (formato: HH:MM)
    var correoAbogado: String? = null // Correo electrónico del abogado asignado a la cita
    var correoCliente: String? = null // Correo electrónico del cliente (dirección a la cual se enviarán notificaciones)
    var tema: String? = null // Tema principal de la cita (Salud, Familia, Medio Ambiente etc.)
    var autorizaCorreo: String? = null // Indica si el cliente autoriza el envío de correos electrónicos (Sí o No)
    var correoVigente: String? = null // Indica si el correo del cliente es vigente (Sí o No)
    var estado: String? = null // Estado actual de la cita (Pendiente, Cancelada, etc.)

    // Constructor vacío para inicializar un objeto Cita sin parámetros
    constructor()

    // Constructor con parámetros para inicializar un objeto Cita con valores específicos
    constructor(
        id: Int?,
        descripcion: String?,
        fecha: String?,
        hora: String?,
        correoAbogado: String?,
        correoCliente: String?,
        tema: String?,
        autorizaCorreo: String?,
        correoVigente: String?,
        estado: String?
    ) {
        // Asignación de valores proporcionados a las propiedades de la clase
        this.id = id
        this.descripcion = descripcion
        this.fecha = fecha
        this.hora = hora
        this.correoAbogado = correoAbogado
        this.correoCliente = correoCliente
        this.tema = tema
        this.autorizaCorreo = autorizaCorreo
        this.correoVigente = correoVigente
        this.estado = estado
    }

}