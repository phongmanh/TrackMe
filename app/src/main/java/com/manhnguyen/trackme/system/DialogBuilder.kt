package com.manhnguyen.trackme.system

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.manhnguyen.trackme.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogBuilder {

    companion object {
        fun alertDialog(
            context: Context,
            dialogView: View,
            showButton: Boolean = false,
            listener: DialogInterface.OnClickListener
        ): Dialog {
            return if (showButton)
                MaterialAlertDialogBuilder(context)
                    .setCustomTitle(null)
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.ok_label), listener)
                    .setNegativeButton(context.getString(R.string.cancel_label), listener)
                    .setView(dialogView)
                    .create()
            else
                MaterialAlertDialogBuilder(context)
                    .setCustomTitle(null)
                    .setCancelable(false)
                    .setView(dialogView).create()
        }

        fun alertDialog2(
            context: Context,
            dialogView: View
        ): Dialog {
            return MaterialAlertDialogBuilder(context)
                .setCustomTitle(null)
                .setCancelable(true)
                .setView(dialogView)
                .setView(dialogView).create()
        }

        fun alertProgressDialog(
            context: Context,
            dialogView: View
        ): Dialog {


            return MaterialAlertDialogBuilder(context)
                .setCustomTitle(null)
                .setCancelable(false)
                .setView(dialogView)
                /* .setBackground( ColorDrawable(Color.TRANSPARENT))*/
                .create()
        }

        fun alertYesDialog(
            context: Context,
            title: String,
            labelButton: String,
            message: String,
            listener: DialogInterface.OnClickListener
        ): Dialog {
            return MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(labelButton, listener).create()
        }

        fun alertErrorDialog(
            context: Context,
            title: String,
            message: String,
            listener: DialogInterface.OnClickListener
        ): Dialog {
            return MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", listener).create()
        }


        fun confirmYesNoDialog(
            context: Context,
            message: String,
            listener: DialogInterface.OnClickListener
        ) {
            MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", listener)
                .setNegativeButton("No", listener).show()
        }

    }


}