package com.example.realestate.ui.viewmodels.userregistermodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.User
import com.example.realestate.data.models.UserId
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class VerificationCodeModel(private val repository: UsersRepository) : LoginModel(repository) {

    companion object {
        private const val TAG = "VerificationCodeModel"
    }

    private val _verificationCode = MutableLiveData<String>()
    private val _loading = super._isLoading

    val isValid = MediatorLiveData<Boolean>().apply {
        addSource(_verificationCode) { code ->
            this.value = code.length == 6
        }
    }

    fun setVerificationCode(code: String) {
        _verificationCode.postValue(code)
    }


}