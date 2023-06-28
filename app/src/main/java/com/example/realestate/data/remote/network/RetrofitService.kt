package com.example.realestate.data.remote.network

import com.example.realestate.data.models.*
import okhttp3.RequestBody
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

    @GET("posts/categories")
    fun getCategories(): Call<List<String>>

    //posts
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
}