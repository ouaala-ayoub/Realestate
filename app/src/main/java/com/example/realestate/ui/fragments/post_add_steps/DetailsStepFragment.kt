package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText

class DetailsStepFragment : FragmentStep() {

    companion object {
        private const val TAG = "DetailsStepFragment"
    }

    private var isConverting: Boolean = false
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

                extrasDetailsChipGrp.setOnCheckedStateChangeListener { group, checkedIds ->
                    extrasDetailsChipGrp.forEach { view ->
                        val chip = view as Chip
                        val text = chip.text
                        val lambda = mapOfFunctions["$text"]
                        val isChecked = chip.isChecked

                        lambda?.invoke(isChecked)
                    }

                }

                proprietyConditionRg.setOnCheckedChangeListener { radioGroup, i ->
                    val radioButton =
                        radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                    val state = radioButton.text.toString()

                    setProprietyState(state)
                }

                numberOfBedroomsEditText.doOnTextChanged { text, _, _, _ ->
                    setNumberOfBedrooms(text.toString())
                }

                floorNumberEditText.doOnTextChanged { text, _, _, _ ->
                    setFloorNumber(text.toString())
                }

                converter.apply {
                    spaceMeterEditText.doOnTextChanged { text, _, _, _ ->
                        updateValue(text, spaceFootEditText, ::squareMeterToSquareFoot, ::setSpace)
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
        val details = viewModel.getResult()
        Log.i(TAG, "details: $details")
        (requireActivity() as AddPostActivity).post.details = details
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

    private fun updateValue(
        sourceText: CharSequence?,
        targetInput: TextInputEditText,
        converter: (Double) -> Double,
        setLiveData: ((Number?) -> Unit)? = null
    ) {
        if (!isConverting) {
            isConverting = true

            val inputValue = sourceText.toString().toDoubleOrNull()
            setLiveData?.invoke(inputValue)

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