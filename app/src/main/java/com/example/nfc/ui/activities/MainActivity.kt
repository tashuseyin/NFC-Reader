package com.example.nfc.ui.activities

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nfc.common.Constant.Companion.APP_CAMERA_ACTIVITY_REQUEST_CODE
import com.example.nfc.common.Constant.Companion.DOC_TYPE
import com.example.nfc.databinding.ActivityMainBinding
import com.example.nfc.model.DocType
import org.jmrtd.lds.icao.MRZInfo

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var docType: DocType
    private var adapter: NfcAdapter? = null
    private var passportNumber: String? = null
    private var expirationDate: String? = null
    private var birthDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
    }

    private fun setMrzData(mrzInfo: MRZInfo) {
        adapter = NfcAdapter.getDefaultAdapter(this)
        passportNumber = mrzInfo.documentNumber
        expirationDate = mrzInfo.dateOfExpiry
        birthDate = mrzInfo.dateOfBirth
    }

    private fun readCard() {
        val mrzData = "P<GBPANGELA<ZOE<<SMITH<<<<<<<<<<<<<<<<<<<<<<" +
                "9990727768GBR7308196F2807041<<<<<<<<<<<<<<02"
        val mrzInfo = MRZInfo(mrzData)
        setMrzData(mrzInfo)
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


    override fun onResume() {
        super.onResume()
        if (adapter != null) {
            val intent = Intent(applicationContext, this.javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
            adapter!!.enableForegroundDispatch(this, pendingIntent, null, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        if (adapter != null) {
            adapter!!.disableForegroundDispatch(this)
        }
    }

}