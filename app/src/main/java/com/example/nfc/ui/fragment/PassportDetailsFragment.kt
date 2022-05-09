package com.example.nfc.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nfc.common.Constant
import com.example.nfc.databinding.FragmentPassportDetailsBinding
import com.example.nfc.model.Passport
import com.google.android.material.tabs.TabLayoutMediator

class PassportDetailsFragment : Fragment() {

    private var _binding: FragmentPassportDetailsBinding? = null
    private val binding get() = _binding!!
    private var passport: Passport? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassportDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments!!.containsKey(Constant.KEY_PASSPORT)) {
            passport = arguments.getParcelable(Constant.KEY_PASSPORT)
        }

        setParameterPagerAdapter()
    }

    private fun setParameterPagerAdapter() {
        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(DetailFragment())
        fragments.add(AdvancedFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Detail")
        titles.add("Advanced")


        val adapter = PagerAdapter(
            fragments,
            requireActivity()
        )
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    companion object {
        fun newInstance(passport: Passport): PassportDetailsFragment {
            val myFragment = PassportDetailsFragment()
            val args = Bundle()
            args.putParcelable(Constant.KEY_PASSPORT, passport)
            myFragment.arguments = args
            return myFragment
        }
    }
}
