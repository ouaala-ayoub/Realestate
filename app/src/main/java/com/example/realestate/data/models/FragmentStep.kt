package com.example.realestate.data.models

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.utils.HandleSubmitInterface

open class FragmentStep : Fragment(), HandleSubmitInterface {
    override fun onNextClicked(viewPager: ViewPager2, post: Post) {
    }

    override fun onBackClicked(viewPager: ViewPager2) {
    }
}
