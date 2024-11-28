package dev.ashish.callerinfo.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ashish.callerinfo.model.AppInfoUiState
import dev.ashish.callerinfo.repository.AppInfoRepo
import dev.ashish.callerinfo.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AppInfoViewModel @Inject constructor(
    private val repository: AppInfoRepo,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppInfoUiState>(AppInfoUiState.Loading)
    val uiState: StateFlow<AppInfoUiState> = _uiState

    fun fetchApps() {
        viewModelScope.launch {
            _uiState.value = AppInfoUiState.Loading
            try {
                repository.getAppsWithMicAccess()
                    .flowOn(dispatcherProvider.io)
                    .collect { apps ->
                        _uiState.value = AppInfoUiState.Success(apps)
                    }
            } catch (e: Exception) {
                _uiState.value = AppInfoUiState.Error("Failed to fetch apps")
            }
        }
    }
}

