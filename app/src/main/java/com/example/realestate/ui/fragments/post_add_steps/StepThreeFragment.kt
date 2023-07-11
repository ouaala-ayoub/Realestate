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
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
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
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val stepThreeModel: StepThreeModel by lazy {
        StepThreeModel(PostsRepository(Retrofit.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationPermissionRequest = requestMultiplePermissions(object : LocationPermission {
            @SuppressLint("MissingPermission")
            override fun onGrantedPrecise() {
                //
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        Log.d(TAG, "location: $location")
                        Log.d(TAG, "location longitude: ${location?.longitude}")
                        Log.d(TAG, "location altitude: ${location?.altitude}")
                    }
            }

            override fun onGrantedApproximate() {
                locationPermissionRequest.requestLocationPermission()
            }

            override fun onNonGranted() {
                val snackBar = makeSnackBar(
                    requireView(),
                    getString(R.string.permission),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction(R.string.OK) {
                    snackBar.dismiss()
                }.show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStepThreeBinding.inflate(inflater, container, false)

        stepThreeModel.apply {

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

        binding.getLocation.setOnClickListener {
            requireActivity().handlePermission(
                object : PermissionResult {
                    @SuppressLint("MissingPermission")
                    override fun onGranted() {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                // Got last known location. In some rare situations this can be null.
                                Log.d(TAG, "location longitude: ${location?.longitude}")
                                Log.d(TAG, "location altitude: ${location?.altitude}")

                                if (location == null) {
                                    requireContext().toast(
                                        getString(R.string.no_gps),
                                        Toast.LENGTH_SHORT
                                    )
                                } else {
                                    //get the location and
                                }
                            }
                    }

                    override fun onNonGranted() {
                        locationPermissionRequest.requestLocationPermission()
                    }

                },
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }


        setEditTexts()
        handleLocationEditText()

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
                        val userId = CurrentUser.prefs.get()
                        if (userId != null) {
                            stepThreeModel.apply {
                                val l = LocationData(
                                    country = countryLiveData.value.toString(),
                                    city = "Rabat"
                                )

                                if (cityLiveData.value != null)
                                    l.city = cityLiveData.value.toString()

                                if (streetLiveData.value != null)
                                    l.street = streetLiveData.value.toString()

                                location = l
                                description = descriptionLiveData.value.toString()
                                ownerId = userId
                            }
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

                _countryLiveData.value = countryPicker.selectedCountryName

                countryPicker.setOnCountryChangeListener {
                    // your code to handle selected country
                    val country = countryPicker.selectedCountryName
                    Log.d(TAG, "country: $country")
                    _countryLiveData.postValue(country)
                    getCities(country)
                }

                //handle edit texts use input
                cityEditText.updateLiveData(_cityLiveData)
                streetEditText.updateLiveData(_streetLiveData)
                descriptionEditText.updateLiveData(_descriptionLiveData)

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun handleLocationEditText() {
        stepThreeModel.apply {
            binding.apply {

                //for test purposes
//                countryEditText.setWithList(listOf("Morocco", "Armenia", "UAE"), requireContext())

                cities.observe(viewLifecycleOwner) { cities ->
                    cityEditText.apply {
                        isEnabled = true
                        val adapter = setUpAndHandleSearch(cities)
                        setOnItemClickListener { _, view, _, _ ->
                            val city = (view as TextView).text

                            Log.d(TAG, "onItemSelected: $city")
                            adapter.filter.filter(null)

//                    getStreets(city)
                        }
                    }
                }
                streets.observe(viewLifecycleOwner) { streets ->
                    streetEditText.apply {
                        isEnabled = true
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