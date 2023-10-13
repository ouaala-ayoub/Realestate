package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.view.forEach
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.databinding.FragmentDetailsStepBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.DetailsStepViewModel
import com.example.realestate.utils.formatDecimal
import com.example.realestate.utils.squareFeetToSquareMeters
import com.example.realestate.utils.squareMeterToSquareFoot
import com.google.android.material.textfield.TextInputEditText

class DetailsStepFragment : FragmentStep() {

    companion object {
        private const val TAG = "DetailsStepFragment"
    }

    private var isConverting: Boolean = false
    private var _binding: FragmentDetailsStepBinding?=null
    private val binding get() = _binding!!
    private val viewModel: DetailsStepViewModel by lazy {
        DetailsStepViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsStepBinding.inflate(inflater, container, false)
        // Set the ViewModel variable in the layout to the ViewModel instance

        binding.apply {
            viewModel.apply {
                // Set up listeners for user input changes

                detailsFullLayout.features.proprietyDetailsCg.forEach { view ->
                    val checkBox = view as CheckBox

                    checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                        val feature = compoundButton.text.toString()
                        if (isChecked) {
                            viewModel.addFeature(feature)
                        } else {
                            viewModel.deleteFeature(feature)
                        }
                    }
                }

                detailsFullLayout.proprietyConditionRg.setOnCheckedChangeListener { radioGroup, i ->
                    val radioButton =
                        radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                    val state = radioButton.text.toString()

                    setProprietyState(state)
                }

                detailsFullLayout.numberOfRoomsEditText.doOnTextChanged { text, _, _, _ ->
                    setNumberOfRooms(text.toString())
                }

                detailsFullLayout.numberOfElevatorsEditText.doOnTextChanged { text, _, _, _ ->
                    setNumberOfElevators(text.toString())
                }

                detailsFullLayout.floorInfo.floorNumberEditText.doOnTextChanged { text, _, _, _ ->
                    setFloorNumber(text.toString())
                }

                detailsFullLayout.floorInfo.numberOfFloorsEditText.doOnTextChanged { text, _, _, _ ->
                    setNumberOfFloors(text.toString())
                }

                detailsFullLayout.converter.apply {
                    spaceMeterEditText.doOnTextChanged { text, _, _, _ ->
                        updateValue(text, spaceFootEditText, ::squareMeterToSquareFoot, ::setSpace)
                        setSpace(text.toString())
                    }
                    spaceFootEditText.doOnTextChanged { text, _, _, _ ->
                        updateValue(text, spaceMeterEditText, ::squareFeetToSquareMeters)
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
        (requireActivity() as AddPostActivity).post.apply {
            viewModel.apply {
                features = featuresLiveData.value
                condition = propertyState.value
                rooms = numberOfRooms.value
                elevators = numberOfElevators.value
                floors = numberOfFloors.value
                floorNumber = floorNumberLiveData.value
                space = spaceLiveData.value
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateValue(
        sourceText: CharSequence?,
        targetInput: TextInputEditText,
        converter: (Double) -> Double,
        setLiveData: ((String?) -> Unit)? = null
    ) {
        if (!isConverting) {
            isConverting = true

            val inputValue = sourceText.toString().toDoubleOrNull()
            setLiveData?.invoke(sourceText.toString())

            if (inputValue != null) {
                val convertedValue = converter(inputValue)
                targetInput.setText(formatDecimal(convertedValue))
            } else {
                targetInput.setText("")
            }

            isConverting = false
        }
    }
}