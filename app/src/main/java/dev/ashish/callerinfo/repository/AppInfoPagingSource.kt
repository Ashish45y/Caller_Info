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

/**
 * A PagingSource implementation for loading a paginated list of installed applications on the device.
 *
 * This class fetches details about installed applications, such as their name, icon, package name,
 * and whether the app has background access and microphone permission.
 * It is used in conjunction with Paging3 to efficiently load and display application data in a paginated manner.
 *
 * @param context The application context, used to access system services and package manager.
 */
@Singleton
class AppInfoPagingSource(@ApplicationContext private val context: Context) :
    PagingSource<Int, AppInfo>() {
    /**
     * Loads a page of data. This method queries the package manager for a list of installed applications,
     * checks permissions, and determines whether each app is running in the background.
     *
     * @param params The parameters for loading data, including the page key and load size.
     * @return A [LoadResult.Page] containing a list of [AppInfo] objects and the keys for previous and next pages.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AppInfo> {
        val page = params.key ?: 1
        // Get the PackageManager to query installed applications
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        // Calculate the range of apps to load for the current page
        val startIndex = (page - 1) * params.loadSize
        val endIndex = (startIndex + params.loadSize).coerceAtMost(apps.size)
        // Create a list of AppInfo objects for the current page of apps
        val appInfoList = apps.subList(startIndex, endIndex).map { app ->
            val hasMicPermission = pm.checkPermission(
                Manifest.permission.RECORD_AUDIO,
                app.packageName
            ) == PackageManager.PERMISSION_GRANTED
            // Check if the app is running in the background
            val isRunning = isAppRunningInBackground(app.packageName)
            // Create an AppInfo object for the app
            AppInfo(
                name = app.loadLabel(pm).toString(),
                icon = app.loadIcon(pm),
                packageName = app.packageName,
                hasBackgroundAccess = hasMicPermission && isRunning
            )
        }
        // Return the result as a paginated LoadResult.Page
        return LoadResult.Page(
            data = appInfoList,
            prevKey = if (page == 1) null else page - 1,  // If it's the first page, no previous key
            nextKey = if (endIndex == apps.size) null else page + 1  // If it's the last page, no next key
        )
    }


    /**
     * Returns the key to use for refreshing the list.
     * This is typically used for reloading data based on the anchor position.
     *
     * @param state The current state of the PagingSource.
     * @return The refresh key, calculated based on the closest item to the current position.
     */

    override fun getRefreshKey(state: PagingState<Int, AppInfo>): Int? {
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.packageName?.hashCode() }
    }


    /**
     * Determines whether a given app is running in the background.
     *
     * @param packageName The package name of the app to check.
     * @return `true` if the app is running in the background, `false` otherwise.
     */

    private fun isAppRunningInBackground(packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processes = activityManager.runningAppProcesses ?: return false
        return processes.any {
            it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
        }
    }
}
