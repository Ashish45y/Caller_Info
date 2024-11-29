package dev.ashish.callerinfo.model

import android.graphics.drawable.Drawable

/**
 * A data class representing information about an application installed on the device.
 *
 * This class is used to encapsulate details about an app, such as its name, icon,
 * package name, and whether it has background access permissions.
 *
 * @property name The name of the application. Defaults to `null` if unavailable.
 * @property icon The application's icon represented as a [Drawable]. Defaults to `null` if unavailable.
 * @property packageName The unique package name of the application. Defaults to `null` if unavailable.
 * @property hasBackgroundAccess A flag indicating whether the app has permission for background activity. Defaults to `null` if unknown.
 */
data class AppInfo(
    val name: String? = null,
    val icon: Drawable? = null,
    val packageName: String? = null,
    val hasBackgroundAccess: Boolean? = null
)