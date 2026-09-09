package com.fourteen.sombookingapp.data.api

import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.model.TimeSlot

interface BookingApiService {
    suspend fun getServices(query: String? = null): ApiResult<List<Service>>
    suspend fun getServiceById(serviceId: String): ApiResult<Service>
    suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<TimeSlot>>
    suspend fun createBooking(request: BookingRequest): ApiResult<Booking>
    suspend fun getBookings(): ApiResult<List<Booking>>
}
