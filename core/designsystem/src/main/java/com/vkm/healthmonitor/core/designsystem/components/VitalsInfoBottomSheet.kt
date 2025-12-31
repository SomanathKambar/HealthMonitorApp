package com.vkm.healthmonitor.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsInfoBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    details: String,
    recommendation: String
) {
    if (visible) {
        ModalBottomSheet (onDismissRequest = { onDismiss() }) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text("Vitals: $details", style = MaterialTheme.typography.bodyMedium)
                Text("Recommendation:", style = MaterialTheme.typography.titleMedium)
                Text(recommendation, style = MaterialTheme.typography.bodyLarge, color = Color.Red)

                Spacer(Modifier.height(8.dp))
                Button (
                    onClick = { onDismiss() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
