package dev.ashish.callerinfo.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.ashish.callerinfo.ui.adpater.AppInfoAdapter


/**
 * A Dagger module for providing dependencies specific to an Android Activity.
 *
 * This module is installed in the `ActivityComponent`, meaning the provided dependencies
 * are scoped to the lifecycle of an Activity and will be available for injection
 * only within Activities.
 */
@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
    /**
     * Provides an instance of [AppInfoAdapter].
     *
     * The [AppInfoAdapter] is initialized with a lambda function that takes a
     * `packageName` as input and performs the desired action. The implementation
     * of this lambda can be defined by the calling code when the adapter is utilized.
     *
     * @return A new instance of [AppInfoAdapter].
     */
    @Provides
    fun provideAppInfoAdapter(): AppInfoAdapter {
        return AppInfoAdapter{ packageName ->
                   }
    }
}
