package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.PostWithWholeOwner
import com.example.realestate.data.models.User
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class PostPageModel(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "PostPageModel"
    }

    private val _post = MutableLiveData<PostWithWholeOwner?>()
    private val _likes = MutableLiveData<Int>()
    private val _postLoading = MutableLiveData<Boolean>()
    private val _liked = MutableLiveData<MessageResponse?>()
    private val _unliked = MutableLiveData<MessageResponse?>()

    val post: LiveData<PostWithWholeOwner?>
        get() = _post
    val likes: LiveData<Int> get() = _likes
    val postLoading: LiveData<Boolean>
        get() = _postLoading
    val liked: LiveData<MessageResponse?>
        get() = _liked
    val unliked: LiveData<MessageResponse?>
        get() = _unliked

    fun getPost(postId: String) {
        handleApiRequest(postsRepository.getPostById(postId), _postLoading, _post, TAG)
    }

    fun like(postId: String) {
        handleApiRequest(
            usersRepository.like(postId),
            null,
            _liked,
            TAG,
            function = "like()", additionalCode = object : AdditionalCode<MessageResponse> {
                override fun onResponse(responseBody: Response<MessageResponse>) {
                    if (responseBody.isSuccessful) {
                        var n = _likes.value
                        n?.apply { n = this + 1 }
                        _likes.postValue(n!!)
                    }
                }

                override fun onFailure() {}

            }
        )
    }

    fun unlike(postId: String) {
        handleApiRequest(
            usersRepository.unlike(postId),
            null,
            _unliked,
            TAG,
            function = "unlike()", additionalCode = object : AdditionalCode<MessageResponse> {
                override fun onResponse(responseBody: Response<MessageResponse>) {
                    if (responseBody.isSuccessful) {
                        var n = _likes.value
                        n?.apply { n = this - 1 }
                        _likes.postValue(n!!)
                    }
                }

                override fun onFailure() {}

            }
        )
    }

    fun setLikes(likes: Int) {
        _likes.postValue(likes)
    }

}