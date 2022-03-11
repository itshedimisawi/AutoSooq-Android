package com.example.googlelogindemo.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostFilter (
    var ordering : String  = "",
    var search : String  = "",
    //Filters
    var make : String  = "",
    var model : String  = "",
    var color : String  = "",
    var fuel_type : String  = "",
    var body_type : String  = "",
    var location : String  = "",
    var mileage_gt :Int? = null,
    var mileage_lt:Int? = null,
    var displacement_gt:Int? = null,
    var displacement_lt:Int? = null,
    var price_gt :Int? = null,
    var price_lt :Int? = null,
    var transmission :String = "",
    var type :Int? = null,
) : Parcelable