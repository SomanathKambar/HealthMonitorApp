package com.vkm.healthmonitor.compose.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    sheetContent: Pair<String, String>,
    recommendation: String
) {
    if (visible) {
        ModalBottomSheet  (onDismissRequest = { onDismiss() }) {
            Column  (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                androidx.compose.material3.Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Text(text = sheetContent.first)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Text(text = sheetContent.second)
                androidx.compose.material3.Text(
                    recommendation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
            Button  (
                onClick = { onDismiss() },
                modifier = Modifier.align(Alignment.End)
            ) {
                androidx.compose.material3.Text("Close")
            }
        }
    }
}