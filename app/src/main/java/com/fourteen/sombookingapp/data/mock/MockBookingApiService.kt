package com.fourteen.sombookingapp.data.mock

import com.fourteen.sombookingapp.data.api.ApiErrorType
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.api.BookingApiService
import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.BookingStatus
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.model.TimeSlot
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

/**
 * Mock API service that uses fake data in memory for testing offline.
 */
class MockBookingApiService : BookingApiService {

    private val bookings = mutableListOf<Booking>()
    private val bookedSlotIds = mutableSetOf<String>()

    // Simulates fetching a list of services with optional search filtering
    override suspend fun getServices(query: String?): ApiResult<List<Service>> {
        simulateLatency()

        if (query.equals("error", ignoreCase = true)) return ApiResult.Error("Unable to load services right now. Please try again.", ApiErrorType.GENERIC)
        if (query.equals("empty", ignoreCase = true)) return ApiResult.Success(emptyList())

        val results = if (query.isNullOrBlank()) mockServices else {
            mockServices.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.provider.contains(query, ignoreCase = true)
            }
        }
        return ApiResult.Success(results)
    }

    // Simulates fetching a single service by ID, including a hardcoded error case
    override suspend fun getServiceById(serviceId: String): ApiResult<Service> {
        simulateLatency()
        if (serviceId == "svc-error") return ApiResult.Error("Failed to load service details.", ApiErrorType.GENERIC)
        
        val service = mockServices.find { it.id == serviceId } ?: return ApiResult.Error("Service not found.", ApiErrorType.NOT_FOUND)
        return ApiResult.Success(service)
    }

    override suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<TimeSlot>> {
        simulateLatency()

        if (!mockServices.any { it.id == serviceId }) return ApiResult.Error("Service not found.", ApiErrorType.NOT_FOUND)

        val times = listOf("09:00", "10:30", "12:00", "14:00", "15:30", "17:00")

        val slots = times.map { time ->
            val slotId = "$serviceId-$date-$time"
            TimeSlot(id = slotId, date = date, time = time, available = !bookedSlotIds.contains(slotId))
        }

        return ApiResult.Success(slots)
    }

    override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> {
        simulateLatency()

        validate(request)?.let { return ApiResult.Error(it, ApiErrorType.VALIDATION) }
        if (request.serviceId == "svc-error") return ApiResult.Error("Booking failed due to a server error.", ApiErrorType.GENERIC)
        if (bookedSlotIds.contains(request.slotId)) {
            return ApiResult.Error("This time slot was just booked by someone else. Please choose another slot.", ApiErrorType.CONFLICT)
        }

        val service = mockServices.find { it.id == request.serviceId } ?: return ApiResult.Error("Service not found.", ApiErrorType.NOT_FOUND)

        bookedSlotIds.add(request.slotId)
        
        val newBooking = Booking(
            id = UUID.randomUUID().toString(),
            bookingNumber = "SOM-${(1000..9999).random()}",
            serviceId = service.id,
            serviceName = service.name,
            provider = service.provider,
            date = request.date,
            time = request.time,
            status = BookingStatus.CONFIRMED,
            customerName = request.customerName,
            contact = request.contact
        )
        
        bookings.add(0, newBooking)
        return ApiResult.Success(newBooking)
    }

    // Returns the local in-memory list of created bookings
    override suspend fun getBookings(): ApiResult<List<Booking>> {
        simulateLatency()
        return ApiResult.Success(bookings.toList())
    }

    // Validates required form fields for the mock request
    private fun validate(request: BookingRequest): String? {
        return when {
            request.customerName.isBlank() -> "Please enter your name."
            request.customerName.trim().length < 2 -> "Name must be at least 2 characters."
            request.contact.isBlank() -> "Please enter a phone number or email."
            request.slotId.isBlank() -> "Please select a time slot."
            else -> null
        }
    }

    // Adds artificial delay to simulate real network conditions
    private suspend fun simulateLatency() = delay((300L..900L).random().milliseconds)

}
