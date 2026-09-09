package com.fourteen.sombookingapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable & customizable Material 3 Expressive Container Card.
 */
@Composable
fun SomElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
        ) {
            Column(modifier = Modifier.padding(18.dp), content = content)
        }
    } else {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
        ) {
            Column(modifier = Modifier.padding(18.dp), content = content)
        }
    }
}

@Composable
fun SomOutlinedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        OutlinedCard(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            border = border
        ) {
            Column(modifier = Modifier.padding(18.dp), content = content)
        }
    } else {
        OutlinedCard(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            border = border
        ) {
            Column(modifier = Modifier.padding(18.dp), content = content)
        }
    }
}

@Composable
fun SomTonalCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentPadding: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}
