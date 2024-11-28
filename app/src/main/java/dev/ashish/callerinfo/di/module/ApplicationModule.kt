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


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideLogger(): Logger = AppLogger()

}