package com.example.nfc.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.nfc.R
import com.example.nfc.util.NfcIntentHandler

class NfcActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onNewIntent(intent: Intent) {
        (navHostFragment.childFragmentManager.fragments[0] as? NfcIntentHandler)?.handlerNfcIntent(
            intent
        )
        super.onNewIntent(intent)
    }
}
