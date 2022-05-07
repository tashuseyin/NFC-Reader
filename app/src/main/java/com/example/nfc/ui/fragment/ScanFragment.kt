package com.example.nfc.ui.fragment

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nfc.common.Constant
import com.example.nfc.databinding.FragmentScanBinding
import com.example.nfc.ui.activities.NfcActivity
import com.example.nfc.util.NFCUtil
import org.jmrtd.lds.icao.MRZInfo


class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private var adapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNfc()
        readCard()
    }

    private fun setNfc() {
        adapter = NfcAdapter.getDefaultAdapter(requireActivity())
        pendingIntent = PendingIntent.getActivity(
            requireActivity(), 0,
            Intent(requireActivity(), NfcActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
    }


    private fun setMrzData(mrzInfo: MRZInfo) {
        val docNumber = "Doc Number:" + mrzInfo.documentNumber
        val expDate = "Expiry Date:" + mrzInfo.dateOfExpiry
        val birthDate = "Date Of Birth:" + mrzInfo.dateOfBirth
        binding.documentNumber.text = docNumber
        binding.expiryDate.text = expDate
        binding.dateOfBirth.text = birthDate
    }

    private fun readCard() {
        val mrzData = requireActivity().intent.getSerializableExtra(Constant.MRZ_RESULT)
        val mrzInfo = MRZInfo(mrzData.toString())
        setMrzData(mrzInfo)
    }


    private fun nfcScan() {
        val eDocument = NFCUtil.readNfcData(requireContext())
        findNavController().navigate(
            ScanFragmentDirections.actionScanFragmentToResultFragment(
                eDocument
            )
        )
    }


    override fun onResume() {
        super.onResume()
        if (adapter != null) {
            adapter!!.enableForegroundDispatch(requireActivity(), pendingIntent, null, null)
        }
    }


    override fun onPause() {
        super.onPause()
        if (adapter != null) {
            adapter!!.disableForegroundDispatch(requireActivity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}