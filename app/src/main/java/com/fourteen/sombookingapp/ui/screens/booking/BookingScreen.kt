package com.fourteen.sombookingapp.ui.screens.booking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.fourteen.sombookingapp.ui.components.SomElevatedCard
import com.fourteen.sombookingapp.ui.components.SomPrimaryButton
import com.fourteen.sombookingapp.ui.components.SomSectionHeader
import com.fourteen.sombookingapp.ui.components.SomTextField
import com.fourteen.sombookingapp.ui.components.SomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    args: BookingArgs,
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
                        onNameChange = viewModel::onNameChanged,
                        onContactChange = viewModel::onContactChanged,
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
    onNameChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = modifier) {
        SomElevatedCard(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = 3.dp
        ) {
            Text("Service", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(data.service.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(Icons.Default.DateRange, contentDescription = "Date")
                Spacer(modifier = Modifier.width(8.dp))
                Text(data.date, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Schedule, contentDescription = "Time")
                Spacer(modifier = Modifier.width(8.dp))
                Text(data.time, style = MaterialTheme.typography.bodyMedium)
            }
        }

        SomSectionHeader("Your Details")
        SomTextField(
            value = data.customerName,
            onValueChange = onNameChange,
            label = "Full Name",
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
            label = "Phone or Email",
            isError = data.contactError != null,
            errorMessage = data.contactError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        data.validationErrorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorView(message = it)
        }

        data.conflictErrorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorView(message = it)
        }

        data.submitErrorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorView(message = it)
        }

        Spacer(modifier = Modifier.weight(1f))

        SomPrimaryButton(
            text = if (data.isSubmitting) "Booking..." else "Confirm Booking",
            onClick = onSubmit,
            enabled = !data.isSubmitting,
            modifier = Modifier.padding(16.dp)
        )
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Booking Confirmed!", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Booking reference: $bookingNumber",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        SomPrimaryButton(
            text = "View My Bookings",
            onClick = onViewMyBookings
        )
    }
}
