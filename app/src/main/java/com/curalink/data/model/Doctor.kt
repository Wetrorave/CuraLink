package com.curalink.data.model

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialty: String = "",
    val location: String = "",
    val biography: String = "",
    val availableSlots: List<String> = emptyList()
)
