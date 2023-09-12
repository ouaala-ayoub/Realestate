package com.example.realestate.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.models.OnChanged
import com.example.realestate.data.remote.network.Retrofit

class Cities {
    companion object {
        private const val TAG = "Countries"
        private val liveData = MutableLiveData<Map<String, List<String>>?>()

        private fun requestCities() {
            handleApiRequest(
                Retrofit.getInstance().getAllCities(),
                loadingLiveData = null,
                liveData,
                TAG
            )
        }

        fun isAvailable() = liveData.value != null
        fun get() = liveData.value
        fun set(data: Map<String, List<String>>?) {
            liveData.postValue(data)
        }

        fun observeAllCities(
            lifecycleOwner: LifecycleOwner,
            onChanged: OnChanged<Map<String, List<String>>?>
        ) {
            if (!isAvailable()) {
                requestCities()
            }
            liveData.observe(lifecycleOwner) { countries ->
                onChanged.onChange(countries)
            }
        }
    }
}