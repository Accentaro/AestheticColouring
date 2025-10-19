package com.trippify.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trippify.app.scenes.SceneDefinition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneSelector(
    scenes: List<SceneDefinition>,
    selectedSceneId: String,
    onSceneSelected: (SceneDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Choose Scene",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(scenes) { scene ->
                val selected = scene.id == selectedSceneId
                Surface(
                    tonalElevation = if (selected) 12.dp else 4.dp,
                    shadowElevation = if (selected) 16.dp else 6.dp,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(0.75f),
                    onClick = { onSceneSelected(scene) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = scene.displayName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text = scene.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
