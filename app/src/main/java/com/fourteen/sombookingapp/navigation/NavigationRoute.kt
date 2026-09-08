package com.fourteen.sombookingapp.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface NavigationRoute {
    @Serializable
    data object ServiceList : NavigationRoute

    @Serializable
    data class ServiceDetails(val serviceId: String) : NavigationRoute

    @Serializable
    data class Booking(
        val serviceId: String,
        val slotId: String,
        val date: String,
        val time: String
    ) : NavigationRoute

    @Serializable
    data object MyBookings : NavigationRoute
}
