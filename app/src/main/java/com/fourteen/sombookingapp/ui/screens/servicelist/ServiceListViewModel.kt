package com.fourteen.sombookingapp.ui.screens.servicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.repository.BookingRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class ServiceListViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val uiState: StateFlow<ServiceListUiState> = _searchQuery
        .debounce(300L.milliseconds)
        .flatMapLatest { query ->
            repository.getServices(query.ifBlank { null }).map { result ->
                when (result) {
                    is ApiResult.Success -> ServiceListUiState.Success(
                        searchQuery = query,
                        data = ServiceListData(
                            services = result.data.toImmutableList(),
                            isEmpty = result.data.isEmpty()
                        )
                    )
                    is ApiResult.Error -> ServiceListUiState.Error(
                        searchQuery = query,
                        message = result.message
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ServiceListUiState.Loading()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun retry() {
        val current = _searchQuery.value
        _searchQuery.value = ""
        _searchQuery.value = current
    }
}
