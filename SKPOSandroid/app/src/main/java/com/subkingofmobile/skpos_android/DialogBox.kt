package com.subkingofmobile.skpos_android

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class DialogBox(currentContext : Context) {
    private val appContext : Context

    init {
        appContext = currentContext
    }

    fun showSingleOptionDialogBox(DialogTitle : String, DialogMessage : String, onClickOK : () -> Unit = {}) {
        AlertDialog.Builder(appContext).setTitle(DialogTitle).setMessage(DialogMessage).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> dialog.cancel()}).show()
    }
}