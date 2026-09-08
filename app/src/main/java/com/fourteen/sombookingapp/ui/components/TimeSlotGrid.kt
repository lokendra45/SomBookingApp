package com.fourteen.sombookingapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fourteen.sombookingapp.data.model.TimeSlot
import kotlinx.collections.immutable.ImmutableList

/**
 * Reusable time slot selection grid using ImmutableList for optimal Compose performance.
 */
@Composable
fun TimeSlotGrid(
    slots: ImmutableList<TimeSlot>,
    selectedSlot: TimeSlot?,
    onSlotSelected: (TimeSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        items(slots, key = { it.id }) { slot ->
            FilterChip(
                selected = selectedSlot?.id == slot.id,
                onClick = { if (slot.available) onSlotSelected(slot) },
                enabled = slot.available,
                label = { Text(slot.time) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
