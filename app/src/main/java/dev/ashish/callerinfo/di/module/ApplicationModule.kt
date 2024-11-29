package dev.ashish.callerinfo.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ashish.callerinfo.utils.DefaultDispatcherProvider
import dev.ashish.callerinfo.utils.DispatcherProvider
import dev.ashish.callerinfo.utils.logger.AppLogger
import dev.ashish.callerinfo.utils.logger.Logger
import javax.inject.Singleton


/**
 * A Dagger module that provides application-wide dependencies.
 *
 * This module is installed in the `SingletonComponent`, meaning the provided
 * dependencies are available throughout the entire application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    /**
     * Provides an instance of [DispatcherProvider].
     *
     * The [DispatcherProvider] is responsible for managing coroutines' dispatchers,
     * such as `Dispatchers.IO`, `Dispatchers.Main`, etc., in a centralized and testable way.
     * The implementation used here is [DefaultDispatcherProvider], which provides default
     * dispatcher implementations.
     *
     * @return An instance of [DispatcherProvider] that can be injected wherever needed.
     */
    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()

    /**
     * Provides an instance of [Logger].
     *
     * The [Logger] is used for logging application events, errors, and debug information.
     * The implementation used here is [AppLogger], which encapsulates the logging functionality.
     *
     * @return An instance of [Logger] that can be injected wherever needed.
     */
    @Provides
    @Singleton
    fun provideLogger(): Logger = AppLogger()

}