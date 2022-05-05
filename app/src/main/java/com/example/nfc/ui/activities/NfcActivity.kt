package com.example.nfc.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc.R
import com.example.nfc.common.Constant

class NfcActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        val data = intent.getSerializableExtra(Constant.MRZ_RESULT)
        print(data)
    }
}
