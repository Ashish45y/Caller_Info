package dev.ashish.callerinfo.utils.logger


/**
 * Interface for logging messages with various log levels.
 *
 * This interface defines the structure for a logger implementation that can be
 * used to log debug-level messages with a tag for categorization.
 *
 * You can implement this interface to create custom logging mechanisms,
 * or use it as an abstraction for existing logging libraries.
 */
interface Logger {
    /**
     * Logs a debug-level message with a specified tag.
     *
     * @param tag A string used to categorize the log message (e.g., a class name, module name, etc.).
     * @param msg The message to be logged. It could be anything from simple strings to complex objects.
     */
    fun d(tag: String, msg: String)
}