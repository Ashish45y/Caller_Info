package dev.ashish.callerinfo.repository

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ashish.callerinfo.model.AppInfo
import javax.inject.Singleton

@Singleton
class AppInfoPagingSource(@ApplicationContext private val context: Context) : PagingSource<Int, AppInfo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AppInfo> {
        val page = params.key ?: 1
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val startIndex = (page - 1) * params.loadSize
        val endIndex = (startIndex + params.loadSize).coerceAtMost(apps.size)
        val appInfoList = apps.subList(startIndex, endIndex).map { app ->
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
        return LoadResult.Page(
            data = appInfoList,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (endIndex == apps.size) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, AppInfo>): Int? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.packageName?.hashCode() }
    }

    private fun isAppRunningInBackground(packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processes = activityManager.runningAppProcesses ?: return false
        return processes.any {
            it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
        }
    }
}
