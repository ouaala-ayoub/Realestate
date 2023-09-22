package com.example.realestate.ui.viewmodels.userregistermodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.User
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.Task
import com.example.realestate.utils.handleApiRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import okhttp3.ResponseBody
import retrofit2.Response

open class LoginModel(private val repository: UsersRepository) : ViewModel() {
    companion object {
        const val TAG = "LoginModel"
    }

    val _isLoading = MutableLiveData<Boolean>()
    private val _user = MutableLiveData<User?>()

    val user: LiveData<User?>
        get() = _user
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getAuth() {
        handleApiRequest(repository.getAuth(), _isLoading, _user, TAG)
    }

    fun signInWithPhoneAuthCredential(
        activity: Activity,
        credential: PhoneAuthCredential,
        myTask: Task
    ) {
        _isLoading.postValue(true)
        FirebaseAuth.getInstance().currentUser!!.linkWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "linkWithCredential:success")
                    val user = task.result?.user

                    myTask.onSuccess(user)
                } else {
                    Log.w(TAG, "linkWithCredential:failure", task.exception)
                    val e = task.exception
                    myTask.onFail(e)
                }
                _isLoading.postValue(false)
            }
    }

    fun login(token: String) {
        handleApiRequest(
            repository.login(token),
            _isLoading,
            null,
            TAG,
            object : AdditionalCode<ResponseBody> {
                override fun onResponse(responseBody: Response<ResponseBody>) {

                    if (responseBody.isSuccessful) {
                        //get the auth on login success
                        getAuth()
                    } else {
                        _user.postValue(null)
                    }

                }

                override fun onFailure() {
                    _user.postValue(null)
                }

            }
        )
    }
}