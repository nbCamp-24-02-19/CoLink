package com.seven.colink.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seven.colink.ui.home.child.HomeProjectFragment
import com.seven.colink.ui.home.child.HomeStudyFragment

class BottomViewPagerAdapter (frag : Fragment) : FragmentStateAdapter(frag) {
    private val frags = listOf<Fragment>(HomeProjectFragment(),HomeStudyFragment())

    override fun getItemCount(): Int {
        return frags.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeProjectFragment()
            else -> HomeStudyFragment()
        }
    }
}

