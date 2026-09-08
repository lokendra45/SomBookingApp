package com.fourteen.sombookingapp.data.model

/**
 * Data models for services, time slots, and bookings.
 */

data class Service(
    val id: String,
    val name: String,
    val category: String,
    val provider: String,
    val price: Double,
    val currency: String,
    val durationMinutes: Int,
    val rating: Double,
    val description: String
)

data class TimeSlot(
    val id: String,
    val date: String,      // ISO-8601 date, e.g. "2026-09-10"
    val time: String,      // "HH:mm", e.g. "14:30"
    val available: Boolean
)

enum class BookingStatus {
    CONFIRMED,
    PENDING,
    CANCELLED
}

data class Booking(
    val id: String,
    val bookingNumber: String,
    val serviceId: String,
    val serviceName: String,
    val provider: String,
    val date: String,
    val time: String,
    val status: BookingStatus,
    val customerName: String,
    val contact: String
)

/**
 * Request data required to create a new booking.
 */
data class BookingRequest(
    val serviceId: String,
    val slotId: String,
    val date: String,
    val time: String,
    val customerName: String,
    val contact: String
)

