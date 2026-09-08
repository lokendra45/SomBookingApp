package com.fourteen.sombookingapp.data.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import java.util.concurrent.TimeoutException

/**
 * Maps raw Exceptions to user-friendly UI strings based on the error type.
 */
fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is IOException -> "No internet connection. Please check your network and try again."
        is TimeoutException -> "The connection timed out. Please try again."
        else -> "An unexpected error occurred. Please try again."
    }
}

/**
 * Extension for Repository Flows to automatically catch exceptions and emit them safely.
 */
fun <T> Flow<ApiResult<T>>.catchApiError(): Flow<ApiResult<T>> = this.catch { e ->
    emit(ApiResult.Error(e.toUserFriendlyMessage(), ApiErrorType.GENERIC))
}

/**
 * Wrapper for safe suspend API calls (non-Flow) in the Repository.
 */
inline fun <T> safeApiCall(apiCall: () -> ApiResult<T>): ApiResult<T> {
    return try {
        apiCall()
    } catch (e: Exception) {
        ApiResult.Error(e.toUserFriendlyMessage(), ApiErrorType.GENERIC)
    }
}
