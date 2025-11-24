package com.example.crimewavee.data.model

data class BillingAddress(
    val nombre: String = "",
    val apellidos: String = "",
    val direccion: String = "",
    val rut: String = "",
    val ciudad: String = "",
    val codigoPostal: String = "",
    val pais: String = "Chile",
    val region: String = "",
    val celular: String = "",
    val instagram: String = ""
)
