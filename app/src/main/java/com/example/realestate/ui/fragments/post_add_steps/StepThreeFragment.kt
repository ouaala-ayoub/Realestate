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
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Location
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.Type
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.databinding.FragmentStepThreeBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.postaddmodels.StepThreeModel
import com.example.realestate.utils.*
import com.google.android.material.chip.Chip

class StepThreeFragment : FragmentStep() {

    companion object {
        private const val TAG = "StepThreeFragment"
    }

    private lateinit var binding: FragmentStepThreeBinding
    private val stepThreeModel: StepThreeModel by lazy {
        StepThreeModel(PostsRepository(Retrofit.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                }
                requireActivity().finish()
            }
        }

        setEditTexts(Type.RENT.value)
        handleLocationEditText()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val lastState = stepThreeModel.isDataValid.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }

    override fun onNextClicked(viewPager: ViewPager2, post: Post) {
        super.onNextClicked(viewPager, post)
        // send the request

        makeDialog(
            requireContext(),
            object : OnDialogClicked {
                override fun onPositiveButtonClicked() {
                    (requireActivity() as AddPostActivity).post.apply {
                        stepThreeModel.apply {
                            location = Location(
                                country = countryLiveData.value.toString(),
                                city = cityLiveData.value.toString(),
                                street = streetLiveData.value.toString()
                            )
                            type = typeLiveData.value.toString()
                            description = descriptionLiveData.value.toString()
                        }
                        stepThreeModel.addPost(this)
                    }
                }

                override fun onNegativeButtonClicked() {}

            },
            getString(R.string.finish_dialog_title),
            getString(R.string.finish_dialog_message)
        )


    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }

    private fun setEditTexts(defaultType: String) {
        binding.apply {
            stepThreeModel.apply {

                _typeLiveData.value = defaultType

                //handle edit texts use input
                countryEditText.updateLiveData(_countryLiveData)
                cityEditText.updateLiveData(_cityLiveData)
                streetEditText.updateLiveData(_streetLiveData)
                descriptionEditText.updateLiveData(_descriptionLiveData)

                //handle chips user input
                for (view in chips.children) {
                    val chip = view as Chip
                    chip.setOnClickListener { _typeLiveData.postValue(chip.text.toString()) }
                }
            }
        }
    }

    private fun handleLocationEditText() {
        stepThreeModel.apply {
            binding.apply {

                //for test purposes
//                countryEditText.setWithList(listOf("Morocco", "Armenia", "UAE"), requireContext())

                countries.observe(viewLifecycleOwner) { countries ->
                    countryEditText.apply {
                        val adapter = setUpAndHandleSearch(countries, requireContext())
                        setOnItemClickListener { _, view, _, _ ->
                            val country = (view as TextView).text

                            Log.d(TAG, "onItemSelected: $country")
                            adapter.filter.filter(null)

//                    getCities(country)
                        }
                    }
                }
                cities.observe(viewLifecycleOwner) { cities ->
                    cityEditText.apply {
                        isEnabled = true
                        val adapter = setUpAndHandleSearch(cities, requireContext())
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
                        val adapter = setUpAndHandleSearch(streets, requireContext())
                        setOnItemClickListener { _, _, _, _ ->
                            adapter.filter.filter(null)
                        }
                    }
                }

            }
        }
    }
}