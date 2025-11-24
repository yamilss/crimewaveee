package com.example.crimewavee.data.model

data class ShippingAddress(
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
