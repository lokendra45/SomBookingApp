package com.fourteen.sombookingapp.data.repository

import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.model.TimeSlot
import kotlinx.coroutines.flow.Flow

/**
 * Interface for loading services and managing bookings.
 */
interface BookingRepository {
    fun getServices(query: String? = null): Flow<ApiResult<List<Service>>>
    fun getServiceById(serviceId: String): Flow<ApiResult<Service>>
    fun getAvailability(serviceId: String, date: String): Flow<ApiResult<List<TimeSlot>>>
    fun getBookings(): Flow<ApiResult<List<Booking>>>
    suspend fun createBooking(request: BookingRequest): ApiResult<Booking>
}
