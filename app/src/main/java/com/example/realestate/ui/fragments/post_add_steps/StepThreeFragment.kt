package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Location
import com.example.realestate.data.models.Post
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.databinding.FragmentStepThreeBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.StepThreeModel
import com.example.realestate.utils.setUpAndHandleSearch
import com.example.realestate.utils.setWithList
import com.example.realestate.utils.updateLiveData
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

        stepThreeModel.isDataValid.observe(viewLifecycleOwner) { isValid ->
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValid)
        }

        setEditTexts()
        handleLocationEditText()

        return binding.root
    }

    override fun onNextClicked(viewPager: ViewPager2, post: Post) {
        super.onNextClicked(viewPager, post)
        // send the request

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
        }
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }

    private fun setEditTexts() {
        binding.apply {
            stepThreeModel.apply {

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
                countryEditText.setWithList(listOf("Morocco", "Armenia", "UAE"), requireContext())

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