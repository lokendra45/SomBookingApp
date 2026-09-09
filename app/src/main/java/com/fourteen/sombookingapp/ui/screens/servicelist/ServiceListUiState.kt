package com.fourteen.sombookingapp.ui.screens.servicelist

import com.fourteen.sombookingapp.data.model.Service
import kotlinx.collections.immutable.ImmutableList

data class ServiceListData(
    val services: ImmutableList<Service>,
    val isEmpty: Boolean
)

sealed interface ServiceListUiState {
    val searchQuery: String
    
    data class Loading(
        override val searchQuery: String = ""
    ) : ServiceListUiState

    data class Error(
        override val searchQuery: String,
        val message: String
    ) : ServiceListUiState

    data class Success(
        override val searchQuery: String,
        val data: ServiceListData
    ) : ServiceListUiState
}
