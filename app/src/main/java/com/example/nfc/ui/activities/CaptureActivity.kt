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
    private lateinit var preview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay
    private var docType: DocType = DocType.OTHER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkTypeControl()

        preview = binding.cameraSourcePreview
        graphicOverlay = binding.graphicsOverlay

        createCameraSource()
        startCameraSource()
    }

    private fun checkTypeControl() {
        if (intent.hasExtra(DOC_TYPE)) {
            docType = intent.getSerializableExtra(DOC_TYPE) as DocType
            if (docType == DocType.PASSPORT) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }


    private fun createCameraSource() {
        if (cameraSource == null){
            cameraSource = CameraSource(this, graphicOverlay)
            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
        }
        cameraSource!!.setMachineLearningFrameProcessor(TextRecognitionProcessor(docType, this))
    }

    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e("TAG", "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    override fun onSuccess(mrzInfo: MRZInfo?) {
        val returnIntent = Intent()
        returnIntent.putExtra(MRZ_RESULT, mrzInfo)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onError(exp: Exception?) {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource!!.release()
        }
    }


}