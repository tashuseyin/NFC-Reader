package com.example.nfc.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.nfc.common.Constant
import com.example.nfc.databinding.FragmentHomeBinding
import com.example.nfc.model.DocType
import com.example.nfc.ui.activities.CaptureActivity


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var docType: DocType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
    }


    private fun setListener() {
        binding.apply {
            constraintKimlik.setOnClickListener {
                docType = DocType.ID_CARD
                requestPermissionForCamera()
            }
            constraintPasaport.setOnClickListener {
                docType = DocType.PASSPORT
                requestPermissionForCamera()
            }
        }
    }

    private fun openCameraActivity() {
        val intent = Intent(requireActivity(), CaptureActivity::class.java)
        intent.putExtra(Constant.DOC_TYPE, docType)
        startActivityForResult(intent, Constant.APP_CAMERA_ACTIVITY_REQUEST_CODE)
    }

    private fun requestPermissionForCamera() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                Constant.APP_CAMERA_ACTIVITY_REQUEST_CODE
            )
        } else {
            openCameraActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.APP_CAMERA_ACTIVITY_REQUEST_CODE) {
            val result = grantResults[0]
            if (result == PackageManager.PERMISSION_DENIED) {
                requestPermissionForCamera()
            } else if (result == PackageManager.PERMISSION_GRANTED) {
                openCameraActivity()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}