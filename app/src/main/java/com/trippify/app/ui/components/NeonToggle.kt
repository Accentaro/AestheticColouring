package com.trippify.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun NeonToggle(
    label: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    locked: Boolean = false,
    lockLabel: String = "Premium feature",
    onCheckedChange: (Boolean) -> Unit,
    onLockedClick: (() -> Unit)? = null
) {
    val background by animateColorAsState(
        targetValue = when {
            locked -> Color(0x33FFFFFF)
            checked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else -> Color(0x33000000)
        },
        label = "NeonToggleBackground"
    )
    val knobColor by animateColorAsState(
        targetValue = when {
            locked -> Color(0x55FFFFFF)
            checked -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondary
        },
        label = "NeonToggleKnob"
    )

    val toggleDescription = description ?: label
    val stateDescriptionText = when {
        locked -> "$lockLabel locked"
        checked -> "$label on"
        else -> "$label off"
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .semantics {
                role = Role.Switch
                contentDescription = toggleDescription
                stateDescription = stateDescriptionText
            }
            .clickable(enabled = enabled || locked, onClick = {
                when {
                    locked -> onLockedClick?.invoke()
                    enabled -> onCheckedChange(!checked)
                }
            })
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(knobColor)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled || locked) Color.White else Color.White.copy(alpha = 0.5f)
            )
            if (description != null) {
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = if (enabled) 0.7f else 0.4f)
                )
            }
            if (locked) {
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = lockLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }
        }
    }
}
