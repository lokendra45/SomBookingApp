package com.fourteen.sombookingapp.ui.screens.servicedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.model.TimeSlot
import com.fourteen.sombookingapp.data.repository.BookingRepository
import com.fourteen.sombookingapp.utils.DateTimeUtils
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceDetailsViewModel(
    private val serviceId: String,
    private val repository: BookingRepository
) : ViewModel() {

    private val _selectedDateIso = MutableStateFlow(DateTimeUtils.getUpcomingDateOptions(7).first().isoDate)
    private val _selectedSlot    = MutableStateFlow<TimeSlot?>(null)
    private val _serviceRetry    = MutableStateFlow(0)
    private val _availabilityRetry = MutableStateFlow(0)

    // Re-fetches whenever retry is incremented
    private val serviceFlow = _serviceRetry
        .flatMapLatest { repository.getServiceById(serviceId) }

    // Re-fetches whenever the selected date or retry changes
    private val availabilityFlow = combine(_selectedDateIso, _availabilityRetry) { date, _ -> date }
        .flatMapLatest { dateIso -> repository.getAvailability(serviceId, dateIso) }

    val uiState: StateFlow<ServiceDetailsUiState> = combine(
        serviceFlow,
        availabilityFlow,
        _selectedDateIso,
        _selectedSlot
    ) { serviceResult, availabilityResult, selectedDateIso, selectedSlot ->
        when (serviceResult) {
            is ApiResult.Error   -> ServiceDetailsUiState.Error(serviceResult.message)
            is ApiResult.Success -> ServiceDetailsUiState.Success(
                data = ServiceDetailsData(
                    service                  = serviceResult.data,
                    availableDates           = DateTimeUtils.getUpcomingDateOptions(7).toImmutableList(),
                    selectedDateIso          = selectedDateIso,
                    selectedSlot             = selectedSlot,
                    isLoadingAvailability    = false,
                    availableSlots           = if (availabilityResult is ApiResult.Success) availabilityResult.data.toImmutableList() else persistentListOf(),
                    isSlotsEmpty             = availabilityResult is ApiResult.Success && availabilityResult.data.isEmpty(),
                    availabilityErrorMessage = if (availabilityResult is ApiResult.Error) availabilityResult.message else null
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ServiceDetailsUiState.Loading
    )

    fun onDateSelected(dateIso: String) {
        _selectedDateIso.value = dateIso
        _selectedSlot.value = null
    }

    fun onSlotSelected(slot: TimeSlot) {
        _selectedSlot.value = slot
    }

    fun retryService()       = _serviceRetry.update { it + 1 }
    fun retryAvailability()  = _availabilityRetry.update { it + 1 }
}

