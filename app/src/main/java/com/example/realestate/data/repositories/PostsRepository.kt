package com.example.realestate.data.repositories

import com.example.realestate.data.models.PostWithoutId
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.remote.network.RetrofitService

class PostsRepository(private val retrofit: RetrofitService) {
    fun getPosts(searchParams: SearchParams) = retrofit.getPosts(
        title = searchParams.title,
        country = searchParams.location?.country?.name,
        city = searchParams.location?.city,
        category = searchParams.category,
        type = searchParams.type,
        page = searchParams.page,
        features = searchParams.features,
        condition = searchParams.condition
    )

    fun addPost(post: PostWithoutId) = retrofit.addPost(post)

    fun getPostById(postId: String) = retrofit.getPostById(postId)

    fun getPostsCount() = retrofit.getPostsCount()

}