package com.fourteen.sombookingapp.ui.screens.servicelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.di.ServiceListViewModelFactory
import com.fourteen.sombookingapp.ui.components.EmptyView
import com.fourteen.sombookingapp.ui.components.ErrorView
import com.fourteen.sombookingapp.ui.components.LoadingView
import com.fourteen.sombookingapp.ui.components.ServiceCard
import com.fourteen.sombookingapp.ui.components.SomSearchTextField
import com.fourteen.sombookingapp.ui.components.SomTopAppBar
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    onServiceClick: (String) -> Unit,
    onMyBookingsClick: () -> Unit,
    viewModel: ServiceListViewModel = viewModel(factory = ServiceListViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SomTopAppBar(
                title = "Discover Services",
                actions = {
                    IconButton(onClick = onMyBookingsClick) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "My Bookings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SomSearchTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (val state = uiState) {
                is ServiceListUiState.Loading -> LoadingView(label = "Loading services...")
                is ServiceListUiState.Error -> ErrorView(state.message, onRetry = viewModel::retry)
                is ServiceListUiState.Success -> {
                    if (state.data.isEmpty) {
                        EmptyView("No services match your search.")
                    } else {
                        ServiceList(state.data.services, onServiceClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceList(services: ImmutableList<Service>, onServiceClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(services, key = { it.id }) { service ->
            ServiceCard(service, onClick = { onServiceClick(service.id) })
        }
    }
}
