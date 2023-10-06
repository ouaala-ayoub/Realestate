package com.example.realestate.data.models

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.realestate.R
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.utils.TAG
import com.example.realestate.utils.handleApiRequest

class CurrentUser {
    companion object {
        private var liveData: MutableLiveData<User?> = MutableLiveData<User?>()
        private const val name = "user"
        private const val keyRes = R.string.cookie_token
        val prefs = PrefsCRUD(name, keyRes)

        fun getAuth(){
            handleApiRequest(Retrofit.getInstance().getAuth(), null, liveData, TAG)
        }
        fun isConnected() = liveData.value != null
        fun get() = liveData.value
        fun set(user: User?) {
            liveData.postValue(user)
        }

        fun logout() {
            liveData.postValue(null)
            prefs.delete()
        }

        fun observe(lifecycleOwner: LifecycleOwner, onChanged: OnChanged<User>) {
            liveData.observe(lifecycleOwner) { user ->
                onChanged.onChange(user)
            }
        }


    }
}
interface OnChanged<T> {
    fun onChange(data: T?)
}