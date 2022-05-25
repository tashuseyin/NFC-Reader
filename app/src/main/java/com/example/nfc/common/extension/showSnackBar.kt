package com.example.nfc.common.extension

import android.app.Activity
import com.google.android.material.snackbar.Snackbar

fun showSnackBar(activity: Activity, message: String) {
    val snackBar =
        Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
    snackBar.show()
}