package com.fourteen.sombookingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fourteen.sombookingapp.ui.screens.BookingScreen
import com.fourteen.sombookingapp.ui.screens.MyBookingsScreen
import com.fourteen.sombookingapp.ui.screens.ServiceDetailsScreen
import com.fourteen.sombookingapp.ui.screens.ServiceListScreen
import com.fourteen.sombookingapp.viewmodel.BookingArgs

@Composable
fun SomNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.ServiceList
    ) {
        composable<NavigationRoute.ServiceList> {
            ServiceListScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(NavigationRoute.ServiceDetails(serviceId = serviceId))
                },
                onMyBookingsClick = {
                    navController.navigate(NavigationRoute.MyBookings)
                }
            )
        }

        composable<NavigationRoute.ServiceDetails> { backStackEntry ->
            val route: NavigationRoute.ServiceDetails = backStackEntry.toRoute()
            ServiceDetailsScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onContinueToBooking = { slotId, date, time ->
                    navController.navigate(
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
                onBack = { navController.popBackStack() },
                onViewMyBookings = {
                    navController.navigate(NavigationRoute.MyBookings) {
                        popUpTo(NavigationRoute.ServiceList)
                    }
                }
            )
        }

        composable<NavigationRoute.MyBookings> {
            MyBookingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
