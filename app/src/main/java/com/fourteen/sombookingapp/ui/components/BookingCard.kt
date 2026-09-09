package com.fourteen.sombookingapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fourteen.sombookingapp.data.model.Booking

@Composable
fun BookingCard(
    booking: Booking,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    SomElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                booking.bookingNumber, 
                style = MaterialTheme.typography.titleMedium, 
                color = MaterialTheme.colorScheme.primary
            )
            StatusBadge(status = booking.status)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(booking.serviceName, style = MaterialTheme.typography.titleLarge)
        Text(
            booking.provider, 
            style = MaterialTheme.typography.bodyMedium, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "${booking.date} at ${booking.time}", 
            style = MaterialTheme.typography.bodyMedium, 
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
