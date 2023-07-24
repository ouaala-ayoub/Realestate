package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.databinding.FragmentDetailsStepBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.DetailsStepViewModel

class DetailsStepFragment : FragmentStep() {

    companion object {
        private const val TAG = "DetailsStepFragment"
    }

    private lateinit var binding: FragmentDetailsStepBinding
    private val viewModel: DetailsStepViewModel by lazy {
        DetailsStepViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsStepBinding.inflate(inflater, container, false)
        // Set the ViewModel variable in the layout to the ViewModel instance

        binding.apply {
            viewModel.apply {
                // Set up listeners for user input changes
                buildingAgeEditText.doOnTextChanged { text, _, _, _ ->
                    setBuildingAge(text.toString())
                }

                isFurnishedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    setIsFurnished(isChecked)
                }

                hasBalconyCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    setHasBalcony(isChecked)
                }

                isNewCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    setIsNew(isChecked)
                }

                hasGymCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    setHasGym(isChecked)
                }

                hasSwimmingPoolCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    setHasSwimmingPool(isChecked)
                }

                hasParkingCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    setHasParking(isChecked)
                }

                numberOfBedroomsEditText.doOnTextChanged { text, _, _, _ ->
                    setNumberOfBedrooms(text.toString())
                }

                floorNumberEditText.doOnTextChanged { text, _, _, _ ->
                    setFloorNumber(text.toString())
                }

                spaceEditText.doOnTextChanged { text, _, _, _ ->
                    val spaceValue = text?.toString()?.toIntOrNull()
                    if (spaceValue != null) {
                        setSpace(spaceValue)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe the LiveData for form validation
        viewModel.validationLiveData.observe(viewLifecycleOwner) { isFormValid ->
            // Enable or disable the submit button based on the form validity
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isFormValid)
        }
    }

    override fun onNextClicked(viewPager: ViewPager2) {
        (requireActivity() as AddPostActivity).post.details = viewModel.getFinalDetails()
        viewPager.currentItem++
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }

    override fun onResume() {
        super.onResume()
        val lastState = viewModel.validationLiveData.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }
}