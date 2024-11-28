package dev.ashish.callerinfo.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import dev.ashish.callerinfo.R
import dev.ashish.callerinfo.databinding.CallInfoViewBinding

@AndroidEntryPoint
class CallInfoService : Service(){
    private lateinit var windowManager: WindowManager
    private lateinit var binding: CallInfoViewBinding

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
       binding = CallInfoViewBinding.inflate(LayoutInflater.from(this))
        windowManager.addView(binding.root, layoutParams)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       intent.let {
           val number = it?.getStringExtra("number")
           val name = it?.getStringExtra("name")
           binding.apply {
               cancelButton.setOnClickListener {
                   stopSelf()
               }
               binding.name.text = name
               binding.number.text = number
           }
       }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(binding.root)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}