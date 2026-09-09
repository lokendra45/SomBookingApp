package com.fourteen.sombookingapp.ui.screens.booking

import app.cash.turbine.test
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

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
            kotlinx.coroutines.delay(10)
            if (returnConflictError) {
                return ApiResult.Error("Slot already booked", ApiErrorType.CONFLICT)
            }
            return ApiResult.Success(
                Booking(
                    id = UUID.randomUUID().toString(),
                    bookingNumber = "SOM-123",
                    serviceId = request.serviceId,
                    serviceName = "Mock Service",
                    provider = "Mock Provider",
                    date = request.date,
                    time = request.time,
                    status = BookingStatus.CONFIRMED,
                    customerName = request.customerName,
                    contact = request.contact
                )
            )
        }

        override fun getServiceById(serviceId: String): Flow<ApiResult<Service>> = flow {
            kotlinx.coroutines.delay(10)
            emit(ApiResult.Success(Service("svc-1", "Mock Service", "Cat", "Prov", 10.0, "USD", 60, 5.0, "Desc")))
        }

        override fun getServices(query: String?) = throw NotImplementedError()
        override fun getAvailability(serviceId: String, date: String) = throw NotImplementedError()
        override fun getBookings() = throw NotImplementedError()
    }

    @Test
    fun `validation fails when name is empty`() = runTest {
        val repository = FakeBookingRepository()
        val args = BookingArgs("svc-1", "slot-1", "2026-10-25", "10:30")
        val viewModel = BookingViewModel(args, repository)
        
        viewModel.uiState.test {
            advanceUntilIdle() // let service load

            viewModel.onNameChanged("")
            viewModel.onContactChanged("555-1234")
            viewModel.submit()
            
            advanceUntilIdle()

            val resultState = expectMostRecentItem() as BookingUiState.Content
            assertEquals("Please fix errors in the form.", resultState.data.validationErrorMessage)
            assertEquals("Name is required.", resultState.data.nameError)
            assertTrue(!resultState.data.isSubmitting)
            assertNull(resultState.data.confirmedBookingNumber)
        }
    }

    @Test
    fun `successful booking updates state correctly`() = runTest {
        val repository = FakeBookingRepository()
        val args = BookingArgs("svc-1", "slot-1", "2026-10-25", "10:30")
        val viewModel = BookingViewModel(args, repository)
        
        viewModel.uiState.test {
            advanceUntilIdle() // let service load
            
            viewModel.onNameChanged("John")
            viewModel.onContactChanged("john@example.com")
            viewModel.submit()
            
            advanceUntilIdle() // let booking complete

            val successState = expectMostRecentItem() as BookingUiState.Content
            assertTrue(!successState.data.isSubmitting)
            assertEquals("SOM-123", successState.data.confirmedBookingNumber)
        }
    }

    @Test
    fun `conflict error from repository surfaces correctly`() = runTest {
        val repository = FakeBookingRepository()
        repository.returnConflictError = true
        val args = BookingArgs("svc-1", "slot-1", "2026-10-25", "10:30")
        val viewModel = BookingViewModel(args, repository)
        
        viewModel.uiState.test {
            advanceUntilIdle() // let service load
            
            viewModel.onNameChanged("John")
            viewModel.onContactChanged("john@example.com")
            viewModel.submit()
            
            advanceUntilIdle() // let booking complete

            val errorState = expectMostRecentItem() as BookingUiState.Content
            assertTrue(!errorState.data.isSubmitting)
            assertEquals("Slot already booked", errorState.data.conflictErrorMessage)
            assertNull(errorState.data.confirmedBookingNumber)
        }
    }
}
