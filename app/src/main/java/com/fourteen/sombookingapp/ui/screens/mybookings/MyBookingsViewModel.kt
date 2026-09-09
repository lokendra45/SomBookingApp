package com.fourteen.sombookingapp.ui.screens.mybookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.repository.BookingRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MyBookingsViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MyBookingsUiState> = retryTrigger
        .flatMapLatest {
            repository.getBookings().map { result ->
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
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyBookingsUiState.Loading
        )

    fun retry() = retryTrigger.update { it + 1 }
}
