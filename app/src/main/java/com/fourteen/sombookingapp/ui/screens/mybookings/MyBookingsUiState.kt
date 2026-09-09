package com.fourteen.sombookingapp.ui.screens.mybookings

import com.fourteen.sombookingapp.data.model.Booking
import kotlinx.collections.immutable.ImmutableList

data class MyBookingsData(
    val bookings: ImmutableList<Booking>
)

sealed interface MyBookingsUiState {
    data object Loading : MyBookingsUiState
    data class Error(val message: String) : MyBookingsUiState
    data object Empty : MyBookingsUiState
    data class Success(val data: MyBookingsData) : MyBookingsUiState
}
