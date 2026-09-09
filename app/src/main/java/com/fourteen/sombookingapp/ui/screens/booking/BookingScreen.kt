package com.fourteen.sombookingapp.ui.screens.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fourteen.sombookingapp.di.BookingViewModelFactory
import com.fourteen.sombookingapp.ui.components.ErrorView
import com.fourteen.sombookingapp.ui.components.LoadingView
import com.fourteen.sombookingapp.ui.components.SomPrimaryButton
import com.fourteen.sombookingapp.ui.components.SomSectionHeader
import com.fourteen.sombookingapp.ui.components.SomTextField
import com.fourteen.sombookingapp.ui.components.SomTonalCard
import com.fourteen.sombookingapp.ui.components.SomTopAppBar

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.fourteen.sombookingapp.ui.components.SomTonalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    args: BookingArgs,
    onShowSnackbar: (String) -> Unit,
    onBack: () -> Unit,
    onViewMyBookings: () -> Unit,
    viewModel: BookingViewModel = viewModel(factory = BookingViewModelFactory(args))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SomTopAppBar(
                title = "Complete Booking",
                onBackClick = onBack
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is BookingUiState.Loading -> LoadingView(modifier = Modifier.padding(padding))
            is BookingUiState.Error -> ErrorView(
                message = state.message,
                modifier = Modifier.padding(padding),
                onRetry = viewModel::retryLoadService
            )
            is BookingUiState.Content -> {
                if (state.data.confirmedBookingNumber != null) {
                    BookingConfirmedContent(
                        modifier = Modifier.padding(padding),
                        bookingNumber = state.data.confirmedBookingNumber,
                        onViewMyBookings = onViewMyBookings
                    )
                } else {
                    BookingFormContent(
                        modifier = Modifier.padding(padding),
                        data = state.data,
                        onShowSnackbar = onShowSnackbar,
                        onClearError = viewModel::resetToIdle,
                        onNameChange = viewModel::onNameChanged,
                        onContactChange = viewModel::onEmailChanged,
                        onSubmit = viewModel::submit
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingFormContent(
    modifier: Modifier = Modifier,
    data: BookingFormData,
    onShowSnackbar: (String) -> Unit,
    onClearError: () -> Unit,
    onNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val generalError = data.validationErrorMessage ?: data.conflictErrorMessage ?: data.submitErrorMessage
    
    LaunchedEffect(generalError) {
        if (generalError != null) {
            onShowSnackbar(generalError)
            onClearError()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        SomTonalCard(
            modifier = Modifier.padding(16.dp),
            contentPadding = 24.dp
        ) {
            Text(
                text = "Booking Summary",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.service.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "by ${data.service.provider}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text(data.date, style = MaterialTheme.typography.titleMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text(data.time, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        SomSectionHeader("Your Details")

        SomTextField(
            value = data.customerName,
            onValueChange = onNameChange,
            label = "Full Name",
            placeholder = "e.g. Ram Bahadur",
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = null)
            },
            isError = data.nameError != null,
            errorMessage = data.nameError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SomTextField(
            value = data.contact,
            onValueChange = onContactChange,
            label = "Email",
            placeholder = "e.g. you@example.com",
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = data.contactError != null,
            errorMessage = data.contactError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            SomPrimaryButton(
                text = if (data.isSubmitting) "Booking..." else "Confirm Booking",
                onClick = onSubmit,
                enabled = !data.isSubmitting,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun BookingConfirmedContent(
    modifier: Modifier = Modifier,
    bookingNumber: String,
    onViewMyBookings: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(24.dp)
                    .height(80.dp)
                    .width(80.dp)
            )
        }

        Text(
            "Booking Confirmed!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Booking Reference",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    bookingNumber,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SomPrimaryButton(
            text = "View My Bookings",
            onClick = onViewMyBookings,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}
