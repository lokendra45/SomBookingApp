package com.fourteen.sombookingapp.data.repository

import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.api.BookingApiService
import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.model.TimeSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository implementation that fetches booking data from the API service.
 */
class BookingRepositoryImpl(
    private val apiService: BookingApiService
) : BookingRepository {

    override fun getServices(query: String?): Flow<ApiResult<List<Service>>> = flow {
        withContext(Dispatchers.IO){
            emit(apiService.getServices(query))
        }
    }.flowOn(Dispatchers.IO)

    override fun getServiceById(serviceId: String): Flow<ApiResult<Service>> = flow {
        emit(apiService.getServiceById(serviceId))
    }

    override fun getAvailability(serviceId: String, date: String): Flow<ApiResult<List<TimeSlot>>> = flow {
        emit(apiService.getAvailability(serviceId, date))
    }

    override fun getBookings(): Flow<ApiResult<List<Booking>>> = flow {
        emit(apiService.getBookings())
    }

    override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> =
        apiService.createBooking(request)
}
