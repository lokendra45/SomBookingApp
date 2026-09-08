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
    // Fetches a list of services, optionally filtered by a search query
    fun getServices(query: String? = null): Flow<ApiResult<List<Service>>>
    
    // Fetches the full details of a specific service
    fun getServiceById(serviceId: String): Flow<ApiResult<Service>>
    
    // Fetches available time slots for a service on a specific date
    fun getAvailability(serviceId: String, date: String): Flow<ApiResult<List<TimeSlot>>>
    
    // Fetches the user's booking history
    fun getBookings(): Flow<ApiResult<List<Booking>>>
    
    // Submits a new booking request to the server
    suspend fun createBooking(request: BookingRequest): ApiResult<Booking>
}
