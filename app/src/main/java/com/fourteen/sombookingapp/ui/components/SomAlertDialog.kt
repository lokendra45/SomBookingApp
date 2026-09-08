package com.fourteen.sombookingapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Reusable & customizable Material 3 Expressive Confirmation / Alert Dialog.
 */
@Composable
fun SomAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    dismissButtonText: String? = null,
    onDismissClick: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(confirmButtonText, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = if (dismissButtonText != null && onDismissClick != null) {
            {
                TextButton(onClick = onDismissClick) {
                    Text(dismissButtonText, style = MaterialTheme.typography.labelLarge)
                }
            }
        } else null,
        icon = icon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        },
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        },
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
    )
}
