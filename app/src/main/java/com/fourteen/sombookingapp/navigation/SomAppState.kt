package com.fourteen.sombookingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * State holder for top-level navigation state in SomBookingApp following Google's recommended architecture.
 */
@Stable
class SomAppState(
    val navController: NavHostController
) {
    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigateTo(route: NavigationRoute) {
        navController.navigate(route)
    }
}

@Composable
fun rememberSomAppState(
    navController: NavHostController = rememberNavController()
): SomAppState {
    return remember(navController) {
        SomAppState(navController)
    }
}
