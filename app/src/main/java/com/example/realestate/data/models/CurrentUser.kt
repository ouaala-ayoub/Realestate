package com.example.realestate.data.models

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.realestate.R

class CurrentUser {
    companion object {
        private var liveData: MutableLiveData<User?> = MutableLiveData<User?>()
        private const val name = "user"
        private const val keyRes = R.string.cookie_token
        val prefs = PrefsCRUD(name, keyRes)

        fun isConnected() = liveData.value != null
        fun get() = liveData.value
        fun set(user: User?) {
            liveData.postValue(user)
        }

        fun logout() {
            liveData.postValue(null)
            prefs.delete()
        }

        fun observe(lifecycleOwner: LifecycleOwner, onUserChanged: OnUserChanged) {
            liveData.observe(lifecycleOwner) { user ->
                onUserChanged.onChange(user)
            }
        }

        interface OnUserChanged {
            fun onChange(user: User?)
        }
    }
}