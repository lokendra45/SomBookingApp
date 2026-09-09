package com.fourteen.sombookingapp.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourteen.sombookingapp.data.api.ApiErrorType
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.Booking
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
    val email: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val confirmedBookingId: String? = null,
    val confirmedBookingNumber: String? = null,
    val conflictErrorMessage: String? = null,
    val submitErrorMessage: String? = null,
    val validationErrorMessage: String? = null
)

/**
 * Manages the booking form for a specific service slot.
 *
 * Combines a reactive service fetch (retryable via [retryLoadService]) with
 * user-entered form fields into a single [uiState] stream consumed by the UI.
 */
class BookingViewModel(
    private val args: BookingArgs,
    private val repository: BookingRepository
) : ViewModel() {

    private val _form = MutableStateFlow(FormState())
    private val _retryTrigger = MutableStateFlow(0)

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
                        contact = form.email,
                        nameError = form.nameError,
                        contactError = form.emailError,
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

    private var _lastConfirmedBooking: Booking? = null

    fun onNameChanged(name: String) {
        _form.update { it.copy(customerName = name, nameError = null) }
    }

    fun onEmailChanged(email: String) {
        _form.update { it.copy(email = email, emailError = null) }
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

    /** Validates the form and, if valid, submits the booking to the repository. */
    fun submit() {
        val form = _form.value
        val nameErr  = validateName(form.customerName)
        val emailErr = validateEmail(form.email)

        if (nameErr != null || emailErr != null) {
            _form.update { it.copy(nameError = nameErr, emailError = emailErr) }
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
                contact = form.email.trim()
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
                    _form.update {
                        it.copy(
                            isSubmitting = false,
                            conflictErrorMessage   = result.message.takeIf { result.type == ApiErrorType.CONFLICT },
                            validationErrorMessage = result.message.takeIf { result.type == ApiErrorType.VALIDATION },
                            submitErrorMessage     = result.message.takeIf { result.type == ApiErrorType.GENERIC || result.type == ApiErrorType.NOT_FOUND }
                        )
                    }
                }
            }
        }
    }

    private fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty()   -> "Name is required."
            trimmed.length < 2  -> "Name must be at least 2 characters."
            trimmed.any { it.isDigit() } -> "Name must not contain numbers."
            else                -> null
        }
    }

    private fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        val parts   = trimmed.split('@')
        return when {
            trimmed.isEmpty()                              -> "Email is required."
            parts.size != 2 || parts[0].isEmpty()         -> "Enter a valid email address."
            parts[1].isEmpty() || !parts[1].contains('.') -> "Email domain is invalid."
            parts[1].endsWith('.')                         -> "Email domain must not end with a dot."
            else                                           -> null
        }
    }
}
