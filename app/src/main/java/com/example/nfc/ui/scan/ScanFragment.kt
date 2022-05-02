package com.example.nfc.ui.scan

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nfc.common.Constant.Companion.DOC_TYPE
import com.example.nfc.databinding.FragmentScanBinding
import com.example.nfc.model.DocType
import com.example.nfc.ui.activities.CaptureActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


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
        cameraPermission()
        setListener()
    }

    private fun setListener() {
        binding.apply {
            constraintKimlik.setOnClickListener {
                openCameraActivity(DocType.ID_CARD)
            }
            constraintPasaport.setOnClickListener {
                openCameraActivity(DocType.PASSPORT)
            }
        }
    }

    private fun openCameraActivity(docType: DocType) {
        val intent = Intent(requireActivity(), CaptureActivity::class.java)
        intent.putExtra(DOC_TYPE, docType)
        startActivity(intent)
    }

    private fun cameraPermission() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {}

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        context,
                        "You have denied storage permission.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show()
            }.onSameThread()
            .check()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}