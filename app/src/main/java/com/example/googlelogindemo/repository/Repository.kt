package com.example.googlelogindemo.repository

import android.net.Uri
import com.example.googlelogindemo.network.models.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import java.io.InputStream

interface Repository {

    var token: String?

    //Auth
    suspend fun login(email: String, password: String): Response<Login>
    suspend fun signup(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Response<Login>

    suspend fun google_login(google_token: String): Response<Login>

    suspend fun logout(): Response<ResponseBody>

    suspend fun changeEmail(password: String, newEmail: String): Response<ResponseBody>

    suspend fun changePassword(password: String, newPassword: String): Response<ResponseBody>

    suspend fun changeInfos(firstName: String?, lastName: String?): Response<ResponseBody>

    suspend fun getProfile(): Response<User>

    suspend fun getPosts(
        page: Int? = null,
        ordering: String? = null, //price or -price
        search: String? = null,
        //Filters
        make: String? = null,
        model: String? = null,
        color: String? = null,
        fuel_type: String? = null,
        body_type: String? = null,
        location: String? = null,
        mileage_gt: Int? = null,
        mileage_lt: Int? = null,
        price_gt: Int? = null,
        price_lt: Int? = null,
        displacement_gt: Int? = null,
        displacement_lt: Int? = null,
        transmission: String? = null,
        type: Int? = null,
    ): Response<List<Post>>

    suspend fun getMyPosts(): Response<List<Post>>

    suspend fun addPost(
        title: String? = null,
        description: String? = null,
        model: Int? = null,
        color: String? = null,
        fuel_type: String? = null,
        body_type: String? = null,
        location: String? = null,
        mileage: Int? = null,
        price: Int? = null,
        displacement: Int? = null,
        transmission: String? = null,
        phone: String? = null,
        type: Int? = null,
        images: List<File> = listOf()
    ):Response<ResponseBody>

    suspend fun deletePost(postId:Int): Response<ResponseBody>

    suspend fun getFavorites(): Response<List<Favorite>>

    suspend fun addToFavorite(postId: Int): Response<ResponseBody>

    suspend fun deleteFavorite(postId: Int): Response<ResponseBody>

    suspend fun getModels(make:String): Response<List<Carmodel>>

    suspend fun reportPost(postId:Int): Response<ResponseBody>
}