package dev.ashish.callerinfo.utils.logger

import android.util.Log

class AppLogger : Logger {
    // Implementation of the 'd' method from Logger interface
    override fun d(tag: String, msg: String) {
        // Log the message using Android's Log.d method
        Log.d(tag, msg)
    }
}