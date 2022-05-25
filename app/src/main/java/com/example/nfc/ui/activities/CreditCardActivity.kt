package com.example.nfc.ui.activities

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.nfc.R
import com.example.nfc.common.extension.showSnackBar
import com.example.nfc.databinding.ActivityCreditCardBinding
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils

class CreditCardActivity : AppCompatActivity(), CardNfcAsyncTask.CardNfcInterface {
    private lateinit var binding: ActivityCreditCardBinding
    private var nfcAdapter: NfcAdapter? = null
    private var mCardNfcAsyncTask: CardNfcAsyncTask? = null
    private var pendingIntent: PendingIntent? = null
    private var mCardNfcUtils: CardNfcUtils? = null
    private var mDoNotMoveCardMessage: String? = null
    private var mUnknownEmvCardMessage: String? = null
    private var mCardWithLockedNfcMessage: String? = null
    private var mIntentFromCreate = false
    private var mIsScanNow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNfc()
    }


    private fun setNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC bulunamadı.", Toast.LENGTH_SHORT).show()
        } else {
            mCardNfcUtils = CardNfcUtils(this)
            mIntentFromCreate = true
            initNfcMessages()
        }

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, CreditCardActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (nfcAdapter != null && nfcAdapter!!.isEnabled) {
            mCardNfcAsyncTask =
                CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                    .build()
        }
    }


    override fun startNfcReadCard() {
        mIsScanNow = true
        binding.progressbar.isVisible = true
    }

    override fun cardIsReadyToRead() {
        binding.constraintPlace.isVisible = false
        binding.cardContent.isVisible = true
        var card: String = mCardNfcAsyncTask!!.cardNumber
        card = getPrettyCardNumber(card)!!
        val cardType = mCardNfcAsyncTask!!.cardType
        binding.cardDate.text = mCardNfcAsyncTask!!.cardExpireDate
        binding.cardNumber.text = card
        parseCardType(cardType)
    }

    override fun doNotMoveCardSoFast() {
        showSnackBar(this, mDoNotMoveCardMessage.toString())
    }

    override fun unknownEmvCard() {
        showSnackBar(this, mUnknownEmvCardMessage.toString())
    }

    override fun cardWithLockedNfc() {
        showSnackBar(this, mCardWithLockedNfcMessage.toString())
    }

    override fun finishNfcReadCard() {
        binding.progressbar.isVisible = false
        mCardNfcAsyncTask = null
        mIsScanNow = false
    }


    private fun viewAlertDialog() {
        val alertDialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
        val alertDialog = Dialog(this)
        alertDialog.setContentView(alertDialogBinding)
        alertDialog.setCancelable(true)
        alertDialog.show()

        val buttonOk = alertDialogBinding.findViewById<Button>((R.id.ok))
        buttonOk.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun initNfcMessages() {
        mDoNotMoveCardMessage = getString(R.string.snack_doNotMoveCard)
        mCardWithLockedNfcMessage = getString(R.string.snack_lockedNfcCard)
        mUnknownEmvCardMessage = getString(R.string.snack_unknownEmv)
    }


    private fun parseCardType(cardType: String) {
        if (cardType == CardNfcAsyncTask.CARD_UNKNOWN) {
            showSnackBar(this, getString(R.string.snack_unknown_bank_card))
        } else if (cardType == CardNfcAsyncTask.CARD_VISA || cardType == CardNfcAsyncTask.CARD_NAB_VISA) {
            binding.cardIcon.setImageResource(R.drawable.visa_logo)
        } else if (cardType == CardNfcAsyncTask.CARD_MASTER_CARD) {
            binding.cardIcon.setImageResource(R.drawable.master_logo)
        }
    }

    private fun getPrettyCardNumber(card: String): String? {
        val div = " - "
        return (card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12)
                + div + card.substring(12, 16))
    }


    override fun onResume() {
        super.onResume()
        mIntentFromCreate = false
        if (nfcAdapter != null && !nfcAdapter!!.isEnabled) {
            viewAlertDialog()
        } else {
            mCardNfcUtils!!.enableDispatch()
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            mCardNfcUtils!!.disableDispatch()
        }
    }
}