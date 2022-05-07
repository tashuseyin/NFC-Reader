package com.example.nfc.util

import android.graphics.Bitmap

object Image {

    /** Image **/
    private var bitmapImage: Bitmap? = null
    private var base64Image: String? = null

    fun getBitmapImage(): Bitmap? {
        return bitmapImage
    }

    fun setBitmapImage(bitmapImage: Bitmap?) {
        this.bitmapImage = bitmapImage
    }

    fun getBase64Image(): String? {
        return base64Image
    }

    fun setBase64Image(base64Image: String?) {
        this.base64Image = base64Image
    }

}