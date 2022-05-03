package com.example.nfc.model

import android.graphics.Bitmap

data class PersonDetails(
    var name: String,
    var surname: String,
    var personalNumber: String,
    var gender:String,
    var birthDate: String,
    var expiryDate: String,
    var serialNumber: String,
    var nationality: String,
    var faceImage: Bitmap,
    var faceImageBase64: String,
    var portraitImage: Bitmap,
    var portraitImageBase64: String,
    var signature: Bitmap,
    var signatureBase64: String,
    var fingerprints: List<Bitmap>
)
