package com.example.nfc.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.security.PublicKey

@Parcelize
data class EDocument(
    var docType: DocType,
    var personDetails: PersonDetails,
    var additionalPersonDetails: AdditionalPersonDetails,
    var docPublicKey: PublicKey? = null
) : Parcelable
