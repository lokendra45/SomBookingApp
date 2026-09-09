package com.fourteen.sombookingapp.ui.screens.servicelist

import com.fourteen.sombookingapp.data.api.ApiErrorType
import com.fourteen.sombookingapp.data.api.ApiResult
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceListViewModelTest {

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
        var returnError = false
        var lastSearchQuery: String? = null

        override fun getServices(query: String?): Flow<ApiResult<List<Service>>> = flow {
            kotlinx.coroutines.delay(10)
            lastSearchQuery = query
            if (returnError) {
                emit(ApiResult.Error("API Error", ApiErrorType.GENERIC))
            } else {
                emit(ApiResult.Success(
                    listOf(Service("1", "Home Cleaning", "Cleaning", "CleanCo", 1500.0, "NPR", 60, 4.5, "Desc"))
                ))
            }
        }

        override fun getServiceById(serviceId: String) = throw NotImplementedError()
        override fun getAvailability(serviceId: String, date: String) = throw NotImplementedError()
        override suspend fun createBooking(request: com.fourteen.sombookingapp.data.model.BookingRequest) = throw NotImplementedError()
        override fun getBookings() = throw NotImplementedError()
    }

    @Test
    fun `shows services when load is successful`() = runTest {
        val viewModel = ServiceListViewModel(FakeBookingRepository())
        // Collect so WhileSubscribed activates the upstream
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value as ServiceListUiState.Success
        assertEquals(1, state.data.services.size)
        assertEquals("Home Cleaning", state.data.services[0].name)
        job.cancel()
    }

    @Test
    fun `shows error when repository fails`() = runTest {
        val repository = FakeBookingRepository()
        repository.returnError = true
        val viewModel = ServiceListViewModel(repository)
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value as ServiceListUiState.Error
        assertEquals("API Error", state.message)
        job.cancel()
    }

    @Test
    fun `search sends query to repository`() = runTest {
        val repository = FakeBookingRepository()
        val viewModel = ServiceListViewModel(repository)
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("cleaning")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ServiceListUiState.Success)
        assertEquals("cleaning", repository.lastSearchQuery)
        job.cancel()
    }
}
