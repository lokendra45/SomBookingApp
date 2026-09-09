package com.fourteen.sombookingapp.ui.screens.servicelist

import app.cash.turbine.test
import com.fourteen.sombookingapp.data.api.ApiErrorType
import com.fourteen.sombookingapp.data.api.ApiResult
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
        var returnEmpty = false
        var lastSearchQuery: String? = null

        override fun getServices(query: String?): Flow<ApiResult<List<Service>>> = flow {
            kotlinx.coroutines.delay(10)
            lastSearchQuery = query
            if (returnError) {
                emit(ApiResult.Error("API Error", ApiErrorType.GENERIC))
                return@flow
            }
            if (returnEmpty) {
                emit(ApiResult.Success(emptyList()))
                return@flow
            }

            emit(ApiResult.Success(
                listOf(
                    Service("1", "Test Service", "Cat", "Prov", 10.0, "USD", 60, 5.0, "Desc")
                )
            ))
        }
        
        override fun getServiceById(serviceId: String) = throw NotImplementedError()
        override fun getAvailability(serviceId: String, date: String) = throw NotImplementedError()
        override suspend fun createBooking(request: com.fourteen.sombookingapp.data.model.BookingRequest) = throw NotImplementedError()
        override fun getBookings() = throw NotImplementedError()
    }

    @Test
    fun `initial state loads services successfully`() = runTest {
        val repository = FakeBookingRepository()
        val viewModel = ServiceListViewModel(repository)
        
        viewModel.uiState.test {
            advanceUntilIdle()
            val successState = expectMostRecentItem() as ServiceListUiState.Success
            assertEquals(1, successState.data.services.size)
            assertEquals("Test Service", successState.data.services[0].name)
        }
    }

    @Test
    fun `search query updates state and fetches new data`() = runTest {
        val repository = FakeBookingRepository()
        val viewModel = ServiceListViewModel(repository)

        viewModel.uiState.test {
            advanceUntilIdle() // let initial load finish
            
            viewModel.onSearchQueryChanged("Plumbing")
            advanceUntilIdle() // let debounce and fetch finish

            val successState = expectMostRecentItem() as ServiceListUiState.Success
            assertEquals("Plumbing", repository.lastSearchQuery)
            assertEquals("Plumbing", successState.searchQuery)
        }
    }

    @Test
    fun `repository error updates state with error message`() = runTest {
        val repository = FakeBookingRepository()
        repository.returnError = true
        
        val viewModel = ServiceListViewModel(repository)
        
        viewModel.uiState.test {
            advanceUntilIdle()
            val errorState = expectMostRecentItem() as ServiceListUiState.Error
            assertEquals("API Error", errorState.message)
        }
    }
}
