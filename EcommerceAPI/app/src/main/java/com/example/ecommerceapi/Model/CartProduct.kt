package com.example.ecommerceapi.Model

data class CartProduct(
    val product: Product,
    val cartid: Int = 0,
    val size: Int = 0,
    val quantity: Int = 0,

)
