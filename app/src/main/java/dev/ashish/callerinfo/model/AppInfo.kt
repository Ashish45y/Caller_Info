package dev.ashish.callerinfo.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String? = null,
    val icon: Drawable? = null,
    val packageName: String? = null,
    val hasBackgroundAccess: Boolean? = null
)