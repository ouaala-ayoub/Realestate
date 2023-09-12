package com.example.realestate.data.repositories

import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.data.models.PostWithoutId
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Status
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

    fun getUserPosts(userId: String) = retrofit.getUserPosts(userId)

    fun deletePost(postId: String) = retrofit.deletePost(postId)

    fun setStatus(postId: String, status: String) =
        retrofit.setStatus(postId, Status(status))

    fun updatePost(postId: String, newPost: PostWithOwnerId) = retrofit.updatePost(postId, newPost)

    fun getPostsCount() = retrofit.getPostsCount()

}