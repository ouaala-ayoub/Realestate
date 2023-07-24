package com.example.realestate.ui.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.ui.fragments.user_register_steps.EmailVerificationFragment
import com.example.realestate.ui.viewmodels.userregistermodels.LoginModel
import com.example.realestate.utils.Task
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class EmailVerificationModel(private val usersRepository: UsersRepository) :
    LoginModel(usersRepository) {

    companion object {
        private const val TAG = "EmailVerificationModel"
    }

    fun launchGmailAuth(signInLauncher: ActivityResultLauncher<Intent>) {

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult, task: Task) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {

            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser

            task.onSuccess(user)
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            task.onFail(response?.error)
        }
    }
}