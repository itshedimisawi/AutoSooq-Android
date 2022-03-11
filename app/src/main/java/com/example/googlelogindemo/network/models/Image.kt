package com.example.googlelogindemo.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    @SerializedName("url")
    val url: String = "",
    @SerializedName("image")
    val thumbnail: String = ""
) : Parcelable