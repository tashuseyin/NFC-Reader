package com.example.nfc.ui.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc.common.Constant.Companion.DOC_TYPE
import com.example.nfc.common.Constant.Companion.MRZ_RESULT
import com.example.nfc.databinding.ActivityCaptureBinding
import com.example.nfc.mlkit.camera.CameraSource
import com.example.nfc.mlkit.camera.CameraSourcePreview
import com.example.nfc.mlkit.other.GraphicOverlay
import com.example.nfc.mlkit.text.TextRecognitionProcessor
import com.example.nfc.model.DocType
import org.jmrtd.lds.icao.MRZInfo
import java.io.IOException


class CaptureActivity : AppCompatActivity(), TextRecognitionProcessor.ResultListener {

    private lateinit var binding: ActivityCaptureBinding
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var isSuccess = false

    private var docType = DocType.OTHER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(DOC_TYPE)) {
            docType = intent.getSerializableExtra(DOC_TYPE) as DocType
            if (docType == DocType.PASSPORT) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
        preview = binding.cameraSourcePreview
        if (preview == null) {
            Log.d("TAG", "Preview is null")
        }
        graphicOverlay = binding.graphicsOverlay
        if (graphicOverlay == null) {
            Log.d("TAG", "graphicOverlay is null")
        }
        createCameraSource()
        startCameraSource()
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume")
        startCameraSource()
    }

    /** Stops the camera.  */
    override fun onPause() {
        super.onPause()
        preview!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource!!.stop()
        }
    }

    private fun createCameraSource() {
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
        }
        cameraSource!!.setMachineLearningFrameProcessor(TextRecognitionProcessor(docType, this))
    }

    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d("TAG", "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d("TAG", "resume: graphOverlay is null")
                }
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e("TAG", "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    override fun onSuccess(mrzInfo: MRZInfo?) {
        if (!isSuccess) {
            isSuccess = true
            startActivity(Intent(this, NfcActivity::class.java).putExtra(MRZ_RESULT, mrzInfo))
        } else {
            finish()
        }
    }

    override fun onError(exp: Exception?) {
        setResult(RESULT_CANCELED)
        finish()
    }

}
