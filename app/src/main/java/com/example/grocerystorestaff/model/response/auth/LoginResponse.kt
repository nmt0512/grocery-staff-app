package com.example.grocerystorestaff.model.response.auth

data class LoginResponse(
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String
)