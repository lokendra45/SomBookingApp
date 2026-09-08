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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

/**
 * Mock API service that uses fake data in memory for testing offline.
 */
class MockBookingApiService : BookingApiService {

    private val mutex = Mutex()
    private val bookings = mutableListOf<Booking>()
    private val bookedSlotIds = mutableSetOf<String>()

    private val services = listOf(
        Service(
            id = "svc-1",
            name = "Standard Home Cleaning",
            category = "Cleaning",
            provider = "SparkleWorks",
            price = 2500.0,
            currency = "NPR",
            durationMinutes = 90,
            rating = 4.7,
            description = "A thorough clean of kitchen, bathrooms, and living areas by a vetted professional."
        ),
        Service(
            id = "svc-2",
            name = "Emergency Plumbing Repair",
            category = "Plumbing",
            provider = "FlowFix Co.",
            price = 4500.0,
            currency = "NPR",
            durationMinutes = 60,
            rating = 4.5,
            description = "Fast-response repair for leaks, clogs, and fixture installation."
        ),
        Service(
            id = "svc-3",
            name = "AC Maintenance & Tune-up",
            category = "HVAC",
            provider = "CoolBreeze Techs",
            price = 3500.0,
            currency = "NPR",
            durationMinutes = 75,
            rating = 4.8,
            description = "Filter replacement, coil cleaning, and performance check for home AC units."
        ),
        Service(
            id = "svc-4",
            name = "1-on-1 Math Tutoring",
            category = "Education",
            provider = "BrightPath Tutors",
            price = 2000.0,
            currency = "NPR",
            durationMinutes = 60,
            rating = 4.9,
            description = "Personalized tutoring session for middle and high school math."
        ),
        Service(
            id = "svc-5",
            name = "At-Home Hair Styling",
            category = "Beauty",
            provider = "Glow Studio",
            price = 3000.0,
            currency = "NPR",
            durationMinutes = 50,
            rating = 4.6,
            description = "Wash, cut, and style in the comfort of your home."
        )
    )

    override suspend fun getServices(query: String?): ApiResult<List<Service>> {
        simulateLatency()

        if (query.equals("error", ignoreCase = true)) {
            return ApiResult.Error("Unable to load services right now. Please try again.", ApiErrorType.GENERIC)
        }
        if (query.equals("empty", ignoreCase = true)) {
            return ApiResult.Success(emptyList())
        }

        val filtered = if (query.isNullOrBlank()) {
            services
        } else {
            services.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.provider.contains(query, ignoreCase = true)
            }
        }
        return ApiResult.Success(filtered)
    }

    override suspend fun getServiceById(serviceId: String): ApiResult<Service> {
        simulateLatency()

        if (serviceId == "svc-error") {
            return ApiResult.Error("Failed to load service details.", ApiErrorType.GENERIC)
        }
        val service = services.find { it.id == serviceId }
            ?: return ApiResult.Error("Service not found.", ApiErrorType.NOT_FOUND)
        return ApiResult.Success(service)
    }

    override suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<TimeSlot>> {
        simulateLatency()

        if (serviceId == "svc-error") {
            return ApiResult.Error("Failed to load availability.", ApiErrorType.GENERIC)
        }
        if (services.none { it.id == serviceId }) {
            return ApiResult.Error("Service not found.", ApiErrorType.NOT_FOUND)
        }

        val baseTimes = listOf("09:00", "10:30", "12:00", "14:00", "15:30", "17:00")
        val slots = mutex.withLock {
            baseTimes.map { time ->
                val slotId = "$serviceId-$date-$time"
                val isDemoConflictSlot = serviceId == "svc-2" && time == "09:00"
                val alreadyBooked = bookedSlotIds.contains(slotId)
                TimeSlot(
                    id = if (isDemoConflictSlot) "$slotId-conflict" else slotId,
                    date = date,
                    time = time,
                    available = !alreadyBooked
                )
            }
        }
        return ApiResult.Success(slots)
    }

    override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> {
        simulateLatency()

        val validationError = validate(request)
        if (validationError != null) {
            return ApiResult.Error(validationError, ApiErrorType.VALIDATION)
        }

        if (request.serviceId == "svc-error") {
            return ApiResult.Error("Booking failed due to a server error.", ApiErrorType.GENERIC)
        }

        return mutex.withLock {
            val isConflictSlot = request.slotId.endsWith("-conflict") ||
                bookedSlotIds.contains(request.slotId)
            if (isConflictSlot) {
                bookedSlotIds.add(request.slotId)
                return@withLock ApiResult.Error(
                    "This time slot was just booked by someone else. Please choose another slot.",
                    ApiErrorType.CONFLICT
                )
            }

            val service = services.find { it.id == request.serviceId }
                ?: return@withLock ApiResult.Error("Service not found.", ApiErrorType.NOT_FOUND)

            bookedSlotIds.add(request.slotId)
            val booking = Booking(
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
            bookings.add(0, booking)
            ApiResult.Success(booking)
        }
    }

    override suspend fun getBookings(): ApiResult<List<Booking>> {
        simulateLatency()
        return mutex.withLock { ApiResult.Success(bookings.toList()) }
    }

    private fun validate(request: BookingRequest): String? {
        return when {
            request.customerName.isBlank() -> "Please enter your name."
            request.customerName.trim().length < 2 -> "Name must be at least 2 characters."
            request.contact.isBlank() -> "Please enter a phone number or email."
            request.slotId.isBlank() -> "Please select a time slot."
            else -> null
        }
    }

    private suspend fun simulateLatency() {
        delay((300L..900L).random().milliseconds)
    }
}
