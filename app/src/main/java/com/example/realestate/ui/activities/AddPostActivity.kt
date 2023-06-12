package com.example.realestate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.ActivityAddPostBinding
import com.example.realestate.ui.adapters.FragmentsAdapter
import com.example.realestate.ui.fragments.post_add_steps.ImagesSelectFragment
import com.example.realestate.ui.fragments.post_add_steps.StepTwoFragment
import com.google.android.material.tabs.TabLayoutMediator

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private lateinit var fragmentsList: List<FragmentStep>
    private lateinit var fragmentsAdapter: FragmentsAdapter
    private lateinit var post: Post
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        fragmentsList = listOf(ImagesSelectFragment(), StepTwoFragment())
        fragmentsAdapter = FragmentsAdapter(this, fragmentsList)
        post = Post.emptyPost

        val viewPager = binding.fragmentsViewPager

        //setting up the view pager with progressTabBar
        setViewPager()

        //handling next and back buttons
        binding.apply {
            back.setOnClickListener {
                fragmentsAdapter.onBackClicked(viewPager.currentItem, viewPager)
            }
            next.setOnClickListener {
                fragmentsAdapter.onNextClicked(viewPager.currentItem, viewPager, post)
            }
        }

        setContentView(binding.root)
    }


    private fun setViewPager() {
        binding.fragmentsViewPager.apply {
            offscreenPageLimit = fragmentsList.size
            adapter = fragmentsAdapter
            TabLayoutMediator(binding.progressTabBar, this, true) { tab, _ ->
                tab.view.isClickable = false
            }.attach()
            isUserInputEnabled = false
        }
    }
}