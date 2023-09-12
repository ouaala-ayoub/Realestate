package com.example.realestate.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.realestate.data.models.OnChanged
import com.example.realestate.data.remote.network.Retrofit

class Categories {

    companion object {
        private const val TAG = "Categories"
        private var liveData: MutableLiveData<List<String>?> = MutableLiveData<List<String>?>()

        private fun requestCategories() {
            handleApiRequest(
                Retrofit.getInstance().getCategories(),
                loadingLiveData = null,
                liveData,
                TAG
            )
        }

        private fun isAvailable() = liveData.value != null
        fun get() = liveData.value
        fun set(categories: List<String>?) {
            liveData.postValue(categories)
        }

        fun observe(lifecycleOwner: LifecycleOwner, onChanged: OnChanged<List<String>?>) {
            if (!isAvailable()) {
                requestCategories()
            }
            liveData.observe(lifecycleOwner) { categories ->
                onChanged.onChange(categories)
            }
        }
    }
}