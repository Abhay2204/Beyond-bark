package com.example.beyondbark.model

data class RescuePet(
    val petId: String = "",
    val commonName: String = "",
    val breed: String = "",
    val location: String = "",
    val condition: String = "",
    val registeredBy: String = "",
    val registeredByEmail: String = "",
    val registerDate: Long = 0L,
    val rescued: Boolean = false, // changed from isRescued
    val rescuedBy: String? = null,
    val rescuedByEmail: String? = null,
    val rescueDate: Long? = null
)
