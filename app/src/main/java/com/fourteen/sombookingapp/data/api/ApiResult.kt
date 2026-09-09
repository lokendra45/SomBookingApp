package com.fourteen.sombookingapp.data.api

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val message: String,
        val type: ApiErrorType = ApiErrorType.GENERIC
    ) : ApiResult<Nothing>()
}

enum class ApiErrorType {
    GENERIC,        // 500-style unexpected server error
    NOT_FOUND,      // 404
    VALIDATION,     // 422-style field validation error
    CONFLICT        // 409 - e.g. slot already booked
}


