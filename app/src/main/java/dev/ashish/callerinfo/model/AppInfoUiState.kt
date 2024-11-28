package dev.ashish.callerinfo.model

sealed class AppInfoUiState {
    object Loading : AppInfoUiState()
    data class Success(val apps: List<AppInfo>) : AppInfoUiState()
    data class Error(val message: String) : AppInfoUiState()
}