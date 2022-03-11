package com.example.googlelogindemo.network.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Carmodel(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("make")
    val make: String = "",
    @SerializedName("model")
    val model: String = ""
) : Parcelable