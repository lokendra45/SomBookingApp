package com.fourteen.sombookingapp.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * State holder for top-level navigation state in SomBookingApp.
 */
@Stable
class SomAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    val coroutineScope: CoroutineScope
) {
    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigateTo(route: NavigationRoute) {
        navController.navigate(route)
    }

    fun showSnackbar(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun rememberSomAppState(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SomAppState {
    return remember(navController, snackbarHostState, coroutineScope) {
        SomAppState(navController, snackbarHostState, coroutineScope)
    }
}
