package com.fourteen.sombookingapp.ui.screens.mybookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.di.MyBookingsViewModelFactory
import com.fourteen.sombookingapp.ui.components.BookingCard
import com.fourteen.sombookingapp.ui.components.EmptyView
import com.fourteen.sombookingapp.ui.components.ErrorView
import com.fourteen.sombookingapp.ui.components.LoadingView
import com.fourteen.sombookingapp.ui.components.SomTopAppBar
import com.fourteen.sombookingapp.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit,
    viewModel: MyBookingsViewModel = viewModel(factory = MyBookingsViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SomTopAppBar(
                title = "My Bookings",
                onBackClick = onBack
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is MyBookingsUiState.Loading -> LoadingView(modifier = Modifier.padding(padding))
            is MyBookingsUiState.Error -> ErrorView(
                message = state.message,
                modifier = Modifier.padding(padding),
                onRetry = viewModel::retry
            )
            is MyBookingsUiState.Empty -> EmptyView(
                "You don't have any bookings yet.",
                modifier = Modifier.padding(padding)
            )
            is MyBookingsUiState.Success -> LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.data.bookings, key = { it.id }) { booking ->
                    BookingCard(booking)
                }
            }
        }
    }
}


