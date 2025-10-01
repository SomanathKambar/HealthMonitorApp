package com.vkm.healthmonitor.compose.ui.screens


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.json.JSONArray
import java.nio.charset.Charset

data class LocalGuide(val title: String, val description: String)


//@Composable
//fun GuidesScreen(nav: NavController) {
//    val ctx = LocalContext.current
//    val healthTips = remember { loadHealthTips(ctx) }
//    val guides = remember { loadLocalGuides(ctx) }
//    val scrollState = rememberScrollState(0)
//
//    Column(Modifier.padding(16.dp).scrollable(scrollState, orientation = Orientation.Vertical)) {
//        Text("How to Use App", style = MaterialTheme.typography.titleLarge)
//        Spacer(Modifier.height(12.dp))
//        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(guides) { g ->
//                Card(Modifier.fillMaxWidth()) {
//                    Column(Modifier.padding(12.dp)) {
//                        Text(g.title, style = MaterialTheme.typography.titleMedium)
//                        Spacer(Modifier.height(4.dp))
//                        Text(g.description, style = MaterialTheme.typography.bodyMedium)
//                    }
//                }
//            }
//        }
//        Spacer(Modifier.height(12.dp))
//        Text("Health Tips", style = MaterialTheme.typography.titleLarge)
//        Spacer(Modifier.height(12.dp))
//        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(healthTips) { g ->
//                Card(Modifier.fillMaxWidth()) {
//                    Column(Modifier.padding(12.dp)) {
//                        Text(g.title, style = MaterialTheme.typography.titleMedium)
//                        Spacer(Modifier.height(4.dp))
//                        Text(g.description, style = MaterialTheme.typography.bodyMedium)
//                    }
//                }
//            }
//        }
//    }
//}

private fun loadHealthTips(ctx: Context): List<LocalGuide> {
    return try {
        val input = ctx.assets.open("health_plans.json")
        val json = input.readBytes().toString(Charset.defaultCharset())
        val arr = JSONArray(json)
        List(arr.length()) { idx ->
            val obj = arr.getJSONObject(idx)
            LocalGuide(
                title = obj.optString("title"),
                description = obj.optString("description")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}
private fun loadLocalGuides(ctx: Context): List<LocalGuide> {
    return try {
        val input = ctx.assets.open("how_to_use_app.json")
        val json = input.readBytes().toString(Charset.defaultCharset())
        val arr = JSONArray(json)
        List(arr.length()) { idx ->
            val obj = arr.getJSONObject(idx)
            LocalGuide(
                title = obj.optString("title"),
                description = obj.optString("description")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}


@Composable
fun GuidesScreen(navController: NavController?) {
    val ctx = LocalContext.current
    val guides = remember { loadLocalGuides(ctx) }
    var selectedGuide by remember { mutableStateOf<LocalGuide?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val healthTips = remember { loadHealthTips(ctx) }
    Column(Modifier.padding(16.dp)) {
        Text("Health Tips", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // Static health tips
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(healthTips) { g ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(g.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(g.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("How to Use the App", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // Dynamic guides from JSON
        if (guides.isEmpty()) {
            Text("No guides available. Please add health_plans.json in assets.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(guides) { g ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(g.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (g.description.length > 200) g.description.take(200) + "..." else g.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            TextButton (onClick = {
                                selectedGuide = g
                                showDialog = true
                            }) {
                                Text("Read more")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedGuide != null) {
        AlertDialog (
            onDismissRequest = { showDialog = false; selectedGuide = null },
            confirmButton = {
                TextButton(onClick = { showDialog = false; selectedGuide = null }) {
                    Text("Close")
                }
            },
            title = { Text(selectedGuide!!.title) },
            text = { Text(selectedGuide!!.description) }
        )
    }
}


