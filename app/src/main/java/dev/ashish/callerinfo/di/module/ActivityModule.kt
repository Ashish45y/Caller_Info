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
        return AppInfoAdapter{ packageName ->
                   }
    }
}
