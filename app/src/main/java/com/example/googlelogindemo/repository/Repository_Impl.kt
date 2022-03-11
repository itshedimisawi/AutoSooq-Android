package com.example.googlelogindemo.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.googlelogindemo.network.APIService
import com.example.googlelogindemo.network.models.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.net.URI

class Repository_Impl(
    private val apiService: APIService,
    private val token_inject: String?
) : Repository {

    override var token = token_inject

    override suspend fun login(email: String, password: String): Response<Login> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", email)
            .addFormDataPart("password", password)
            .build()
        return apiService.login(credentials = requestBody)
    }

    override suspend fun signup(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Response<Login> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", email)
            .addFormDataPart("password", password)
            .addFormDataPart("first_name", firstName)
            .addFormDataPart("last_name", lastName)
            .build()
        return apiService.signup(signupForm = requestBody)
    }

    override suspend fun google_login(google_token: String): Response<Login> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("google_acc", google_token)
            .build()
        return apiService.googleLogin(google_token = requestBody)
    }

    override suspend fun logout(): Response<ResponseBody> {
        return apiService.logout(token = "Token ${token.toString()}")
    }

    override suspend fun changeEmail(password: String, newEmail: String): Response<ResponseBody> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("current_password", password)
            .addFormDataPart("new_email", newEmail)
            .build()
        return apiService.changeEmail(
            token = "Token ${token.toString()}",
            body = requestBody
        )
    }

    override suspend fun changePassword(
        password: String,
        newPassword: String
    ): Response<ResponseBody> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("current_password", password)
            .addFormDataPart("new_password", newPassword)
            .build()
        return apiService.changePassword(
            token = "Token ${token.toString()}",
            body = requestBody
        )
    }

    override suspend fun changeInfos(
        firstName: String?,
        lastName: String?
    ): Response<ResponseBody> {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .apply {
                firstName?.let {
                    addFormDataPart("first_name", it)
                }
                lastName?.let {
                    addFormDataPart("last_name", it)
                }
//                phone?.let {
//                    addFormDataPart("phone", it)
//                }
            }
            .build()
        return apiService.changeInfos(
            token = "Token ${token.toString()}",
            body = requestBody
        )
    }

    override suspend fun getProfile(): Response<User> {
        return apiService.getProfile(token = "Token ${token.toString()}")
    }

    override suspend fun getPosts(
        page: Int?,
        ordering: String?, //price or -price
        search: String?,
        //Filters
        make: String?,
        model: String?,
        color: String?,
        fuel_type: String?,
        body_type: String?,
        location: String?,
        mileage_gt: Int?,
        mileage_lt: Int?,
        price_gt: Int?,
        price_lt: Int?,
        displacement_gt: Int?,
        displacement_lt: Int?,
        transmission: String?,
        type: Int?,
    ): Response<List<Post>> {
        return apiService.getPosts(
            page = page,
            ordering = ordering,  //price or -price
            search = search,
            //Filters
            make = make,
            model = model,
            color = color,
            fuel_type = fuel_type,
            body_type = body_type,
            location = location,
            mileage_gt = mileage_gt,
            mileage_lt = mileage_lt,
            price_gt = price_gt,
            price_lt = price_lt,
            displacement_gt = displacement_gt,
            displacement_lt = displacement_lt,
            transmission = transmission,
            type = type,
        )
    }

    override suspend fun getMyPosts(): Response<List<Post>> {
        return apiService.getMyPosts(token = "Token ${token.toString()}")
    }

    override suspend fun addPost(
        title: String?,
        description: String?,
        model: Int?,
        color: String?,
        fuel_type: String?,
        body_type: String?,
        location: String?,
        mileage: Int?,
        price: Int?,
        displacement: Int?,
        transmission: String?,
        phone: String?,
        type: Int?,
        images: List<File>
    ): Response<ResponseBody> {
//        val requestBody: RequestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .apply {
//                title?.let { addFormDataPart("title", it) }
//                description?.let { addFormDataPart("description", it) }
//                model?.let { addFormDataPart("carmodel_id", it.toString()) }
//                fuel_type?.let { addFormDataPart("fuel_type", it) }
//                body_type?.let { addFormDataPart("body_type", it) }
//                location?.let { addFormDataPart("location", it) }
//                price?.let { addFormDataPart("price", it.toString()) }
//                color?.let { addFormDataPart("color", it) }
//                mileage?.let { addFormDataPart("mileage", it.toString()) }
//                displacement?.let { addFormDataPart("displacement", it.toString()) }
//                transmission?.let { addFormDataPart("transmission", it) }
//                phone?.let { addFormDataPart("owner_phone", it) }
//                type?.let { addFormDataPart("type", it.toString()) }
//
//            }
//            .build()
//
        val postData : HashMap<String, RequestBody> = HashMap()
        title?.let { postData.put("title", RequestBody.create(MediaType.parse("text/plain"), it)) }
        description?.let { postData.put("description", RequestBody.create(MediaType.parse("text/plain"), it)) }
        model?.let { postData.put("carmodel_id", RequestBody.create(MediaType.parse("text/plain"), it.toString())) }
        fuel_type?.let { postData.put("fuel_type", RequestBody.create(MediaType.parse("text/plain"), it)) }
        body_type?.let { postData.put("body_type", RequestBody.create(MediaType.parse("text/plain"), it)) }
        location?.let { postData.put("location", RequestBody.create(MediaType.parse("text/plain"), it)) }
        price?.let { postData.put("price", RequestBody.create(MediaType.parse("text/plain"), it.toString())) }
        color?.let { postData.put("color", RequestBody.create(MediaType.parse("text/plain"), it)) }
        mileage?.let { postData.put("mileage", RequestBody.create(MediaType.parse("text/plain"), it.toString())) }
        displacement?.let { postData.put("displacement", RequestBody.create(MediaType.parse("text/plain"), it.toString())) }
        transmission?.let { postData.put("transmission", RequestBody.create(MediaType.parse("text/plain"), it)) }
        phone?.let { postData.put("owner_phone", RequestBody.create(MediaType.parse("text/plain"), it)) }
        type?.let { postData.put("type", RequestBody.create(MediaType.parse("text/plain"), it.toString())) }

        val postFiles : ArrayList<MultipartBody.Part> = ArrayList()
        for(i in images){
            val fileBody: RequestBody  = RequestBody.create(MediaType.parse("image/*"),i)
            postFiles.add(MultipartBody.Part.createFormData("postimage",i.name,fileBody))
        }
        return apiService.addPost(token = "Token ${token.toString()}", postData = postData, postFiles = postFiles)
    }

    override suspend fun deletePost(postId: Int): Response<ResponseBody> {
        return apiService.deletePost(token = "Token ${token.toString()}", postId = postId)
    }

    override suspend fun getFavorites(): Response<List<Favorite>> {
        return apiService.getFravorites(token = "Token ${token.toString()}")
    }

    override suspend fun addToFavorite(postId: Int): Response<ResponseBody> {
        val post_id: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("post_id", postId.toString())
            .build()
        return apiService.addToFavorite(token = "Token ${token.toString()}", postId = post_id)
    }

    override suspend fun deleteFavorite(postId: Int): Response<ResponseBody> {
        return apiService.deleteFavorite(token = "Token ${token.toString()}", postId = postId)
    }

    override suspend fun getModels(make: String): Response<List<Carmodel>> {
        return apiService.getModels(make = make)
    }

    override suspend fun reportPost(postId: Int): Response<ResponseBody> {
        val post_id: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("post_id", postId.toString())
            .build()
        return apiService.reportPost(postId = post_id)
    }
}