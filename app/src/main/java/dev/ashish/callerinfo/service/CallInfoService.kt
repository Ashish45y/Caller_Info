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

@AndroidEntryPoint
class CallInfoService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    private lateinit var binding: CallInfoViewBinding
    private var isViewAdded = false

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

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

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch(dispatcherProvider.main) {
            if (isViewAdded) {
                windowManager.removeView(binding.root)
                isViewAdded = false
            }
        }
    }

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
