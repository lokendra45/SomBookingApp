package com.fourteen.sombookingapp.ui.screens.booking

import com.fourteen.sombookingapp.data.model.Service

data class BookingFormData(
    val service: Service,
    val date: String,
    val time: String,
    val customerName: String = "",
    val contact: String = "",
    val nameError: String? = null,
    val contactError: String? = null,
    val isSubmitting: Boolean = false,
    val conflictErrorMessage: String? = null,
    val submitErrorMessage: String? = null,
    val validationErrorMessage: String? = null,
    val confirmedBookingNumber: String? = null
)

sealed interface BookingUiState {
    data object Loading : BookingUiState
    
    data class Error(val message: String) : BookingUiState
    
    data class Content(
        val data: BookingFormData
    ) : BookingUiState
}
