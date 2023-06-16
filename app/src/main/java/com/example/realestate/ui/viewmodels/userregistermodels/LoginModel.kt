package com.example.realestate.ui.viewmodels.userregistermodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.realestate.utils.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential

open class LoginModel : ViewModel() {
    companion object {
        const val TAG = "LoginModel"
    }

    fun signInWithPhoneAuthCredential(
        activity: Activity,
        credential: PhoneAuthCredential,
        myTask: Task
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    myTask.onSuccess(user)

                    Log.d(TAG, "signInWithCredential:${user?.phoneNumber}")
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val e = task.exception
                    myTask.onFail(e)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}