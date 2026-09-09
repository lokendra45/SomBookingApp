package com.fourteen.sombookingapp.ui.screens.servicedetails

import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.model.TimeSlot
import com.fourteen.sombookingapp.utils.DateTimeUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ServiceDetailsData(
    val service: Service,
    val availableDates: ImmutableList<DateTimeUtils.DateOption>,
    val selectedDateIso: String,
    val selectedSlot: TimeSlot? = null,
    val isLoadingAvailability: Boolean = false,
    val availableSlots: ImmutableList<TimeSlot> = persistentListOf(),
    val isSlotsEmpty: Boolean = false,
    val availabilityErrorMessage: String? = null
)

sealed interface ServiceDetailsUiState {
    data object Loading : ServiceDetailsUiState
    
    data class Error(val message: String) : ServiceDetailsUiState
    
    data class Success(
        val data: ServiceDetailsData
    ) : ServiceDetailsUiState
}
