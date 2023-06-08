package com.example.realestate.data.remote.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val smsMessage = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)

            Log.d(TAG, "onReceive SMS= $smsMessage")

            // Extract the verification code from the SMS message
            val verificationCode = extractVerificationCode(smsMessage)

            // Pass the verification code to your verification process
            processVerificationCode(verificationCode)
        }
    }

    private fun extractVerificationCode(smsMessage: String?): String? {
        // Extract the verification code using regular expressions or any other method based on your SMS format
        return ""
    }

    private fun processVerificationCode(verificationCode: String?) {
        // Perform the necessary verification logic with the received code
    }
}