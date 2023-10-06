package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
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

class StepThreeFragment : FragmentStep() {

    companion object {
        private const val TAG = "StepThreeFragment"
    }

    private var _binding: FragmentStepThreeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity: AddPostActivity
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
        activity = (requireActivity() as AddPostActivity)
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
        _binding = FragmentStepThreeBinding.inflate(inflater, container, false)


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
        activity.addPostModel.updateIsValidData(lastState)
    }

    override fun onNextClicked(viewPager: ViewPager2) {
        super.onNextClicked(viewPager)
        // send the request

        val dialog = makeDialog(
            requireContext(),
            object : OnDialogClicked {
                override fun onPositiveButtonClicked() {

                    activity.post.apply {
                        if (CurrentUser.isConnected()) {
                            val uris = activity.selectedMedia
                            stepThreeModel.apply {
                                val l = Location(
                                    country = countryLiveData.value.toString(),
                                    city = cityLiveData.value.toString()
                                )

                                l.area = if (streetLiveData.value != null)
                                    streetLiveData.value.toString()
                                else
                                    ""

                                location = l
                                description = descriptionLiveData.value.toString()
                            }
                            Log.i(TAG, "post: $this")
                            stepThreeModel.addPost(this, uris, requireContext())
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
        binding.stepThreeFullLayout.apply {
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
                activity.addPostModel.updateIsValidData(isValid)
            }

            loading.observe(viewLifecycleOwner) { loading ->
                Log.d(TAG, "loading : $loading")
                binding.progressBar.isVisible = loading
                for (v in binding.stepThreeFullLayout.linearLayout.children) {
                    v.isEnabled = !loading
                }
                activity.addPostModel.apply {
                    updateIsValidData(false)
                    updateIsBackEnabled(!loading)
                }
            }

            requestResponse.observe(viewLifecycleOwner) { requestResponse ->
                Log.d(TAG, "onCreateView: ${requestResponse?.message}")
                requireContext().toast(requestResponse?.message!!, Toast.LENGTH_SHORT)
                if (requestResponse.message == "post created") {
                    requireActivity().finish()
                }
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
            binding.stepThreeFullLayout.apply {

                countries.observe(viewLifecycleOwner) { countries ->
                    Log.d(TAG, "cities: $countries")

                    countriesData = countries

                    countries?.apply {
                        countryEditText.apply {

                            val adapter = setUpCountriesAndHandleSearch(countries)
                            adapter.setOnItemClickListener { selectedItem ->
                                val name = selectedItem.name

                                if (!name.isNullOrEmpty()) {
                                    setText(name.toString(), false)
                                    setSelection(name.length)
                                    adapter.filter.filter(null)
                                    dismissDropDown()
                                    getCities(name)
                                    cityEditText.text.clear()
                                    streetEditText.text.clear()
                                }
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}