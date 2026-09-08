package com.fourteen.sombookingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fourteen.sombookingapp.navigation.SomNavHost
import com.fourteen.sombookingapp.ui.theme.SomBookingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SomBookingAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SomNavHost()
                }
            }
        }
    }
}


