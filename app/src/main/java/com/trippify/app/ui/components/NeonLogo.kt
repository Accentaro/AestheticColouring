package com.trippify.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.trippify.app.core.config.AppConfiguration
import com.trippify.app.core.theme.LocalGlowSpec
import com.trippify.app.R

@Composable
fun NeonLogo(
    modifier: Modifier = Modifier,
    onSecretTap: (() -> Unit)? = null
) {
    val glowSpec = LocalGlowSpec.current
    val transition = rememberInfiniteTransition(label = "LogoBreathing")
    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2200 / glowSpec.animationSpeedMultiplier).toInt(),
                easing = FastOutSlowInEasing
            )
        ),
        label = "LogoScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .semantics { contentDescription = stringResource(id = R.string.content_desc_logo) }
            .clickable(enabled = onSecretTap != null) {
                onSecretTap?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = AppConfiguration.appName.uppercase(),
            style = MaterialTheme.typography.displayLarge.merge(
                TextStyle(
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = glowSpec.gradient.colors.first(),
                        blurRadius = 22f
                    )
                )
            ),
            modifier = Modifier.scale(scale)
        )
    }
}
