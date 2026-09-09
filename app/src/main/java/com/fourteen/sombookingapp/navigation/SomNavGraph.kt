package com.fourteen.sombookingapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.fourteen.sombookingapp.ui.screens.booking.BookingArgs
import com.fourteen.sombookingapp.ui.screens.booking.BookingScreen
import com.fourteen.sombookingapp.ui.screens.mybookings.MyBookingsScreen
import com.fourteen.sombookingapp.ui.screens.servicedetails.ServiceDetailsScreen
import com.fourteen.sombookingapp.ui.screens.servicelist.ServiceListScreen

@Composable
fun SomApp(
    appState: SomAppState = rememberSomAppState()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        SomNavHost(appState = appState)
        
        SnackbarHost(
            hostState = appState.snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun SomNavHost(
    appState: SomAppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.ServiceList,
        modifier = modifier
    ) {
        composable<NavigationRoute.ServiceList> {
            ServiceListScreen(
                onServiceClick = { serviceId ->
                    appState.navigateTo(NavigationRoute.ServiceDetails(serviceId = serviceId))
                },
                onMyBookingsClick = {
                    appState.navigateTo(NavigationRoute.MyBookings)
                }
            )
        }

        composable<NavigationRoute.ServiceDetails> { backStackEntry ->
            val route: NavigationRoute.ServiceDetails = backStackEntry.toRoute()
            ServiceDetailsScreen(
                serviceId = route.serviceId,
                onBack = { appState.popBackStack() },
                onContinueToBooking = { slotId, date, time ->
                    appState.navigateTo(
                        NavigationRoute.Booking(
                            serviceId = route.serviceId,
                            slotId = slotId,
                            date = date,
                            time = time
                        )
                    )
                }
            )
        }

        composable<NavigationRoute.Booking> { backStackEntry ->
            val route: NavigationRoute.Booking = backStackEntry.toRoute()
            val args = BookingArgs(
                serviceId = route.serviceId,
                slotId = route.slotId,
                date = route.date,
                time = route.time
            )
            BookingScreen(
                args = args,
                onShowSnackbar = { message -> appState.showSnackbar(message) },
                onBack = { appState.popBackStack() },
                onViewMyBookings = {
                    navController.navigate(NavigationRoute.MyBookings) {
                        popUpTo(NavigationRoute.ServiceList)
                    }
                }
            )
        }

        composable<NavigationRoute.MyBookings> {
            MyBookingsScreen(
                onBack = { appState.popBackStack() }
            )
        }
    }
}
