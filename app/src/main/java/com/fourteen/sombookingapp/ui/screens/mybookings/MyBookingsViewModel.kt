package com.fourteen.sombookingapp.ui.screens.mybookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.repository.BookingRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MyBookingsViewModel(
    repository: BookingRepository
) : ViewModel() {

    val uiState: StateFlow<MyBookingsUiState> = repository
        .getBookings()
        .map { result ->
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.isEmpty()) {
                        MyBookingsUiState.Empty
                    } else {
                        MyBookingsUiState.Success(
                            data = MyBookingsData(bookings = result.data.toImmutableList())
                        )
                    }
                }
                is ApiResult.Error -> MyBookingsUiState.Error(message = result.message)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyBookingsUiState.Loading
        )
}
