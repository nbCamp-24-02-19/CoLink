package com.seven.colink.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seven.colink.databinding.ItemHomeBottomViewpagerBinding
import com.seven.colink.ui.home.child.HomeProjectFragment
import com.seven.colink.ui.home.child.HomeStudyFragment
import com.seven.colink.ui.main.MainActivity
import org.checkerframework.checker.nullness.qual.NonNull

class BottomViewPagerAdapter (frag : Fragment) : FragmentStateAdapter(frag) {
//    private lateinit var bottomViewPagerAdapter: BottomViewPagerAdapter
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

