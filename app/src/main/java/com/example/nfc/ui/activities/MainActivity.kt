package com.example.nfc.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nfc.common.Constant.Companion.APP_CAMERA_ACTIVITY_REQUEST_CODE
import com.example.nfc.common.Constant.Companion.DOC_TYPE
import com.example.nfc.databinding.ActivityMainBinding
import com.example.nfc.model.DocType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var docType: DocType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val intent = Intent(this, CaptureActivity::class.java)
        intent.putExtra(DOC_TYPE, docType)
        startActivityForResult(intent, APP_CAMERA_ACTIVITY_REQUEST_CODE)
    }

    private fun requestPermissionForCamera() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                APP_CAMERA_ACTIVITY_REQUEST_CODE
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
        if (requestCode == APP_CAMERA_ACTIVITY_REQUEST_CODE) {
            val result = grantResults[0]
            if (result == PackageManager.PERMISSION_DENIED) {
                requestPermissionForCamera()
            } else if (result == PackageManager.PERMISSION_GRANTED) {
                openCameraActivity()
            }
        }
    }
}