package com.example.realestate.ui.fragments.post_add_steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.FragmentStepTwoBinding

class StepTwoFragment : FragmentStep() {

    private lateinit var binding: FragmentStepTwoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStepTwoBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onNextClicked(viewPager: ViewPager2, post: Post) {
        viewPager.currentItem++

//        add logic
//        post.media = mediaList.value
    }

    override fun onBackClicked(viewPager: ViewPager2) {
//        showLeaveDialog()
        viewPager.currentItem--
    }

}