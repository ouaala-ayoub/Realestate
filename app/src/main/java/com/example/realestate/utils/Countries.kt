package com.example.realestate.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.models.OnChanged
import com.example.realestate.data.remote.network.Retrofit

class Countries {
    companion object {
        private const val TAG = "Countries"
        private var liveData: MutableLiveData<CountriesData?> = MutableLiveData<CountriesData?>()

        private fun requestCategories() {
            handleApiRequest(
                Retrofit.getInstance().getAllCountries(),
                loadingLiveData = null,
                liveData,
                TAG
            )
        }

        fun isAvailable() = liveData.value != null
        fun get() = liveData.value
        fun set(data: CountriesData?) {
            liveData.postValue(data)
        }

        fun observe(lifecycleOwner: LifecycleOwner, onChanged: OnChanged<CountriesData>) {
            if (!isAvailable()) {
                requestCategories()
            }
            liveData.observe(lifecycleOwner) { countries ->
                onChanged.onChange(countries)
            }
        }
    }
}