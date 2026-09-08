package com.fourteen.sombookingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.fourteen.sombookingapp.ui.screens.BookingScreen
import com.fourteen.sombookingapp.ui.screens.MyBookingsScreen
import com.fourteen.sombookingapp.ui.screens.ServiceDetailsScreen
import com.fourteen.sombookingapp.ui.screens.ServiceListScreen
import com.fourteen.sombookingapp.viewmodel.BookingArgs

@Composable
fun SomApp(
    appState: SomAppState = rememberSomAppState()
) {
    SomNavHost(appState = appState)
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
