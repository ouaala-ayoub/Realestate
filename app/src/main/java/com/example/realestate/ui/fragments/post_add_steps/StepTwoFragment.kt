package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.Contact
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Type
import com.example.realestate.data.models.categories
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentStepTwoBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.postaddmodels.StepTwoModel
import com.example.realestate.utils.capitalizeFirstLetter
import com.example.realestate.utils.doOnFail
import com.example.realestate.utils.setUpAndHandleSearch
import com.example.realestate.utils.updateLiveData

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

        stepTwoModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories != null) {
                binding.categoryEditText.apply {
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

        //validity of the data entered handling
        validateTheData(Type.RENT.value)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val lastState = stepTwoModel.isValidData.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }

    private fun validateTheData(typeDefault: String) {

        binding.apply {
            val wrapper = stepTwoModel.mutableLiveDataWrapper

            //user input
            categoryEditText.updateLiveData(wrapper._categoryLiveData, true)
            priceEditText.updateLiveData(wrapper._priceLiveData)
            whatsappPhoneInput.phoneEditText.updateLiveData(wrapper._whatsappNumberLiveData)
            callPhoneInput.phoneEditText.updateLiveData(wrapper._callNumberLiveData)

            wrapper._typeLiveData.apply {
                value = typeDefault
                rent.setOnClickListener { value = rent.text.toString() }
                forSell.setOnClickListener { value = forSell.text.toString() }
            }

        }

        stepTwoModel.isValidData.observe(viewLifecycleOwner) { isValidData ->
            //update the state of the next button
            Log.d(TAG, "isValidData : $isValidData")
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValidData)
        }
    }

    override fun onNextClicked(viewPager: ViewPager2) {
        val post = (requireActivity() as AddPostActivity).post
//        add logic
        post.apply {
            stepTwoModel.liveDataWrapper.apply {

                category = categoryLiveData.value.toString()
                price = priceLiveData.value!!.toInt()
                type = typeLiveData.value.toString()

                val contactInfo = Contact()
                val whatsappNumber = whatsappNumberLiveData.value
                val callNumber = callNumberLiveData.value

                if (!whatsappNumber.isNullOrEmpty()) {
                    val code = binding.whatsappPhoneInput.countryCode.selectedCountryCodeWithPlus
                    contactInfo.whatsapp = code + whatsappNumber
                }

                if (!callNumber.isNullOrEmpty()) {
                    val code = binding.callPhoneInput.countryCode.selectedCountryCodeWithPlus
                    contactInfo.call = code + callNumber
                }


                contact = contactInfo

                Log.d(TAG, "psot: $post")

                //TODO change with a call to the local database
                if (category == categories[0] || category == categories[1] || category == categories[2]) {
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