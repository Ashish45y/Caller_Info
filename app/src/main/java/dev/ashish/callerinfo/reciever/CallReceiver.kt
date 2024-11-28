package dev.ashish.callerinfo.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import dev.ashish.callerinfo.service.CallInfoService

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val number = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER, "-1")
        if (number != null && context != null) {
            val name = getContactName(context, number)
            val service = Intent(context, CallInfoService::class.java)
            service.putExtra("number", number)
            service.putExtra("name", name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service)
            } else {
                context.startService(service)
            }
        }
    }

    private fun getContactName(context: Context, phoneNumber: String): String {
        val contentResolver = context.contentResolver
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName = "John doe"
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                contactName = it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }
        return contactName
    }
}