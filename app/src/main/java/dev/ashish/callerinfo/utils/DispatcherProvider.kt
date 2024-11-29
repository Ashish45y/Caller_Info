package dev.ashish.callerinfo.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


/**
 * Interface that provides different [CoroutineDispatcher]s to be used
 * for running coroutines in various contexts (e.g., main thread, IO, background tasks).
 *
 * The purpose of this interface is to decouple the dispatcher logic from other
 * components of the application and make it more testable by allowing easy
 * substitution of dispatchers for testing purposes.
 */

interface DispatcherProvider {

    /**
     * Provides the [CoroutineDispatcher] for the main thread. This is typically used
     * for tasks that interact with the UI (e.g., updating views or interacting with
     * UI components).
     */

    val main: CoroutineDispatcher

    /**
     * Provides the [CoroutineDispatcher] for IO-bound operations. This dispatcher is
     * intended for tasks such as network requests, file operations, or any operation
     * that is IO intensive.
     */

    val io: CoroutineDispatcher

    /**
     * Provides the [CoroutineDispatcher] for computationally intensive tasks.
     * This dispatcher is typically used for background tasks that require a significant
     * amount of CPU work and should not block the main thread.
     */

    val default: CoroutineDispatcher

}

/**
 * Default implementation of [DispatcherProvider] that returns the standard [Dispatchers]
 * provided by Kotlin Coroutines. This is used in production code where the default dispatchers
 * for main thread, IO, and default tasks are sufficient.
 */

class DefaultDispatcherProvider : DispatcherProvider {

    /**
     * The dispatcher used for UI-related tasks. This is the main thread and is used to interact
     * with UI components such as updating views or handling user input.
     */

    override val main: CoroutineDispatcher
        get() = Dispatchers.Main

    /**
     * The dispatcher used for IO-bound tasks such as network operations or file access.
     * It runs on a background thread optimized for IO.
     */

    override val io: CoroutineDispatcher
        get() = Dispatchers.IO

    /**
     * The dispatcher used for CPU-bound tasks. This is typically used for operations that
     * require significant processing power, such as complex computations or data manipulation.
     */
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default

}