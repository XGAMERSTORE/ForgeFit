package com.xgamerstore.forgefit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

/**
 * One-argument overload used by the Home screen. The existing two-argument
 * ExerciseCard remains available to Library, while cards on Home now open a
 * full-screen detail when tapped.
 */
@Composable
fun ExerciseCard(e: Exercise) {
    var open by remember { mutableStateOf(false) }

    if (open) {
        Dialog(onDismissRequest = { open = false }) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0B0D10)
            ) {
                Detail(e) { open = false }
            }
        }
    }

    ExerciseCard(e) { open = true }
}
