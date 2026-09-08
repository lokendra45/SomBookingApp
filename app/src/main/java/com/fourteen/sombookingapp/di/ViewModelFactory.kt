package com.fourteen.sombookingapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.fourteen.sombookingapp.data.repository.BookingRepository
import com.fourteen.sombookingapp.ui.screens.booking.BookingArgs
import com.fourteen.sombookingapp.ui.screens.booking.BookingViewModel
import com.fourteen.sombookingapp.ui.screens.mybookings.MyBookingsViewModel
import com.fourteen.sombookingapp.ui.screens.servicedetails.ServiceDetailsViewModel
import com.fourteen.sombookingapp.ui.screens.servicelist.ServiceListViewModel

class ServiceListViewModelFactory(
    private val repository: BookingRepository = RepositoryProvider.bookingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return ServiceListViewModel(repository) as T
    }
}

class ServiceDetailsViewModelFactory(
    private val serviceId: String,
    private val repository: BookingRepository = RepositoryProvider.bookingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return ServiceDetailsViewModel(serviceId, repository) as T
    }
}

class BookingViewModelFactory(
    private val args: BookingArgs,
    private val repository: BookingRepository = RepositoryProvider.bookingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return BookingViewModel(args, repository) as T
    }
}

class MyBookingsViewModelFactory(
    private val repository: BookingRepository = RepositoryProvider.bookingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return MyBookingsViewModel(repository) as T
    }
}
