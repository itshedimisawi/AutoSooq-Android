package com.example.googlelogindemo.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Favorite(
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("post")
    val post: Post,
) : Parcelable