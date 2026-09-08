package com.fourteen.sombookingapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fourteen.sombookingapp.data.model.BookingStatus

/**
 * Reusable Material 3 Expressive status badge pill for booking states.
 */
@Composable
fun StatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (status) {
        BookingStatus.CONFIRMED -> "Confirmed" to MaterialTheme.colorScheme.secondary
        BookingStatus.PENDING -> "Pending" to MaterialTheme.colorScheme.primary
        BookingStatus.CANCELLED -> "Cancelled" to MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = CircleShape
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
