package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.Post
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.utils.handleApiRequest

class AddPostModel(private val repository: PostsRepository) : ViewModel() {

    companion object {
        const val TAG = "AddPostModel"
    }

    private val _requestResponse = MutableLiveData<MessageResponse?>()
    private val _loading = MutableLiveData<Boolean>()

    val requestResponse: LiveData<MessageResponse?>
        get() = _requestResponse
    val loading: LiveData<Boolean>
        get() = _loading

    fun addPost(post: Post) {
        handleApiRequest(repository.addPost(post), _loading, _requestResponse, TAG)
    }
}