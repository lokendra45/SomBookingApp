package com.fourteen.sombookingapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fourteen.sombookingapp.ui.screens.BookingDetailScreen
import com.fourteen.sombookingapp.ui.screens.BookingListScreen
import com.fourteen.sombookingapp.ui.screens.HomeScreen
import com.fourteen.sombookingapp.ui.screens.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToBookings = {
                    navController.navigate(Screen.BookingList)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
                onNavigateToDetail = { bookingId ->
                    navController.navigate(Screen.BookingDetail(bookingId = bookingId))
                }
            )
        }

        composable<Screen.BookingList> {
            BookingListScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingDetail(bookingId = bookingId))
                }
            )
        }

        composable<Screen.BookingDetail> { backStackEntry ->
            val detail: Screen.BookingDetail = backStackEntry.toRoute()
            BookingDetailScreen(
                bookingId = detail.bookingId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Profile> {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
