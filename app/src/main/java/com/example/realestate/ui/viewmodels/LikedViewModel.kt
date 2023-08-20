package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.data.models.PostWithWholeOwner
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class LikedViewModel(private val repository: UsersRepository) : ViewModel() {
    companion object {
        private const val TAG = "LikedViewModel"
        private const val NO_FAVOURITES = "No Liked Posts"
        private const val ERROR = "Unexpected Error"
    }

    private val _savedList = MutableLiveData<List<PostWithOwnerId>?>()
    private val _loading = MutableLiveData<Boolean>()
    private val _unliked = MutableLiveData<MessageResponse?>()
    private val _postsMessage = MutableLiveData<String>()


    val savedList: LiveData<List<PostWithOwnerId>?>
        get() = _savedList
    val loading: LiveData<Boolean>
        get() = _loading
    val unliked: LiveData<MessageResponse?>
        get() = _unliked
    val postsMessage: LiveData<String>
        get() = _postsMessage

    fun getLikedPosts(userId: String) {
        handleApiRequest(
            repository.getLikedPosts(userId),
            _loading,
            _savedList,
            TAG,
            object : AdditionalCode<List<PostWithOwnerId>> {
                override fun onResponse(responseBody: Response<List<PostWithOwnerId>>) {
                    if (responseBody.isSuccessful) {
                        if (responseBody.body()!!.isEmpty()) {
                            _postsMessage.postValue(NO_FAVOURITES)
                        } else {
                            _postsMessage.postValue("")
                        }
                    } else {
                        _postsMessage.postValue(ERROR)
                    }
                }

                override fun onFailure() {
                    _postsMessage.postValue(ERROR)
                }
            }
        )
    }

    fun deleteFromFavourites(postId: String) {
        handleApiRequest(
            repository.unlike(postId),
            null,
            _unliked,
            TAG
        )
    }

}