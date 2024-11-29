package dev.ashish.callerinfo.repository


import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ashish.callerinfo.model.AppInfo
import dev.ashish.callerinfo.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


/**
 * A repository class responsible for providing a paginated list of applications with microphone access.
 *
 * This repository uses Paging3 to load data in pages and provides a flow of `PagingData<AppInfo>`,
 * which can be collected and displayed in the UI. The data is fetched through the `AppInfoPagingSource`
 * that queries the installed apps on the device and checks for microphone permission and background access.
 *
 * @param context The application context used to access system services, such as the package manager.
 * @param dispatcherProvider Provides the dispatchers for managing threading and ensuring background operations.
 */
@Singleton
class AppInfoRepo @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) {
    /**
     * Retrieves a flow of paginated data for apps that have microphone access.
     *
     * This function uses the `AppInfoPagingSource` to load data in pages. The flow emits `PagingData<AppInfo>`,
     * which contains a list of applications that have microphone permission and can be displayed in the UI.
     *
     * @return A flow of paginated `AppInfo` data, representing installed applications with microphone access.
     */
    fun getAppsWithMicAccess(): Flow<PagingData<AppInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // The number of items per page
                enablePlaceholders = false // Disables placeholders to prevent empty items when data is missing
            ),
            pagingSourceFactory = { AppInfoPagingSource(context) } // The factory that creates the paging source
        ).flow.flowOn(dispatcherProvider.io)   // Moves the flow's execution to the IO dispatcher for background work
    }
}