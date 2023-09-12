package com.example.realestate.data.remote.network

import com.example.realestate.data.models.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //locations
    @GET("countries")
    fun getAllCountries(): Call<CountriesData>

    @GET("cities")
    fun getAllCities(): Call<Map<String, List<String>>>

//    @GET("streets")
//    fun getStreets(): Call<List<String>>

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
        @Query("country") country: String? = null,
        @Query("city") city: String? = null,
        @Query("category") category: String? = null,
        @Query("type") type: String? = null,
        @Query("n") filterByDate: String? = null,
        @Query("condition") condition: String? = null,
        @Query("page") page: String? = null,
        @Query("features") features: List<String>? = null
    ): Call<MutableList<PostWithOwnerId>>

    @GET("users/{id}/posts")
    fun getUserPosts(@Path("id") userId: String): Call<List<PostWithOwnerId>>

    @POST("posts")
    fun addPost(@Body post: PostWithoutId): Call<MessageResponse>

    @GET("posts/count")
    fun getPostsCount(): Call<Int>

    @GET("posts/{id}")
    fun getPostById(@Path("id") postId: String): Call<PostWithWholeOwner>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") postId: String): Call<IdResponse>

    @PUT("posts/{id}")
    fun updatePost(
        @Path("id") postId: String,
        @Body newAnnonce: PostWithOwnerId
    ): Call<MessageResponse>

    @PATCH("posts/{id}/like")
    fun like(
        @Path("id") postId: String
    ): Call<MessageResponse>

    @PATCH("posts/{id}/unlike")
    fun unlike(
        @Path("id") postId: String
    ): Call<MessageResponse>

    @PUT("posts/{id}")
    fun setStatus(
        @Path("id") postId: String,
        @Body status: Status
    ): Call<MessageResponse>

    //users
    @GET("users/{id}")
    fun getUserById(@Path("id") userId: String): Call<User>

    @GET("auth")
    fun getAuth(): Call<User>

    @POST("users")
    fun addUser(@Body user: User): Call<IdResponse>

    @PUT("users/{id}")
    fun addData(@Path("id") userId: String, @Body dataToAdd: AdditionalInfo): Call<MessageResponse>

    @PUT("users/{id}")
    fun addPhoneNumber(
        @Path("id") userId: String,
        @Body dataToAdd: PhoneNumber
    ): Call<MessageResponse>

    @POST("login")
    fun login(@Header("Authorization") token: String): Call<User>

    @GET("users/{id}/likes")
    fun getLikedPosts(@Path("id") userId: String): Call<List<PostWithOwnerId>>


}