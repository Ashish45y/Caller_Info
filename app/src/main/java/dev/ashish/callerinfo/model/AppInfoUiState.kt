package dev.ashish.callerinfo.model

import androidx.paging.PagingData

sealed class AppInfoUiState {
    object Loading : AppInfoUiState()
    data class Success(val apps: PagingData<AppInfo>) : AppInfoUiState()
    data class Error(val message: String) : AppInfoUiState()
}