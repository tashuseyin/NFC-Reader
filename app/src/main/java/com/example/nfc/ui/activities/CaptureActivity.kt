package com.example.nfc.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc.R
import com.example.nfc.common.Constant.Companion.DOC_TYPE
import com.example.nfc.common.Constant.Companion.MRZ_RESULT
import com.example.nfc.mlkit.camera.CameraSource
import com.example.nfc.mlkit.camera.CameraSourcePreview
import com.example.nfc.mlkit.other.GraphicOverlay
import com.example.nfc.mlkit.text.TextRecognitionProcessor
import com.example.nfc.model.DocType
import org.jmrtd.lds.icao.MRZInfo
import java.io.IOException


class CaptureActivity : AppCompatActivity(), TextRecognitionProcessor.ResultListener {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null


    private var docType = DocType.OTHER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)
        if (intent.hasExtra(DOC_TYPE)) {
            docType = intent.getSerializableExtra(DOC_TYPE) as DocType
            if (docType == DocType.PASSPORT) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
        preview = findViewById(R.id.camera_source_preview)
        if (preview == null) {
            Log.d("TAG", "Preview is null")
        }
        graphicOverlay = findViewById(R.id.graphics_overlay)
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

    override fun onPause() {
        super.onPause()
        preview!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource!!.stop()
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
        val returnIntent = Intent()
        returnIntent.putExtra(MRZ_RESULT, mrzInfo)
        setResult(Activity.RESULT_OK, returnIntent)
        startActivity(Intent(this,ResultActivity::class.java))
        finish()
    }

    override fun onError(exp: Exception?) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
