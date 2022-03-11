package com.example.googlelogindemo.network.models


import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("auth_token")
    val authToken: String
)