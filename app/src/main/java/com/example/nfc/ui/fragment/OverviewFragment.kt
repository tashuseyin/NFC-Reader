package com.example.nfc.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nfc.databinding.FragmentOverviewBinding
import com.example.nfc.model.Passport


class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!
    private var passport: Passport? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        passport = (parentFragment as PassportDetailsFragment).passport
    }

    override fun onResume() {
        super.onResume()
        setViewData(passport)
    }


    private fun setViewData(passport: Passport?) {
        if (passport == null) {
            return
        }
        val personDetails = passport.personDetails
        if (personDetails != null) {
            binding.userSurname.text = personDetails.primaryIdentifier!!.replace("<", "")
            binding.userName.text = personDetails.secondaryIdentifier!!.replace("<", "")
            binding.identityNumber.text = personDetails.documentCode
            binding.userBirthDate.text = personDetails.dateOfBirth
            binding.userExpiryDate.text = personDetails.dateOfExpiry
            binding.userGender.text = personDetails.gender!!.name
            binding.userNationality.text = personDetails.nationality
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}