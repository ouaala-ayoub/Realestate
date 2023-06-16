package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.User
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.handleApiRequest

class PostPageModel(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PostPageModel"
    }

    private val _post = MutableLiveData<Post?>()
    private val _seller = MutableLiveData<User?>()
    private val _postLoading = MutableLiveData<Boolean>()

    val post: LiveData<Post?>
        get() = _post
    val seller: LiveData<User?>
        get() = _seller
    val postLoading: LiveData<Boolean>
        get() = _postLoading

    fun getPost(postId: String) {
        handleApiRequest(postsRepository.getPostById(postId), _postLoading, _post, TAG)
    }

    fun getUserById(userId: String) {
        handleApiRequest(usersRepository.getUserById(userId), _postLoading, _seller, TAG)
    }

}