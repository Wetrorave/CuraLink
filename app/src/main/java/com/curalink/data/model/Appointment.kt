package com.curalink.data.model

data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val patientEmail: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "Agendado"
)
