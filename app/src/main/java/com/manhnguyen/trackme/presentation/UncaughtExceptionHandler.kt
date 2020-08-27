package com.manhnguyen.trackme.presentation

import android.content.Context

class UncaughtExceptionHandler constructor(val context: Context) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        /*DialogBuilder.alertErrorDialog(
            context,
            "UncaughtException",
            ExceptionUtils.getMessage(e),
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }).show()*/
    }
}