package dev.ashish.callerinfo.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ashish.callerinfo.model.AppInfoUiState
import dev.ashish.callerinfo.repository.AppInfoRepo
import dev.ashish.callerinfo.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that provides app information and manages UI state for the app list.
 *
 * This ViewModel is responsible for fetching a list of apps that have microphone
 * access and updating the UI state based on the result of that operation. It uses
 * a repository to retrieve the app data and manages the UI state with the help of
 * a StateFlow.
 *
 * @property repository The repository responsible for fetching app data.
 * @property dispatcherProvider Provides the necessary dispatchers for different coroutine contexts.
 */

@HiltViewModel
class AppInfoViewModel @Inject constructor(
    private val repository: AppInfoRepo,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    /**
     * Private mutable StateFlow that holds the current state of the UI.
     * The UI state can represent loading, success with app data, or an error message.
     */

    private val _uiState = MutableStateFlow<AppInfoUiState>(AppInfoUiState.Loading)

    /**
     * Publicly exposed immutable StateFlow for observing UI state changes.
     * This is used by the UI layer (e.g., Activity/Fragment) to update the UI.
     */
    val uiState: StateFlow<AppInfoUiState> = _uiState

    /**
     * Fetches the list of apps with microphone access and updates the UI state.
     *
     * The function sets the state to `Loading` at the start, attempts to fetch the
     * apps from the repository, and updates the state accordingly based on the result:
     * - If successful, it updates the state with the list of apps (`Success`).
     * - If an error occurs, it updates the state with an error message (`Error`).
     */

    fun fetchApps() {
        // Launch a coroutine in the ViewModel's scope
        viewModelScope.launch {
            // Set the UI state to Loading before attempting to fetch data

            _uiState.value = AppInfoUiState.Loading

            try {
                // Fetch apps with microphone access from the repository

                repository.getAppsWithMicAccess()
                    .cachedIn(viewModelScope)  // Cache the result in the ViewModel's scope
                    .collect { apps ->
                        // On success, update the state with the retrieved app data
                        _uiState.value = AppInfoUiState.Success(apps)
                    }
            } catch (e: Exception) {
                // On error, update the state with an error message

                _uiState.value = AppInfoUiState.Error("Failed to fetch apps")
            }
        }
    }
}

