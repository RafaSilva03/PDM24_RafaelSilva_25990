package com.example.ecommerceapi.Model

data class Product(
    val name: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val brand: String = ""
)
