package com.example.realestate.data.remote.network

import com.example.realestate.data.models.IdResponse
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //posts
    @GET("posts")
    fun getPosts(
        @Query("s") title: String? = null,
        @Query("p") price: Number? = null,
        @Query("c") category: String? = null,
        @Query("t") type: String? = null,
        @Query("n") filterByDate: String? = null,
        @Query("pg") page: String? = null
    ): Call<MutableList<Post>>

    @POST("posts")
    fun addPost(post: Post): Call<IdResponse>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") postId: String): Call<IdResponse>

    @PUT("posts/{id}")
    fun updatePost(@Path("id") postId: String, @Body newAnnonce: RequestBody): Call<IdResponse>

    //users
    @GET("users/{id}")
    fun getUserById(@Path("id") userId: String): Call<User>

    @POST("users")
    fun addUser(user: User): Call<IdResponse>

    @PUT("posts/{id}")
    fun updateUser(@Path("id") userId: String, @Body newUser: User): Call<IdResponse>
}