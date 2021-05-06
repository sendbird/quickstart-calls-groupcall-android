package com.sendbird.calls.quickstart.groupcall.main

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.ActivityMainBinding
import com.sendbird.calls.quickstart.groupcall.util.BaseActivity
import com.sendbird.calls.quickstart.groupcall.util.requestPermissions

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val tabLayout = binding.tabLayoutMain
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)

        val viewPager2 = binding.viewPagerMain
        viewPager2.adapter = ViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            val iconResourceId = when (position) {
                0 -> R.drawable.icon_rooms
                1 -> R.drawable.icon_settings
                else -> return@TabLayoutMediator
            }

            tab.setIcon(iconResourceId)
        }.attach()
        requestPermissions()
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> tab.setIcon(R.drawable.icon_rooms_filled)
                1 -> tab.setIcon(R.drawable.icon_settings_filled)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> tab.setIcon(R.drawable.icon_rooms_grey)
                1 -> tab.setIcon(R.drawable.icon_settings_grey)
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    inner class ViewPagerAdapter(
        fragmentActivity: FragmentActivity
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DashboardFragment()
                1 -> SettingsContainerFragment()
                else -> throw IndexOutOfBoundsException()
            }
        }
    }
}
