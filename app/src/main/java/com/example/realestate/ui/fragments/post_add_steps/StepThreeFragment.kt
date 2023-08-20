package com.example.realestate.ui.fragments.post_add_steps

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentStepThreeBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.postaddmodels.StepThreeModel
import com.example.realestate.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class StepThreeFragment : FragmentStep() {

    companion object {
        private const val TAG = "StepThreeFragment"
    }

    private lateinit var binding: FragmentStepThreeBinding
    private var countriesData: CountriesData? = null
//    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val stepThreeModel: StepThreeModel by lazy {
        val retrofit = Retrofit.getInstance()
        StepThreeModel(PostsRepository(retrofit), StaticDataRepository(retrofit)).also {
            it.apply {
                getCountries()
                getAllCities()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        locationPermissionRequest = requestMultiplePermissions(object : LocationPermission {
//            @SuppressLint("MissingPermission")
//            override fun onGrantedPrecise() {
//                handleLocation()
//            }
//
//            override fun onGrantedApproximate() {
//                locationPermissionRequest.requestLocationPermission()
//            }
//
//            override fun onNonGranted() {
//                val snackBar = makeSnackBar(
//                    requireView(),
//                    getString(R.string.permission),
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                snackBar.setAction(R.string.OK) {
//                    snackBar.dismiss()
//                }.show()
//            }
//
//        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStepThreeBinding.inflate(inflater, container, false)



//        binding.getLocation.setOnClickListener {
//            requireActivity().handlePermission(
//                object : PermissionResult {
//                    @SuppressLint("MissingPermission")
//                    override fun onGranted() {
//                        handleLocation()
//                    }
//
//                    override fun onNonGranted() {
//                        locationPermissionRequest.requestLocationPermission()
//                    }
//
//                },
//                listOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            )
//        }


        setEditTexts()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val lastState = stepThreeModel.isDataValid.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }

    override fun onNextClicked(viewPager: ViewPager2) {
        super.onNextClicked(viewPager)
        // send the request

        val dialog = makeDialog(
            requireContext(),
            object : OnDialogClicked {
                override fun onPositiveButtonClicked() {
                    (requireActivity() as AddPostActivity).post.apply {
                        if (CurrentUser.isConnected()) {
                            stepThreeModel.apply {
                                val l = Location(
                                    country = countryLiveData.value.toString(),
                                    city = cityLiveData.value.toString()
                                )

                                if (streetLiveData.value != null)
                                    l.area = streetLiveData.value.toString()

                                location = l
                                description = descriptionLiveData.value.toString()
                            }
                            Log.i(TAG, "post: $this")
                            stepThreeModel.addPost(this)
                        } else {
                            doOnFail()
                        }
                    }
                }

                override fun onNegativeButtonClicked() {}

            },
            getString(R.string.finish_dialog_title),
            getString(R.string.finish_dialog_message)
        )

        dialog.apply {
            show()
            separateButtonsBy(10)
        }

    }

    private fun doOnFail() {
        requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }

    private fun setEditTexts() {
        binding.apply {
            stepThreeModel.apply {

//                _countryLiveData.value = countryPicker.selectedCountryName

//                countryPicker.setOnCountryChangeListener {
//                    // your code to handle selected country
//                    val country = countryPicker.selectedCountryName
//                    Log.d(TAG, "country: $country")
//                    _countryLiveData.postValue(country)
//                    getCities(country)
//                }

                //handle edit texts use input
                countryEditText.updateLiveData(_countryLiveData)
                cityEditText.updateLiveData(_cityLiveData)
                streetEditText.updateLiveData(_streetLiveData)
                descriptionEditText.updateLiveData(_descriptionLiveData)

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepThreeModel.apply {
            handleLocationEditText()

            isDataValid.observe(viewLifecycleOwner) { isValid ->
                Log.d(TAG, "isValidData : $isValid")
                (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValid)
            }

            loading.observe(viewLifecycleOwner) { loading ->
                binding.progressBar.isVisible = loading
                for (v in binding.wholeLayout.children) {
                    v.isEnabled = false
                }
            }

            requestResponse.observe(viewLifecycleOwner) { requestResponse ->
                Log.d(TAG, "onCreateView: ${requestResponse?.message}")
                if (requestResponse != null) {
                    requireContext().toast(requestResponse.message, Toast.LENGTH_SHORT)
                } else {
                    doOnFail()
                }
                requireActivity().finish()
            }
        }
    }

//    @SuppressLint("MissingPermission")
//    fun handleLocation() {
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                // Got last known location. In some rare situations this can be null.
//
//                if (location == null) {
//                    requireContext().toast(
//                        getString(R.string.no_gps),
//                        Toast.LENGTH_SHORT
//                    )
//                } else {
//                    //get the location and
//                    location.apply {
//
//
//                        val geoLocationUtils = GeoLocationUtils(requireContext())
//                        val address = geoLocationUtils.getAddress(
//                            this
//                        )
//
//                        binding.apply {
//                            address?.apply {
//                                //country set
//                                val myCountry = countriesData?.find { country ->
//                                    country.code == address.countryCode
//                                }
//                                if (myCountry != null) {
//                                    countryEditText.setText(myCountry.name)
//                                } else {
//                                    countryEditText.setText(address.countryName)
//                                }
//
//                                cityEditText.setText(locality)
//                                streetEditText.setText(thoroughfare)
//                            }
//                        }
//
//                    }
//                }
//
//            }
//    }

    private fun handleLocationEditText() {
        stepThreeModel.apply {
            binding.apply {

                countries.observe(viewLifecycleOwner) { countries ->
                    Log.d(TAG, "cities: $countries")

                    countriesData = countries

                    countries?.apply {
                        countryEditText.apply {
                            val names = map { data -> data.name ?: "____" }
                            val adapter = setUpAndHandleSearch(names)
                            setOnItemClickListener { _, view, _, _ ->
                                val text = (view as TextView).text
                                Log.i(TAG, "onItemSelected: $text")
                                adapter.filter.filter(null)
                                getCities(text.toString())
                                cityEditText.text.clear()
                                streetEditText.text.clear()
//                                cityEditText.isEnabled = false
                            }
                        }
                    }

                }

                cities.observe(viewLifecycleOwner) { cities ->
                    Log.d(TAG, "cities: $cities")
                    cityEditText.apply {
//                        cityTextField.isEnabled = !cities.isNullOrEmpty()
                        val adapter = setUpAndHandleSearch(cities)

                        setOnItemClickListener { _, view, _, _ ->
                            val selectedCity = (view as TextView).text
                            Log.i(TAG, "onItemSelected: $selectedCity")
                            adapter.filter.filter(null)
//                            getStreets(selectedCity.toString())
                            streetEditText.apply {
                                this.text.clear()
//                                isEnabled = false
                            }
                        }
                    }
                }
                streets.observe(viewLifecycleOwner) { streets ->
                    streetEditText.apply {
//                        streetEditText.isEnabled = !streets.isNullOrEmpty()
                        val adapter = setUpAndHandleSearch(streets)
                        setOnItemClickListener { _, _, _, _ ->
                            adapter.filter.filter(null)
                        }
                    }
                }

            }
        }
    }
}