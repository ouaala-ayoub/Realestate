package com.example.realestate.data.repositories

import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.remote.network.RetrofitService

class PostsRepository(private val retrofit: RetrofitService) {
    fun getPosts(searchParams: SearchParams) = retrofit.getPosts(
        title = searchParams.title,
        price = searchParams.price,
        category = searchParams.category,
        type = searchParams.type,
        page = searchParams.page.toString()
    )
}