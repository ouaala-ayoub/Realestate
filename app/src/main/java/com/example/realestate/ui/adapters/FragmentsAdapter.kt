package com.example.realestate.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Post

class FragmentsAdapter(fa: FragmentActivity, private val fragmentList: List<FragmentStep>) :
    FragmentStateAdapter(fa) {

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun onNextClicked(viewPager: ViewPager2, post: Post) {
        fragmentList[viewPager.currentItem].onNextClicked(viewPager, post)
    }

    fun onBackClicked(viewPager: ViewPager2) {
        fragmentList[viewPager.currentItem].onBackClicked(viewPager)
    }

}