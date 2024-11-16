
package com.sprzd.Examen02_Moviles.entities

import java.io.Serializable

data class cls_Product(
    val id: Long,
    val nombre: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val descuento: Int
) : Serializable  // Implementa Serializable
