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
import com.example.realestate.data.models.Contact
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Type
import com.example.realestate.data.models.extras
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentStepTwoBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.postaddmodels.StepTwoModel
import com.example.realestate.utils.*

class StepTwoFragment : FragmentStep() {

    companion object {
        private const val TAG = "StepTwoFragment"
    }

    private lateinit var binding: FragmentStepTwoBinding
    private val stepTwoModel: StepTwoModel by lazy {
        StepTwoModel(StaticDataRepository(Retrofit.getInstance()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStepTwoBinding.inflate(inflater, container, false)

        binding.stepTwoFullLayout.periodRg.setOnCheckedChangeListener { radioGroup, id ->
            val button = radioGroup.findViewById<RadioButton>(id)

            button?.apply {
                val period = text.toString()
                stepTwoModel.mutableLiveDataWrapper._periodLiveData.postValue(period)
            }

        }

        //validity of the data entered handling
        validateTheData(Type.RENT.value)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepTwoModel.liveDataWrapper.typeLiveData.observe(viewLifecycleOwner) { type ->
            val shouldShow = type == Type.RENT.value
            binding.stepTwoFullLayout.periodRg.forEach { it.isEnabled = shouldShow }
            if (!shouldShow) {
                binding.stepTwoFullLayout.periodRg.clearCheck()
                stepTwoModel.mutableLiveDataWrapper.clearPeriod()
            }
        }

        stepTwoModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories != null) {
                binding.stepTwoFullLayout.categoryEditText.apply {
                    val categoriesToShow = categories.capitalizeFirstLetter().sorted()
                    //initial values
                    setText(categoriesToShow[0])

                    val adapter = setUpAndHandleSearch(categoriesToShow)

                    //clear filter after user choose one item
                    setOnItemClickListener { _, _, _, _ ->
                        adapter.filter.filter(null)
                    }
                }
            } else {
                requireActivity().doOnFail()
            }
        }

        stepTwoModel.isValidData.observe(viewLifecycleOwner) { isValidData ->
            //update the state of the next button
            Log.d(TAG, "isValidData : $isValidData")
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValidData)
        }

    }

    override fun onResume() {
        super.onResume()
        val lastState = stepTwoModel.isValidData.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }

    private fun validateTheData(typeDefault: String) {

        binding.stepTwoFullLayout.apply {
            val wrapper = stepTwoModel.mutableLiveDataWrapper

            //user input
            categoryEditText.updateLiveData(wrapper._categoryLiveData, true)
//            priceEditText.updateLiveData(wrapper._priceLiveData)
            priceEditText.addTextChangedListener(
                NumberTextWatcher(
                    priceEditText,
                    wrapper._priceLiveData
                )
            )
            whatsappPhoneInput.phoneEditText.updateLiveData(wrapper._whatsappNumberLiveData)
            callPhoneInput.phoneEditText.updateLiveData(wrapper._callNumberLiveData)

            wrapper._typeLiveData.apply {
                value = typeDefault
                rent.setOnClickListener {
                    value = Type.RENT.value
                }
                forSell.setOnClickListener {
                    value = Type.BUY.value
                }
            }

        }


    }

    override fun onNextClicked(viewPager: ViewPager2) {
        val post = (requireActivity() as AddPostActivity).post
//        add logic
        post.apply {
            stepTwoModel.liveDataWrapper.apply {

                category = categoryLiveData.value.toString()
                price = priceLiveData.value!!.toDouble()
                period = periodLiveData.value
                type = typeLiveData.value.toString()

                Log.d(TAG, "price: $price")

                val contactInfo = Contact()
                val whatsappNumber = whatsappNumberLiveData.value
                val callNumber = callNumberLiveData.value

                if (!whatsappNumber.isNullOrEmpty()) {
                    val code =
                        binding.stepTwoFullLayout.whatsappPhoneInput.countryCode.selectedCountryCodeWithPlus
                    contactInfo.whatsapp = code + whatsappNumber
                }

                if (!callNumber.isNullOrEmpty()) {
                    val code =
                        binding.stepTwoFullLayout.callPhoneInput.countryCode.selectedCountryCodeWithPlus
                    contactInfo.call = code + callNumber
                }


                contact = contactInfo

                //TODO change with a call to the local database
                if (extras.contains(category)) {
                    viewPager.currentItem++
                } else {
                    viewPager.currentItem += 2
                }
            }
        }

    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }


}