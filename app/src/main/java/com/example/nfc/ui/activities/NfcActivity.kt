package com.example.nfc.ui.activities


import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.nfc.R
import com.example.nfc.common.Constant
import com.example.nfc.databinding.ActivityNfcBinding
import com.example.nfc.model.Passport
import com.example.nfc.ui.fragment.NfcFragment
import com.example.nfc.ui.fragment.PassportDetailsFragment
import org.jmrtd.lds.icao.MRZInfo

class NfcActivity : FragmentActivity(), NfcFragment.NfcFragmentListener {
    private lateinit var binding: ActivityNfcBinding
    private var mrzInfo: MRZInfo? = null
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        if (intent.hasExtra(Constant.MRZ_RESULT)) {
            mrzInfo = intent.getSerializableExtra(Constant.MRZ_RESULT) as MRZInfo
        } else {
            onBackPressed()
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC bulunamadı.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, this.javaClass)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )


        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NfcFragment.newInstance(mrzInfo!!), Constant.TAG_NFC)
                .commit()
        }
    }

    public override fun onNewIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            // drop NFC events
            handleIntent(intent)
        } else {
            super.onNewIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        val fragmentByTag = supportFragmentManager.findFragmentByTag(Constant.TAG_NFC)
        if (fragmentByTag is NfcFragment) {
            fragmentByTag.handleNfcTag(intent)
        }
    }


    /////////////////////////////////////////////////////
    //
    //  NFC Fragment events
    //
    /////////////////////////////////////////////////////

    override fun onEnableNfc() {
        if (nfcAdapter != null) {
            if (!nfcAdapter!!.isEnabled)
                showWirelessSettings()

            nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onDisableNfc() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onPassportRead(passport: Passport?) {
        showFragmentDetails(passport!!)
    }

    override fun onCardException(cardException: Exception?) {
        cardException?.printStackTrace()
        //Toast.makeText(this, cardException.toString(), Toast.LENGTH_SHORT).show();
        //onBackPressed();
    }

    private fun showWirelessSettings() {
        Toast.makeText(this, "NFC aktif edilmelidir.", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        startActivity(intent)
    }


    private fun showFragmentDetails(passport: Passport) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PassportDetailsFragment.newInstance(passport))
            .addToBackStack(Constant.TAG_PASSPORT_DETAILS)
            .commit()
    }
}
