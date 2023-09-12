package com.example.realestate.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.PostStatus
import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.utils.handleApiRequest

class UserPostsViewModel(private val repository: PostsRepository) : ViewModel() {

    companion object {
        private const val TAG = "UserPostsViewModel"
    }

    private val _posts = MutableLiveData<List<PostWithOwnerId>?>()
    private val _isEmpty = MutableLiveData<Boolean>()
    private var _deleted = MutableLiveData<List<MutableLiveData<MessageResponse?>>>()
    private var _outOfOrderSet = MutableLiveData<List<MutableLiveData<MessageResponse?>>>()
    private val _isLoading = MutableLiveData<Boolean>()

    val posts: LiveData<List<PostWithOwnerId>?> get() = _posts
    val loading: LiveData<Boolean> get() = _isLoading
    val deleted: LiveData<List<MutableLiveData<MessageResponse?>>> get() = _deleted
    val outOfOrderSet: LiveData<List<MutableLiveData<MessageResponse?>>> get() = _outOfOrderSet
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    fun setIsEmpty(value: Boolean) {
        _isEmpty.postValue(value)
    }

    fun setOutOfOrderSet(size: Int) {
        _outOfOrderSet.postValue(List(size) {
            MutableLiveData(null)
        })
    }

    fun setDeletedList(size: Int) {
        _deleted.postValue(List(size) {
            MutableLiveData(null)
        })
    }

    fun getUserPosts(userId: String) {
        handleApiRequest(repository.getUserPosts(userId), _isLoading, _posts, TAG)
    }

    fun deletePost(postId: String, position: Int) {
        handleApiRequest(
            repository.setStatus(postId, PostStatus.DELETED.value),
            _isLoading,
            _deleted.value?.get(position),
            TAG
        )
    }

    fun setOutOfOrder(postId: String, position: Int, outOfOrder: Boolean) {
        val status = if (outOfOrder) PostStatus.APPROVED.value else PostStatus.OUT_OF_ORDER.value
        handleApiRequest(
            repository.setStatus(postId, status),
            _isLoading,
            _outOfOrderSet.value?.get(position),
            TAG
        )
    }
}