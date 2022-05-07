package com.example.nfc.util

import android.content.Intent

interface NfcIntentHandler {
    fun handlerNfcIntent(intent: Intent)
}