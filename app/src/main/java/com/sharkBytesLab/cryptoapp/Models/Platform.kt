package com.sharkBytesLab.cryptoapp.Models

data class Platform(
    val id: Int,
    val name: String,
    val slug: String,
    val symbol: String,
    val token_address: String
)