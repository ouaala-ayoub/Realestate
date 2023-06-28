package com.example.realestate.ui.viewmodels.userregistermodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.UserId
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class VerificationCodeModel(private val repository: UsersRepository) : LoginModel() {

    companion object {
        private const val TAG = "VerificationCodeModel"
    }

    private val _verificationCode = MutableLiveData<String>()
    private val _userId = MutableLiveData<UserId?>()
    private val _loading = super._isLoading

    val userId: LiveData<UserId?>
        get() = _userId

    val isValid = MediatorLiveData<Boolean>().apply {
        addSource(_verificationCode) { code ->
            this.value = code.length == 6
        }
    }

    fun setVerificationCode(code: String) {
        _verificationCode.postValue(code)
    }

    fun login(token: String) {
        handleApiRequest(repository.login(token), _loading, _userId, TAG, object : AdditionalCode {
            override fun <T> onResponse(responseBody: Response<T>) {
                val userId = (responseBody.body() as UserId?)   ?.id
                Log.d(TAG, "userId: $userId")
                //store user id in the prefs
                if (userId != null)
                    CurrentUser.prefs.set(userId)
            }

            override fun onFailure() {}
        })
    }
}