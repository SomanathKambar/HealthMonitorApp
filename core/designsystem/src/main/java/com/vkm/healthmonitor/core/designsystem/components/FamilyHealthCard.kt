package com.vkm.healthmonitor.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vkm.healthmonitor.core.model.ProfileWithVitals
import com.vkm.healthmonitor.core.model.SheetContent
import com.vkm.healthmonitor.core.model.SliceType
import com.vkm.healthmonitor.core.model.computeFamilyHealthSummary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FamilyHealthCard(profilesWithVitals: List<ProfileWithVitals>) {
    val familySummary = remember(profilesWithVitals) {
        computeFamilyHealthSummary(profilesWithVitals)
    }

    var sheetContent by remember { mutableStateOf<SheetContent?>(null) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            sheetContent?.let {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it.issues,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.recommendation,
                        style = when (it.severity) {
                            SliceType.CRITICAL -> MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red, fontStyle = FontStyle.Italic
                            )
                            SliceType.WARNING -> MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFB8860B), fontStyle = FontStyle.Italic
                            )
                            SliceType.NORMAL -> MaterialTheme.typography.bodySmall.copy(
                                color = Color.Green
                            )
                        }
                    )
                }
            } ?: Box(modifier = Modifier.height(1.dp))
        },
        sheetGesturesEnabled = true
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            PieChartComposable (
                familySummary = familySummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                onSliceClick = { slice ->
                    val sliceType = SliceType.valueOf(slice)
                    val summary = familySummary.firstOrNull { it.slice == sliceType }
                    if (summary != null) {
                        if (summary.items.isEmpty()) {
                            sheetContent = SheetContent(
                                title = slice,
                                issues = "All members are within normal range.",
                                recommendation = "No action required. Maintain healthy lifestyle.",
                                severity = sliceType
                            )
                        } else {
                            val issuesText = summary.items.joinToString("\n") { item ->
                                "${item.profile.name}: ${item.issues}"
                            }
                            val recText = summary.items.joinToString("\n") { item ->
                                "${item.profile.name}: ${item.recommendation}"
                            }
                            sheetContent = SheetContent(
                                title = "$slice Issues",
                                issues = issuesText,
                                recommendation = recText,
                                severity = sliceType
                            )
                        }
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            familySummary.forEach { fs ->
                Text("${fs.slice.name}: ${fs.count}")
            }
        }
    }
}
