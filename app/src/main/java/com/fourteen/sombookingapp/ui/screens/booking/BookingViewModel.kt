package com.fourteen.sombookingapp.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourteen.sombookingapp.data.api.ApiErrorType
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.repository.BookingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingArgs(
    val serviceId: String,
    val slotId: String,
    val date: String,
    val time: String
)

/**
 * Internal form state, kept separate so it can be combined with the service stream
 * via stateIn() without needing an init block.
 */
private data class FormState(
    val customerName: String = "",
    val contact: String = "",
    val nameError: String? = null,
    val contactError: String? = null,
    val isSubmitting: Boolean = false,
    val confirmedBookingId: String? = null,
    val confirmedBookingNumber: String? = null,
    val conflictErrorMessage: String? = null,
    val submitErrorMessage: String? = null,
    val validationErrorMessage: String? = null
)

class BookingViewModel(
    private val args: BookingArgs,
    private val repository: BookingRepository
) : ViewModel() {

    // Holds the reactive state of the user's input form
    private val _form = MutableStateFlow(FormState())
    
    // Controls fetching the service details from the repository
    private val _retryTrigger = MutableStateFlow(0)

    // Maps the fetched service and current form inputs into a unified UI state
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BookingUiState> = combine(
        _retryTrigger.flatMapLatest { repository.getServiceById(args.serviceId) },
        _form
    ) { serviceResult, form ->
        when (serviceResult) {
            is ApiResult.Error -> BookingUiState.Error(message = serviceResult.message)
            is ApiResult.Success -> {
                BookingUiState.Content(
                    data = BookingFormData(
                        service = serviceResult.data,
                        date = args.date,
                        time = args.time,
                        customerName = form.customerName,
                        contact = form.contact,
                        nameError = form.nameError,
                        contactError = form.contactError,
                        isSubmitting = form.isSubmitting,
                        conflictErrorMessage = form.conflictErrorMessage,
                        submitErrorMessage = form.submitErrorMessage,
                        validationErrorMessage = form.validationErrorMessage,
                        confirmedBookingNumber = form.confirmedBookingNumber
                    )
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookingUiState.Loading
    )

    // Holds the full Booking object after a successful submit.
    private var _lastConfirmedBooking: com.fourteen.sombookingapp.data.model.Booking? = null

    fun onNameChanged(name: String) {
        _form.update { it.copy(customerName = name, nameError = null) }
    }

    fun onContactChanged(contact: String) {
        _form.update { it.copy(contact = contact, contactError = null) }
    }

    fun resetToIdle() {
        _form.update {
            it.copy(
                submitErrorMessage = null,
                conflictErrorMessage = null,
                validationErrorMessage = null
            )
        }
    }

    fun retryLoadService() {
        _retryTrigger.update { it + 1 }
    }

    // Validates the form and triggers the booking submission
    fun submit() {
        val form = _form.value
        val nameErr = validateName(form.customerName)
        val contactErr = validateContact(form.contact)

        if (nameErr != null || contactErr != null) {
            _form.update {
                it.copy(
                    nameError = nameErr,
                    contactError = contactErr,
                    validationErrorMessage = "Please fix errors in the form."
                )
            }
            return
        }

        viewModelScope.launch {
            _form.update {
                it.copy(
                    isSubmitting = true,
                    conflictErrorMessage = null,
                    submitErrorMessage = null,
                    validationErrorMessage = null
                )
            }

            val request = BookingRequest(
                serviceId = args.serviceId,
                slotId = args.slotId,
                date = args.date,
                time = args.time,
                customerName = form.customerName.trim(),
                contact = form.contact.trim()
            )

            when (val result = repository.createBooking(request)) {
                is ApiResult.Success -> {
                    _lastConfirmedBooking = result.data
                    _form.update {
                        it.copy(
                            isSubmitting = false,
                            confirmedBookingId = result.data.id,
                            confirmedBookingNumber = result.data.bookingNumber
                        )
                    }
                }
                is ApiResult.Error -> {
                    _form.update { state ->
                        state.copy(
                            isSubmitting = false,
                            conflictErrorMessage = if (result.type == ApiErrorType.CONFLICT) result.message else null,
                            validationErrorMessage = if (result.type == ApiErrorType.VALIDATION) result.message else null,
                            submitErrorMessage = if (result.type != ApiErrorType.CONFLICT && result.type != ApiErrorType.VALIDATION) result.message else null
                        )
                    }
                }
            }
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required."
            name.trim().length < 2 -> "Name must be at least 2 characters."
            else -> null
        }
    }

    private fun validateContact(contact: String): String? {
        return when {
            contact.isBlank() -> "Phone or email is required."
            contact.trim().length < 5 -> "Please enter a valid phone or email."
            else -> null
        }
    }
}
