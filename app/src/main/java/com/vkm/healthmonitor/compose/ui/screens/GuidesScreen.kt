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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.json.JSONArray
import java.nio.charset.Charset

data class LocalGuide(val title: String, val description: String)


@Composable
fun GuidesScreen(nav: NavController) {
    val ctx = LocalContext.current
    val guides = remember { loadLocalGuides(ctx) }

    Column(Modifier.padding(16.dp)) {
        Text("Health Guides", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(guides) { g ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(g.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(g.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun loadLocalGuides(ctx: Context): List<LocalGuide> {
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

