package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.FragmentStepTwoBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.postaddmodels.StepTwoModel
import com.example.realestate.utils.setUpAndHandleSearch
import com.example.realestate.utils.updateLiveData

class StepTwoFragment : FragmentStep() {

    companion object {
        private const val TAG = "StepTwoFragment"
    }

    private lateinit var binding: FragmentStepTwoBinding
    private val stepTwoModel: StepTwoModel by lazy {
        StepTwoModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStepTwoBinding.inflate(inflater, container, false)

        stepTwoModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.categoryEditText.apply {
                val adapter = setUpAndHandleSearch(categories, requireContext())

                //clear filter after user choose one item
                setOnItemClickListener { _, view, _, _ ->
                    adapter.filter.filter(null)
                }
            }
        }

        //validity of the data entered handling
        validateTheData(binding.usd.text.toString())

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val lastState = stepTwoModel.isValidData.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }

    private fun validateTheData(currencyDefault: String) {

        binding.apply {
            val wrapper = stepTwoModel.mutableLiveDataWrapper

            //test purposes
            wrapper._categoryLiveData.value = "are"

            //user input
            titleEditText.updateLiveData(wrapper._titleLiveData)
            categoryEditText.updateLiveData(wrapper._categoryLiveData)
            priceEditText.updateLiveData(wrapper._priceLiveData)

            //currencies
            wrapper._currencyLiveData.value = currencyDefault
            usd.setOnClickListener { wrapper._currencyLiveData.value = usd.text.toString() }
            eu.setOnClickListener { wrapper._currencyLiveData.value = eu.text.toString() }

        }

        stepTwoModel.isValidData.observe(viewLifecycleOwner) { isValidData ->
            Log.d(TAG, "validateTheData: ${stepTwoModel.mutableLiveDataWrapper}")
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValidData)
        }
    }

    override fun onNextClicked(viewPager: ViewPager2, post: Post) {
        viewPager.currentItem++

//        add logic
        (requireActivity() as AddPostActivity).post.apply {
            stepTwoModel.liveDataWrapper.apply {
                title = titleLiveData.value.toString()
                category = categoryLiveData.value.toString()
                price = priceLiveData.value!!.toInt()
                currency = currencyLiveData.value.toString()
            }
        }
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }


}