package com.example.googlelogindemo.network

import com.example.googlelogindemo.network.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @POST("/auth/token/login/")
    suspend fun login(@Body credentials: RequestBody): Response<Login>
    @POST("/createuser/")
    suspend fun signup(@Body signupForm: RequestBody): Response<Login>
    @POST("/googleauth/")
    suspend fun googleLogin(@Body google_token: RequestBody): Response<Login>
    @POST("/auth/token/logout/")
    suspend fun logout(@Header("Authorization") token: String): Response<ResponseBody>

    @POST("/auth/users/set_email/")
    suspend fun changeEmail(@Header("Authorization") token: String,
                            @Body body: RequestBody): Response<ResponseBody>

    @POST("/auth/users/set_password/")
    suspend fun changePassword(@Header("Authorization") token: String,
                               @Body body: RequestBody): Response<ResponseBody>

    @PATCH("/auth/users/me/")
    suspend fun changeInfos(@Header("Authorization") token: String,
                               @Body body: RequestBody): Response<ResponseBody>
    @GET("/auth/users/me/")
    suspend fun getProfile(@Header("Authorization") token: String): Response<User>

    @GET("/market/posts/")
    suspend fun getPosts(@Query("page") page:Int? = null,
                         @Query("ordering") ordering:String? = null, //price or -price
                         @Query("search") search:String? = null,
                         //Filters
                         @Query("carmodel__make") make:String? = null,
                         @Query("carmodel__model") model:String? = null,
                         @Query("color") color:String? = null,
                         @Query("fuel_type") fuel_type:String? = null,
                         @Query("body_type") body_type:String? = null,
                         @Query("location") location:String? = null,
                         @Query("mileage__gt") mileage_gt:Int? = null,
                         @Query("mileage__lt") mileage_lt:Int? = null,
                         @Query("price__gt") price_gt:Int? = null,
                         @Query("price__lt") price_lt:Int? = null,
                         @Query("displacement__gt") displacement_gt:Int? = null,
                         @Query("displacement__lt") displacement_lt:Int? = null,
                         @Query("transmission") transmission:String? = null,
                         @Query("type") type:Int? = null,
    ): Response<List<Post>>

    @GET("/market/myposts/")
    suspend fun getMyPosts(@Header("Authorization") token: String): Response<List<Post>>

    @Multipart
    @POST("/market/myposts/")
    suspend fun addPost(@Header("Authorization") token: String,
                        @PartMap() postData: Map<String, @JvmSuppressWildcards RequestBody>,
                        @Part postFiles: List<MultipartBody.Part>):Response<ResponseBody>

    @DELETE("/market/myposts/{id}/")
    suspend fun deletePost(@Header("Authorization") token: String, @Path("id") postId:Int): Response<ResponseBody>

    @GET("/market/myfavorites/")
    suspend fun getFravorites(@Header("Authorization") token: String): Response<List<Favorite>>

    @POST("/market/myfavorites/")
    suspend fun addToFavorite(@Header("Authorization") token: String, @Body postId: RequestBody): Response<ResponseBody>

    @DELETE("/market/myfavorites/{id}/")
    suspend fun deleteFavorite(@Header("Authorization") token: String, @Path("id") postId:Int): Response<ResponseBody>

    @GET("/market/carmodels/")
    suspend fun getModels(@Query("make") make:String? = null): Response<List<Carmodel>>

    @POST("/reportpost/")
    suspend fun reportPost(@Body postId: RequestBody): Response<ResponseBody>
}