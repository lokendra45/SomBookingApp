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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private data class SelectionState(
    val selectedDateIso: String = DateTimeUtils.getUpcomingDateOptions(7).first().isoDate,
    val selectedSlot: TimeSlot? = null,
    val serviceRetry: Int = 0,
    val availabilityRetry: Int = 0
)

class ServiceDetailsViewModel(
    private val serviceId: String,
    private val repository: BookingRepository
) : ViewModel() {

    private val _selectionState = MutableStateFlow(SelectionState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val serviceFlow = _selectionState
        .map { it.serviceRetry }
        .distinctUntilChanged()
        .flatMapLatest { repository.getServiceById(serviceId) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val availabilityFlow = _selectionState
        .map { it.selectedDateIso to it.availabilityRetry }
        .distinctUntilChanged()
        .flatMapLatest { (dateIso, _) -> repository.getAvailability(serviceId, dateIso) }

    val uiState: StateFlow<ServiceDetailsUiState> = combine(
        serviceFlow,
        availabilityFlow,
        _selectionState
    ) { serviceResult, availabilityResult, selection ->
        val availableDates = DateTimeUtils.getUpcomingDateOptions(7).toImmutableList()
        when (serviceResult) {
            is ApiResult.Error -> ServiceDetailsUiState.Error(serviceResult.message)
            is ApiResult.Success -> ServiceDetailsUiState.Success(
                data = ServiceDetailsData(
                    service = serviceResult.data,
                    availableDates = availableDates,
                    selectedDateIso = selection.selectedDateIso,
                    selectedSlot = selection.selectedSlot,
                    isLoadingAvailability = false,
                    availableSlots = when (availabilityResult) {
                        is ApiResult.Success -> availabilityResult.data.toImmutableList()
                        is ApiResult.Error -> persistentListOf()
                    },
                    isSlotsEmpty = availabilityResult is ApiResult.Success && availabilityResult.data.isEmpty(),
                    availabilityErrorMessage = when (availabilityResult) {
                        is ApiResult.Success -> null
                        is ApiResult.Error -> availabilityResult.message
                    }
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ServiceDetailsUiState.Loading
    )

    fun onDateSelected(dateIso: String) =
        _selectionState.update { it.copy(selectedDateIso = dateIso, selectedSlot = null) }

    fun onSlotSelected(slot: TimeSlot) =
        _selectionState.update { it.copy(selectedSlot = slot) }

    fun retryService() =
        _selectionState.update { it.copy(serviceRetry = it.serviceRetry + 1) }

    fun retryAvailability() =
        _selectionState.update { it.copy(availabilityRetry = it.availabilityRetry + 1) }
}
