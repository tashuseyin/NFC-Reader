package com.example.nfc.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nfc.model.Passport

class PagerAdapter(
    private val resultBundle: Passport,
    private val fragments: ArrayList<Fragment>,
    private val fm: FragmentActivity,
) : FragmentStateAdapter(fm) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}