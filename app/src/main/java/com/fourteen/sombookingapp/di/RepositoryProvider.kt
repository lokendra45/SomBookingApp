package com.fourteen.sombookingapp.di

import com.fourteen.sombookingapp.data.api.BookingApiService
import com.fourteen.sombookingapp.data.mock.MockBookingApiService
import com.fourteen.sombookingapp.data.repository.BookingRepository
import com.fourteen.sombookingapp.data.repository.BookingRepositoryImpl

object RepositoryProvider {
    private val apiService: BookingApiService by lazy { MockBookingApiService() }

    val bookingRepository: BookingRepository by lazy {
        BookingRepositoryImpl(apiService)
    }
}
