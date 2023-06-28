package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Type
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentStepTwoBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.viewmodels.postaddmodels.StepTwoModel
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
                    //initial values
                    setText(categories[0])

                    val adapter = setUpAndHandleSearch(categories)

                    //clear filter after user choose one item
                    setOnItemClickListener { _, _, _, _ ->
                        adapter.filter.filter(null)
                    }
                }
            } else {
                doOnFail()
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
            categoryEditText.updateLiveData(wrapper._categoryLiveData)
            priceEditText.updateLiveData(wrapper._priceLiveData)

            //currencies
            wrapper._typeLiveData.value = typeDefault
            rent.setOnClickListener { wrapper._typeLiveData.value = rent.text.toString() }
            forSell.setOnClickListener { wrapper._typeLiveData.value = forSell.text.toString() }

        }

        stepTwoModel.isValidData.observe(viewLifecycleOwner) { isValidData ->
            //update the state of the next button
            Log.d(TAG, "isValidData : $isValidData")
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValidData)
        }
    }

    override fun onNextClicked(viewPager: ViewPager2) {
        viewPager.currentItem++

//        add logic
        (requireActivity() as AddPostActivity).post.apply {
            stepTwoModel.liveDataWrapper.apply {
                category = categoryLiveData.value.toString()
                price = priceLiveData.value!!.toInt()
                type = typeLiveData.value.toString()
            }
        }
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        viewPager.currentItem--
    }


}