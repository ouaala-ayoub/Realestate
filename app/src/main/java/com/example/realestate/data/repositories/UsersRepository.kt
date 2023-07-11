package com.example.realestate.data.repositories

import com.example.realestate.data.models.AdditionalInfo
import com.example.realestate.data.remote.network.NewFavouritesRequest
import com.example.realestate.data.remote.network.RetrofitService
import okhttp3.ResponseBody
import retrofit2.Call

class UsersRepository(private val retrofitService: RetrofitService) {
    fun getUserById(userId: String) = retrofitService.getUserById(userId)

    fun addData(userId: String, data: AdditionalInfo) = retrofitService.addData(userId, data)

    fun login(token: String) = retrofitService.login("Bearer $token")

    fun getSavedPosts(userId: String) = retrofitService.getSavedPosts(userId)

    fun addToFavourites(userId: String, favouriteId: String) =
        retrofitService.addFavourite(userId, NewFavouritesRequest(favouriteId))

    fun deleteFavourite(userId: String, favouriteId: String) =
        retrofitService.deleteFavourite(userId, NewFavouritesRequest(favouriteId))

}