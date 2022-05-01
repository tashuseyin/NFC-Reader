package com.example.nfc.ui.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.nfc.R
import com.example.nfc.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation: BottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.nav_host_fragment)
        AppBarConfiguration.Builder(
            R.id.scan,
            R.id.history
        ).build()

        bottomNavigation.setupWithNavController(navController)

        cameraPermission()
    }

    private fun openCameraActivity() {
       // val intent = Intent(this, CaptureActivity::class.java)
        //startActivity(intent)
    }

    private fun cameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    openCameraActivity()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@MainActivity,
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
                Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show()
            }.onSameThread()
            .check()
    }

}