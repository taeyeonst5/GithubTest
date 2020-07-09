package com.allen_chou.githubtest.util

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * 可防止返回上一個Fragment時或是alert重複觸發 Observer onChanged()
 */
open class EventWrapper<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}