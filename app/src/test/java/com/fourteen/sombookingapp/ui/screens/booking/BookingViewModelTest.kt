package com.fourteen.sombookingapp.ui.screens.booking

import com.fourteen.sombookingapp.data.api.ApiErrorType
import com.fourteen.sombookingapp.data.api.ApiResult
import com.fourteen.sombookingapp.data.model.Booking
import com.fourteen.sombookingapp.data.model.BookingRequest
import com.fourteen.sombookingapp.data.model.BookingStatus
import com.fourteen.sombookingapp.data.model.Service
import com.fourteen.sombookingapp.data.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    class FakeBookingRepository : BookingRepository {
        var returnConflictError = false

        override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> {
            if (returnConflictError) {
                return ApiResult.Error("Slot already booked", ApiErrorType.CONFLICT)
            }
            return ApiResult.Success(
                Booking(
                    id = "1",
                    bookingNumber = "SOM-001",
                    serviceId = request.serviceId,
                    serviceName = "Home Cleaning",
                    provider = "CleanCo",
                    date = request.date,
                    time = request.time,
                    status = BookingStatus.CONFIRMED,
                    customerName = request.customerName,
                    contact = request.contact
                )
            )
        }

        override fun getServiceById(serviceId: String): Flow<ApiResult<Service>> = flow {
            emit(ApiResult.Success(Service("svc-1", "Home Cleaning", "Cleaning", "CleanCo", 1500.0, "NPR", 60, 4.5, "Desc")))
        }

        override fun getServices(query: String?) = throw NotImplementedError()
        override fun getAvailability(serviceId: String, date: String) = throw NotImplementedError()
        override fun getBookings() = throw NotImplementedError()
    }

    private fun createViewModel(repository: FakeBookingRepository = FakeBookingRepository()): BookingViewModel {
        return BookingViewModel(BookingArgs("svc-1", "slot-1", "2026-10-25", "10:00"), repository)
    }

    @Test
    fun `validation fails when name is blank`() = runTest {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onNameChanged("")
        viewModel.onContactChanged("9800000000")
        viewModel.submit()

        val state = viewModel.uiState.value as BookingUiState.Content
        assertEquals("Name is required.", state.data.nameError)
        assertNull(state.data.confirmedBookingNumber)
        job.cancel()
    }

    @Test
    fun `booking succeeds and shows confirmation number`() = runTest {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onNameChanged("Ram Bahadur")
        viewModel.onContactChanged("9800000000")
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BookingUiState.Content
        assertEquals("SOM-001", state.data.confirmedBookingNumber)
        assertNull(state.data.submitErrorMessage)
        job.cancel()
    }

    @Test
    fun `booking conflict shows conflict error message`() = runTest {
        val repository = FakeBookingRepository()
        repository.returnConflictError = true
        val viewModel = createViewModel(repository)
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onNameChanged("Ram Bahadur")
        viewModel.onContactChanged("9800000000")
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BookingUiState.Content
        assertNotNull(state.data.conflictErrorMessage)
        assertNull(state.data.confirmedBookingNumber)
        job.cancel()
    }
}
