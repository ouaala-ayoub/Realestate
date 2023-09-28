package com.example.realestate.data.repositories

import com.example.realestate.data.models.AdditionalInfo
import com.example.realestate.data.models.PhoneNumber
import com.example.realestate.data.models.User
import com.example.realestate.data.remote.network.RetrofitService
import retrofit2.Call

class UsersRepository(private val retrofitService: RetrofitService) {

    fun getAuth() = retrofitService.getAuth()

    fun addData(userId: String, data: AdditionalInfo) = retrofitService.addData(userId, data)

    fun login(token: String) = retrofitService.login("Bearer $token")

    fun getLikedPosts(userId: String) = retrofitService.getLikedPosts(userId)

    fun like(favouriteId: String) =
        retrofitService.like(favouriteId)

    fun unlike(favouriteId: String) =
        retrofitService.unlike(favouriteId)

    fun addPhoneNumber(userId: String, phone: String) =
        retrofitService.addPhoneNumber(userId, PhoneNumber(phone))

}