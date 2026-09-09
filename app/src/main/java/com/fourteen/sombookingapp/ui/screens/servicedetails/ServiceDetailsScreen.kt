package com.fourteen.sombookingapp.ui.screens.servicedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.fourteen.sombookingapp.di.ServiceDetailsViewModelFactory
import com.fourteen.sombookingapp.ui.components.DateSelector
import com.fourteen.sombookingapp.ui.components.EmptyView
import com.fourteen.sombookingapp.ui.components.ErrorView
import com.fourteen.sombookingapp.ui.components.LoadingView
import com.fourteen.sombookingapp.ui.components.RatingBadge
import com.fourteen.sombookingapp.ui.components.SomElevatedCard
import com.fourteen.sombookingapp.ui.components.SomPrimaryButton
import com.fourteen.sombookingapp.ui.components.SomSectionHeader
import com.fourteen.sombookingapp.ui.components.SomTonalCard
import com.fourteen.sombookingapp.ui.components.SomTopAppBar
import com.fourteen.sombookingapp.ui.components.TimeSlotGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(
    serviceId: String,
    onBack: () -> Unit,
    onContinueToBooking: (slotId: String, date: String, time: String) -> Unit,
    viewModel: ServiceDetailsViewModel = viewModel(
        factory = ServiceDetailsViewModelFactory(serviceId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SomTopAppBar(
                title = "Service Details",
                onBackClick = onBack
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ServiceDetailsUiState.Loading -> LoadingView(
                modifier = Modifier.padding(padding),
                label = "Loading service details..."
            )
            is ServiceDetailsUiState.Error -> ErrorView(
                message = state.message,
                modifier = Modifier.padding(padding),
                onRetry = viewModel::retryService
            )
            is ServiceDetailsUiState.Success -> ServiceDetailsContent(
                modifier = Modifier.padding(padding),
                data = state.data,
                onDateSelected = viewModel::onDateSelected,
                onSlotSelected = viewModel::onSlotSelected,
                onRetryAvailability = viewModel::retryAvailability,
                onContinueToBooking = onContinueToBooking
            )
        }
    }
}

@Composable
private fun ServiceDetailsContent(
    modifier: Modifier = Modifier,
    data: ServiceDetailsData,
    onDateSelected: (String) -> Unit,
    onSlotSelected: (com.fourteen.sombookingapp.data.model.TimeSlot) -> Unit,
    onRetryAvailability: () -> Unit,
    onContinueToBooking: (slotId: String, date: String, time: String) -> Unit
) {
    val service = data.service
    Column(modifier = modifier.fillMaxSize()) {
        SomTonalCard(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    service.name, 
                    style = MaterialTheme.typography.headlineSmall, 
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                RatingBadge(rating = service.rating)
            }
            Text(
                "${service.provider} · ${service.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                service.description, 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${service.currency} ${"%.2f".format(service.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                androidx.compose.material3.Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        "${service.durationMinutes} min",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        SomSectionHeader(title = "Select Date")
        DateSelector(
            dates = data.availableDates,
            selectedDate = data.selectedDateIso,
            onDateSelected = onDateSelected
        )

        SomSectionHeader(title = "Available Time Slots")

        when {
            data.isLoadingAvailability -> LoadingView(label = "Checking slot availability...")
            data.availabilityErrorMessage != null -> ErrorView(
                data.availabilityErrorMessage,
                onRetry = onRetryAvailability
            )
            data.isSlotsEmpty -> EmptyView("No available slots on this date.")
            else -> TimeSlotGrid(
                slots = data.availableSlots,
                selectedSlot = data.selectedSlot,
                onSlotSelected = onSlotSelected
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        androidx.compose.material3.Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            SomPrimaryButton(
                text = "Continue to Booking",
                onClick = {
                    data.selectedSlot?.let {
                        onContinueToBooking(it.id, it.date, it.time)
                    }
                },
                enabled = data.selectedSlot != null,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
