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
    var passport: Passport? = null

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

        setViewData(passport)
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
            passport!!,
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


    private fun setViewData(passport: Passport?) {

        if (passport!!.face != null) {
            binding.userImage.setImageBitmap(passport.face)
        } else if (passport.portrait != null) {
            binding.userImage.setImageBitmap(passport.portrait)
        }

        val personDetails = passport.personDetails
        if (personDetails != null) {
            val surname = personDetails.primaryIdentifier!!.replace("<", "")
            val name = personDetails.secondaryIdentifier!!.replace("<", "")
            val fullName = "$name $surname"
            binding.userName.text = fullName
            binding.userGender.text = personDetails.gender!!.name
        }
    }

}
