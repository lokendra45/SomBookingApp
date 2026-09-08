package com.fourteen.sombookingapp.data.api

import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.model.TimeSlot

/**
 * API service for fetching services, time slots, and creating bookings.
 */
interface BookingApiService {

    /** GET /api/v1/services — list/search services. */
    suspend fun getServices(query: String? = null): ApiResult<List<Service>>

    /** GET /api/v1/services/{service_id} — service details. */
    suspend fun getServiceById(serviceId: String): ApiResult<Service>

    /** GET /api/v1/services/{service_id}/availability?date= — slots for a date. */
    suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<TimeSlot>>

    /** POST /api/v1/bookings — create a booking. */
    suspend fun createBooking(request: BookingRequest): ApiResult<Booking>

    /** GET /api/v1/bookings — list the customer's bookings. */
    suspend fun getBookings(): ApiResult<List<Booking>>
}
