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


/**
 * A BroadcastReceiver that listens for incoming calls and starts a service to handle call information.
 *
 * This receiver captures the incoming call number and its associated contact name (if available).
 * It then starts a service (`CallInfoService`) to process the call information.
 */

class CallReceiver : BroadcastReceiver() {

    /**
     * Called when a broadcast is received. In this case, it listens for incoming calls.
     *
     * @param context The application context.
     * @param intent The intent containing the broadcast data.
     */

    override fun onReceive(context: Context?, intent: Intent?) {
        // Retrieve the incoming phone number from the intent extras
        val number = intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER, "-1")
        // Proceed only if a valid phone number is retrieved
        if (number != null && context != null) {
            // Get the contact name associated with the incoming number
            val name = getContactName(context, number)
            // Create an intent to start the CallInfoService and pass the phone number and contact name
            val service = Intent(context, CallInfoService::class.java)

            service.putExtra("number", number)
            service.putExtra("name", name)
            // Start the service, using a foreground service for Android O and above, or a regular service otherwise

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service)
            } else {
                context.startService(service)
            }
        }
    }

    /**
     * Retrieves the contact name associated with a given phone number.
     *
     * This method queries the Contacts content provider to find the display name of the contact
     * matching the provided phone number.
     *
     * @param context The application context.
     * @param phoneNumber The phone number to look up.
     * @return The contact name if found, or "John Doe" if no match is found.
     */
    private fun getContactName(context: Context, phoneNumber: String): String {
        val contentResolver = context.contentResolver
        // Construct the URI for querying contacts with the given phone number
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        // The projection defines the columns to retrieve from the query, in this case, the contact name

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        // Default contact name if not found
        var contactName = "John doe"
        // Query the contacts content provider to find the matching contact name
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            // Move to the first result, if available
            if (it.moveToFirst()) {
                // Retrieve the contact name from the cursor
                contactName =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }
        // Return the contact name or the default value
        return contactName
    }
}