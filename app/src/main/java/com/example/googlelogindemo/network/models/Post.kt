package com.example.googlelogindemo.network.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    @SerializedName("body_type")
    val bodyType: String? = null,
    @SerializedName("carmodel")
    val carmodel: Carmodel? = Carmodel(),
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("fuel_type")
    val fuelType: String? = null,
    @SerializedName("id")
    val id: Int,
    @SerializedName("images")
    val images: List<Image>? = listOf(),
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("mileage")
    val mileage: Int? = null,
    @SerializedName("displacement")
    val displacement: Int? = null,
    @SerializedName("owner_phone")
    val ownerPhone: String? = "",
    @SerializedName("price")
    val price: Int,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("registration_date")
    val registrationDate: String? = null,
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("transmission")
    val transmission: String? = null,
    @SerializedName("created_at")
    val created_at: String? = null,
    @SerializedName("user_id")
    val userId: Int? = 0
) : Parcelable