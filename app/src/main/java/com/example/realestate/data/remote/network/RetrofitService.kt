package com.example.realestate.data.remote.network

import com.example.realestate.data.models.*
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //locations
//    @GET("countries")
//    fun getCountries(): Call<List<String>>
//
//    @GET("cities")
//    fun getCities(): Call<List<String>>
//
//    @GET("streets")
//    fun getStreets(): Call<List<String>>

    //sign the uploads
    @POST("upload/newnew")
    fun generateSignature(@Query("timestamp") timeStamp: Long): Call<SignResult>

    //reports
    @GET("reports/reasons")
    fun getReportReasons(): Call<List<String>>

    @POST("reports")
    fun addReport(@Body reportToAdd: Report): Call<MessageResponse>

    //posts

    @GET("posts/categories")
    fun getCategories(): Call<List<String>>

    @GET("posts")
    fun getPosts(
        @Query("search") title: String? = null,
        @Query("p") price: Number? = null,
        @Query("country") country: String? = null,
        @Query("category") category: String? = null,
        @Query("type") type: String? = null,
        @Query("n") filterByDate: String? = null,
        @Query("pg") page: String? = null
    ): Call<MutableList<Post>>

    @POST("posts")
    fun addPost(@Body post: PostWithoutId): Call<MessageResponse>

    @GET("posts/{id}")
    fun getPostById(@Path("id") postId: String): Call<Post>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") postId: String): Call<IdResponse>

    @PUT("posts/{id}")
    fun updatePost(@Path("id") postId: String, @Body newAnnonce: RequestBody): Call<IdResponse>

    //users
    @GET("users/{id}")
    fun getUserById(@Path("id") userId: String): Call<User>

    @POST("users")
    fun addUser(@Body user: User): Call<IdResponse>

    @PUT("users/{id}")
    fun addData(@Path("id") userId: String, @Body dataToAdd: AdditionalInfo): Call<MessageResponse>

    @POST("login")
    fun login(@Header("Authorization") token: String): Call<UserId>

    @GET("users/{id}/favourites")
    fun getSavedPosts(@Path("id") userId: String): Call<List<Post>>

    @HTTP(method = "DELETE", path = "users/{id}/favourites", hasBody = true)
    fun deleteFavourite(
        @Path("id") userId: String,
        @Body favouriteId: NewFavouritesRequest
    ): Call<BooleanHolder>

    @PATCH("users/{id}/favourites")
    fun addFavourite(
        @Path("id") userId: String,
        @Body favouriteId: NewFavouritesRequest
    ): Call<BooleanHolder>
}

data class BooleanHolder(
    @SerializedName("updated")
    val data: Boolean
)