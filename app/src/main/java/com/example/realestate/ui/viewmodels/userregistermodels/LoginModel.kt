package com.example.realestate.ui.viewmodels.userregistermodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.UserId
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.Task
import com.example.realestate.utils.handleApiRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import retrofit2.Response

open class LoginModel(private val repository: UsersRepository) : ViewModel() {
    companion object {
        const val TAG = "LoginModel"
    }

    val _isLoading = MutableLiveData<Boolean>()
    val _userId = MutableLiveData<UserId?>()

    val userId: LiveData<UserId?>
        get() = _userId
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    fun addPhoneToUser(){

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

//        _isLoading.postValue(true)
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//            .addOnCompleteListener(activity) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//
//                    val user = task.result?.user
//                    myTask.onSuccess(user)
//
//                    Log.d(TAG, "signInWithCredential:${user?.phoneNumber}")
//                } else {
//                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    val e = task.exception
//                    myTask.onFail(e)
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        // The verification code entered was invalid
//                    }
//                    // Update UI
//                }
//                _isLoading.postValue(false)
//            }
    }

    fun login(token: String) {
        handleApiRequest(
            repository.login(token),
            _isLoading,
            _userId,
            TAG,
            object : AdditionalCode<UserId> {
                override fun onResponse(responseBody: Response<UserId>) {
                    val userId = (responseBody.body())?.id
                    Log.d(TAG, "userId: $userId")
                    //store user id in the prefs
                    if (userId != null)
                        CurrentUser.prefs.set(userId)
                }

                override fun onFailure() {}
            })
    }
}