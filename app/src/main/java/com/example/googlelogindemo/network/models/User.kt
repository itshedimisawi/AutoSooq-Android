package com.example.googlelogindemo.network.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email")
    var email: String = "",
    @SerializedName("first_name")
    var firstName: String = "",
    @SerializedName("last_name")
    var lastName: String = "",
    @SerializedName("google_acc")
    var googleAcc: String? = null
)