package dev.ashish.callerinfo.model

import androidx.paging.PagingData


/**
 * A sealed class representing the various states of the UI when fetching application information.
 *
 * This class helps manage the different outcomes when fetching a list of applications,
 * such as loading, success, and error states. It is useful for representing the UI state
 * in a unidirectional data flow, typically within a ViewModel.
 */

sealed class AppInfoUiState {
    /**
     * Represents the loading state when the app information is being fetched.
     * This state is used to indicate that data is in the process of being loaded.
     */
    object Loading : AppInfoUiState()
    /**
     * Represents a successful state with a list of applications.
     *
     * @property apps A [PagingData] object that holds the paginated list of [AppInfo] objects.
     * This is typically used with Paging3 to display a large list of apps in a paginated manner.
     */
    data class Success(val apps: PagingData<AppInfo>) : AppInfoUiState()
    /**
     * Represents an error state when something goes wrong during data fetching.
     *
     * @property message A string message explaining the error.
     * This message could be used to show an error message to the user.
     */
    data class Error(val message: String) : AppInfoUiState()
}