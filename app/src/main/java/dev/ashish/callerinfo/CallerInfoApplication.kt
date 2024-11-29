package dev.ashish.callerinfo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * The main application class for the CallerInfo application.
 *
 * This class is annotated with [@HiltAndroidApp], which triggers Hilt's code generation
 * and allows Hilt to provide dependency injection (DI) for the entire app. By extending
 * [Application], this class is created when the application starts and can be used to
 * perform any necessary global setup, including initializing libraries or managing app-wide resources.
 *
 * The [CallerInfoApplication] class acts as the entry point for Hilt's DI system. Hilt will
 * automatically generate the necessary components and inject dependencies into any class
 * that is annotated with [@Inject] within the app.
 */

@HiltAndroidApp
class CallerInfoApplication: Application()