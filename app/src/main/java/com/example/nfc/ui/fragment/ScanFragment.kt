package com.example.nfc.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nfc.common.Constant
import com.example.nfc.databinding.FragmentScanBinding
import org.jmrtd.lds.icao.MRZInfo


class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readCard()
    }

    private fun setMrzData(mrzInfo: MRZInfo) {
        binding.cardNumber.text = mrzInfo.documentNumber
        binding.expirationDate.text = mrzInfo.dateOfExpiry
        binding.birthDate.text = mrzInfo.dateOfBirth
    }

    private fun readCard() {
        val mrzData = requireActivity().intent.getSerializableExtra(Constant.MRZ_RESULT)
        val mrzInfo = MRZInfo(mrzData.toString())
        setMrzData(mrzInfo)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}