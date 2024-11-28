package dev.ashish.callerinfo.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.ashish.callerinfo.ui.adpater.AppInfoAdapter

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
    @Provides
    fun provideAppInfoAdapter(): AppInfoAdapter {
        // Provide an empty lambda or a default action for the click handler
        return AppInfoAdapter(ArrayList()) { packageName ->
            // Default action: Log or handle the click event (can be overridden later)
            // Example: Log.d("AppInfoAdapter", "Clicked on app with package: $packageName")
        }
    }
}
