package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.utils.handleApiRequest

class HomeViewModel(private val postsRepository: PostsRepository) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _postsList = MutableLiveData<MutableList<Post>?>()
    private val _isLoading = MutableLiveData<Boolean>()

    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading
    val postsList: LiveData<MutableList<Post>?>
        get() = _postsList

    fun getPosts(searchParams: SearchParams) {
        handleApiRequest(
            postsRepository.getPosts(searchParams),
            _isLoading,
            _postsList,
            TAG
        )
    }

}