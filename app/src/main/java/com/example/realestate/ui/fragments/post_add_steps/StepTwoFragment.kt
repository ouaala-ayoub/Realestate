package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.forEach
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.*
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

    private var _binding: FragmentStepTwoBinding? = null
    private val binding get() = _binding!!
    private val stepTwoModel: StepTwoModel by lazy {
        StepTwoModel(StaticDataRepository(Retrofit.getInstance()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStepTwoBinding.inflate(inflater, container, false)

        binding.stepTwoFullLayout.periodRg.setOnCheckedChangeListener { radioGroup, id ->
            val button = radioGroup.findViewById<RadioButton>(id)

            button?.apply {
                val period = text.toString()
                stepTwoModel.mutableLiveDataWrapper._periodLiveData.postValue(period)
            }

        }

        binding.stepTwoFullLayout.apply {
            whatsapp.setOnCheckedChangeListener { _, isChecked ->
                stepTwoModel.updateSelectedOptions(isChecked, call.isChecked)
            }

            call.setOnCheckedChangeListener { _, isChecked ->
                stepTwoModel.updateSelectedOptions(whatsapp.isChecked, isChecked)
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

        Categories.observe(viewLifecycleOwner, object : OnChanged<List<String>?> {
            override fun onChange(data: List<String>?) {
                Log.d(TAG, "onChange data: $data")
                if (data != null) {
                    binding.stepTwoFullLayout.categoryEditText.apply {
                        val categoriesToShow = data.sorted()
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

        })

        stepTwoModel.isValidData.observe(viewLifecycleOwner) { isValidData ->
            //update the state of the next button
            Log.d(TAG, "isValidData : $isValidData")
            Log.d(
                TAG,
                "contactTypeLiveData : ${stepTwoModel.liveDataWrapper.contactTypeLiveData.value}"
            )
            Log.d(TAG, "phoneLiveData : ${stepTwoModel.liveDataWrapper.phoneLiveData.value}")
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
            categoryEditText.updateLiveData(wrapper._categoryLiveData)
//            priceEditText.updateLiveData(wrapper._priceLiveData)
            priceEditText.addTextChangedListener(
                NumberTextWatcher(
                    priceEditText,
                    wrapper._priceLiveData
                )
            )
            phoneNumber.phoneEditText.updateLiveData(wrapper._phoneNumberLiveData)

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

                Log.d(TAG, "category: $category")
                val code =
                    binding.stepTwoFullLayout.phoneNumber.countryCode.selectedCountryCodeWithPlus
                val phoneNumber = phoneLiveData.value.toString()
                val contactType = contactTypeLiveData.value.toString()

                contact = Contact(code, phoneNumber, contactType)

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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}