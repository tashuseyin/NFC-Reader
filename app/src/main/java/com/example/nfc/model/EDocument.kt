package com.example.nfc.model

import java.security.PublicKey

data class EDocument(
    var docType: DocType,
    var personDetails: PersonDetails,
    var additionalPersonDetails: AdditionalPersonDetails,
    var docPublicKey: PublicKey
)
