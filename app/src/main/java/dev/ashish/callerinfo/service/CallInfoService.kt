package dev.ashish.callerinfo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.ashish.callerinfo.R
import dev.ashish.callerinfo.databinding.CallInfoViewBinding
import dev.ashish.callerinfo.utils.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * A service class that runs in the foreground to display a floating window showing information about an incoming call.
 *
 * The service listens for incoming calls, retrieves the caller's name and number, and displays them in a floating
 * window above other apps. The window can be closed via a cancel button. The service is started as a foreground
 * service to ensure it remains active during the call.
 *
 * @param dispatcherProvider Provides dispatchers for managing background threading operations.
 */
@AndroidEntryPoint
class CallInfoService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    private lateinit var binding: CallInfoViewBinding
    private var isViewAdded = false

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    /**
     * Called when the service is created. Initializes the `WindowManager` and the floating window,
     * starts the service in the foreground, and sets up the cancel button.
     *
     * The method also configures the layout parameters for the floating window and attaches it to the window manager.
     */

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        // initializing the binding here with layout params.
        binding = CallInfoViewBinding.inflate(LayoutInflater.from(this))
        windowManager.addView(binding.root, layoutParams)
        isViewAdded = true
        lifecycleScope.launch(dispatcherProvider.main) {
            binding.cancelButton.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    windowManager.removeView(binding.root)
                    isViewAdded = false
                    stopForeground(true)
                }else{
                    windowManager.removeView(binding.root)
                    isViewAdded = false
                    stopSelf()
                }
            }
        }
    }


    /**
     * Called when the service is started. Retrieves the incoming call number and name, and displays them in the floating window.
     *
     * @param intent The intent containing the incoming call data, such as the caller's number and name.
     * @param flags Additional flags for the service.
     * @param startId The ID to track the service start request.
     * @return The start command result, which ensures the service continues running.
     */

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        lifecycleScope.launch(dispatcherProvider.io) {
            val number = intent?.getStringExtra("number")
            val name = intent?.getStringExtra("name")
            withContext(dispatcherProvider.main) {
                binding.name.text = name
                binding.number.text = number
            }
        }
        return START_STICKY
    }


    /**
     * Called when the service is destroyed. Removes the floating window if it was added and stops the service.
     */

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch(dispatcherProvider.main) {
            if (isViewAdded) {
                windowManager.removeView(binding.root)
                isViewAdded = false
            }
        }
    }


    /**
     * Creates a notification to display while the service is running in the foreground.
     *
     * @return A notification object that informs the user that the service is active.
     */

    private fun createNotification(): Notification {
        val channelId = "call_info_service_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Call Info Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call Info Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.fontisto_close) // Replace with your app's icon
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
