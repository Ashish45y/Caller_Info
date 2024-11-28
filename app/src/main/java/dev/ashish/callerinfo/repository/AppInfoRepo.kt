package dev.ashish.callerinfo.repository

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ashish.callerinfo.model.AppInfo
import dev.ashish.callerinfo.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoRepo @Inject constructor(
    @ApplicationContext private val context: Context,
     private val dispatcherProvider: DispatcherProvider
) {
    fun getAppsWithMicAccess(): Flow<List<AppInfo>> = flow {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appInfoList = apps.map { app ->
            val hasMicPermission = pm.checkPermission(
                Manifest.permission.RECORD_AUDIO,
                app.packageName
            ) == PackageManager.PERMISSION_GRANTED

            val isRunning = isAppRunningInBackground(app.packageName)

            AppInfo(
                name = app.loadLabel(pm).toString(),
                icon = app.loadIcon(pm),
                packageName = app.packageName,
                hasBackgroundAccess = hasMicPermission && isRunning
            )
        }

        emit(appInfoList)
    }.flowOn(dispatcherProvider.io)

    private fun isAppRunningInBackground(packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processes = activityManager.runningAppProcesses ?: return false
        return processes.any {
            it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
        }
    }
}
