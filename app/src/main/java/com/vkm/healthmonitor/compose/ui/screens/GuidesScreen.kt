package com.vkm.healthmonitor.compose.ui.screens


//data class LocalGuide(val title: String, val description: String)


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
//

//private fun loadLocalGuides(ctx: Context): List<LocalGuide> {
//    return try {
//        val input = ctx.assets.open("how_to_use_app.json")
//        val json = input.readBytes().toString(Charset.defaultCharset())
//        val arr = JSONArray(json)
//        List(arr.length()) { idx ->
//            val obj = arr.getJSONObject(idx)
//            LocalGuide(
//                title = obj.optString("title"),
//                description = obj.optString("description")
//            )
//        }
//    } catch (e: Exception) {
//        emptyList()
//    }
//}


//@Composable
//fun GuidesScreen(navController: NavController?) {
//    val ctx = LocalContext.current
//    val guides = remember { loadLocalGuides(ctx) }
//    var selectedGuide by remember { mutableStateOf<LocalGuide?>(null) }
//    var showDialog by remember { mutableStateOf(false) }
//
//    val healthTips = remember { loadHealthTips(ctx) }
//    Column(Modifier.padding(16.dp)) {
//        Text("Health Tips", style = MaterialTheme.typography.titleLarge)
//        Spacer(Modifier.height(12.dp))
//
//        // Static health tips
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
//
//        Spacer(Modifier.height(24.dp))
//        Text("How to Use the App", style = MaterialTheme.typography.titleLarge)
//        Spacer(Modifier.height(12.dp))
//
//        // Dynamic guides from JSON
//        if (guides.isEmpty()) {
//            Text("No guides available. Please add health_plans.json in assets.", style = MaterialTheme.typography.bodyMedium)
//        } else {
//            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                items(guides) { g ->
//                    Card(Modifier.fillMaxWidth()) {
//                        Column(Modifier.padding(12.dp)) {
//                            Text(g.title, style = MaterialTheme.typography.titleMedium)
//                            Spacer(Modifier.height(4.dp))
//                            Text(
//                                text = if (g.description.length > 200) g.description.take(200) + "..." else g.description,
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Spacer(Modifier.height(6.dp))
//                            TextButton (onClick = {
//                                selectedGuide = g
//                                showDialog = true
//                            }) {
//                                Text("Read more")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    if (showDialog && selectedGuide != null) {
//        AlertDialog (
//            onDismissRequest = { showDialog = false; selectedGuide = null },
//            confirmButton = {
//                TextButton(onClick = { showDialog = false; selectedGuide = null }) {
//                    Text("Close")
//                }
//            },
//            title = { Text(selectedGuide!!.title) },
//            text = { Text(selectedGuide!!.description) }
//        )
//    }
//}


//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun GuidesScreen(navController: NavController?) {
//    val ctx = LocalContext.current
//    val guides = remember { loadLocalGuides(ctx) }
//    var selectedGuide by remember { mutableStateOf<LocalGuide?>(null) }
//    var showDialog by remember { mutableStateOf(false) }
//    val healthTips = remember { loadHealthTips(ctx) }
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        // Health Tips header
//        stickyHeader {
//            Text(
//                "Health Tips",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.background)
//                    .padding(vertical = 8.dp)
//            )
//        }
//
//        // Health Tips items
//        items(healthTips) { g ->
//            Card(Modifier.fillMaxWidth()) {
//                Column(Modifier.padding(12.dp)) {
//                    Text(g.title, style = MaterialTheme.typography.titleMedium)
//                    Spacer(Modifier.height(4.dp))
//                    Text(g.description, style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//        }
//
//        // Spacer before next section
//        item { Spacer(Modifier.height(24.dp)) }
//
//        // How to Use header
//        stickyHeader {
//            Text(
//                "How to Use the App",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.background)
//                    .padding(vertical = 8.dp)
//            )
//        }
//
//        // Dynamic guides from JSON
//        if (guides.isEmpty()) {
//            item {
//                Text(
//                    "No guides available. Please add health_plans.json in assets.",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        } else {
//            items(guides) { g ->
//                Card(Modifier.fillMaxWidth().padding(12.dp)) {
//                    Column(Modifier.padding(12.dp)) {
//                        Text(g.title, style = MaterialTheme.typography.titleMedium)
//                        Spacer(Modifier.height(4.dp))
//                        Text(
//                            text = if (g.description.length > 200) g.description.take(200) + "..." else g.description,
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                        Spacer(Modifier.height(6.dp))
//                        TextButton(onClick = {
//                            selectedGuide = g
//                            showDialog = true
//                        }) {
//                            Text("Read more")
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    // Dialog for "Read more"
//    if (showDialog && selectedGuide != null) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false; selectedGuide = null },
//            confirmButton = {
//                TextButton(onClick = { showDialog = false; selectedGuide = null }) {
//                    Text("Close")
//                }
//            },
//            title = { Text(selectedGuide!!.title) },
//            text = { Text(selectedGuide!!.description) }
//        )
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun GuidesScreen(navController: NavController?) {
//    val ctx = LocalContext.current
//    val guides = remember { loadLocalGuides(ctx) }
//    var selectedGuide by remember { mutableStateOf<LocalGuide?>(null) }
//    var showDialog by remember { mutableStateOf(false) }
//
//    val healthTips = remember { loadHealthTips(ctx) }
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//        contentPadding = PaddingValues(bottom = 32.dp) // ✅ ensures last item not cut
//    ) {
//        // Health Tips header
//        stickyHeader {
//            Text(
//                "Health Tips",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.background)
//                    .padding(vertical = 8.dp)
//            )
//        }
//
//        // Health Tips items
//        items(healthTips) { g ->
//            Card(Modifier.fillMaxWidth()) {
//                Column(Modifier.padding(12.dp)) {
//                    Text(g.title, style = MaterialTheme.typography.titleMedium)
//                    Spacer(Modifier.height(4.dp))
//                    Text(g.description, style = MaterialTheme.typography.bodyMedium)
//                }
//            }
//        }
//
//        // Spacer before next section
//        item { Spacer(Modifier.height(24.dp)) }
//
//        // How to Use header
//        stickyHeader {
//            Text(
//                "How to Use the App",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.background)
//                    .padding(vertical = 8.dp)
//            )
//        }
//
//        // Dynamic guides from JSON
//        if (guides.isEmpty()) {
//            item {
//                Text(
//                    "No guides available. Please add health_plans.json in assets.",
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        } else {
//            items(guides) { g ->
//                Card(Modifier.fillMaxWidth()) {
//                    Column(Modifier.padding(12.dp)) {
//                        Text(g.title, style = MaterialTheme.typography.titleMedium)
//                        Spacer(Modifier.height(4.dp))
//                        Text(
//                            text = if (g.description.length > 200) g.description.take(200) + "..." else g.description,
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                        Spacer(Modifier.height(6.dp))
//                        TextButton(onClick = {
//                            selectedGuide = g
//                            showDialog = true
//                        }) {
//                            Text("Read more")
//                        }
//                    }
//                }
//            }
//        }
//
//        // ✅ Add safe spacer at the very bottom
//        item { Spacer(modifier = Modifier.height(64.dp)) }
//    }
//
//    // Dialog for "Read more"
//    if (showDialog && selectedGuide != null) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false; selectedGuide = null },
//            confirmButton = {
//                TextButton(onClick = { showDialog = false; selectedGuide = null }) {
//                    Text("Close")
//                }
//            },
//            title = { Text(selectedGuide!!.title) },
//            text = { Text(selectedGuide!!.description) }
//        )
//    }
//}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun GuidesScreen(navController: NavController?) {
//    val ctx = LocalContext.current
//    val guidesBundle = remember { loadGuidesBundle(ctx) }
//
//    var selectedGuide by remember { mutableStateOf<LocalGuide?>(null) }
//    var showDialog by remember { mutableStateOf(false) }
//    val healthTips = remember { loadHealthTips(ctx) }
//    val scrollState = rememberScrollState()
//    LazyColumn (
//        modifier = Modifier
//            .fillMaxWidth().wrapContentHeight(unbounded = false)
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//    ) {
//        // Health Tips (static, always available)
//        item {
//            GuideSection("Health Tips", healthTips) { g ->
//                selectedGuide = g; showDialog = true
//            }
//        }
//
//        item {
//            Spacer(Modifier.height(24.dp))
//        }
//
//        // How to Use (JSON)
//       item {
//           GuideSection("How to Use the App", guidesBundle.howToUse) { g ->
//            selectedGuide = g;
//               showDialog = true
//        }
//}
//        item {
//            Spacer(Modifier.height(24.dp))
//        }
//
//        // Diet Plans (JSON)
//        item {
//            GuideSection("Diet Plans", guidesBundle.dietPlans) { g ->
//            selectedGuide = g; showDialog = true
//        }
//        }
//
//        item {
//            Spacer(Modifier.height(24.dp))
//        }
//
//        // Workout Tips (JSON)
//        item {
//            GuideSection("Workout Tips", guidesBundle.workoutTips) { g ->
//            selectedGuide = g; showDialog = true
//        }
//        }
//
//        item {
//            Spacer(Modifier.height(64.dp))
//        }
//    }
//
//    if (showDialog && selectedGuide != null) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false; selectedGuide = null },
//            confirmButton = {
//                TextButton(onClick = { showDialog = false; selectedGuide = null }) {
//                    Text("Close")
//                }
//            },
//            title = { Text(selectedGuide!!.title) },
//            text = { Text(selectedGuide!!.description) }
//        )
//    }
//}


import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

// Your data model
data class LocalGuide(
    val title: String,
    val description: String
)

data class GuidesBundle(
    val howToUse: List<LocalGuide>,
    val standards: List<LocalGuide>,
    val dietPlans: List<LocalGuide>,
    val workoutTips: List<LocalGuide>
)

// Loading functions (you already have these)
fun loadGuidesBundle(ctx: Context): GuidesBundle {
    return try {
        val input = ctx.assets.open("health_plans.json")
        val json = input.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val obj = JSONObject(json)

        fun parseArray(arr: JSONArray?): List<LocalGuide> {
            if (arr == null) return emptyList()
            return List(arr.length()) { idx ->
                val item = arr.getJSONObject(idx)
                LocalGuide(
                    title = item.optString("title", "Untitled"),
                    description = item.optString("description", "")
                )
            }
        }

        GuidesBundle(
            standards = parseArray(obj.optJSONArray("health_standards")),
            howToUse = parseArray(obj.optJSONArray("how_to_use")),
            dietPlans = parseArray(obj.optJSONArray("diet_plans")),
            workoutTips = parseArray(obj.optJSONArray("workout_tips"))
        )
    } catch (e: Exception) {
        e.printStackTrace()
        GuidesBundle(emptyList(), emptyList(), emptyList(), emptyList())
    }
}

@Composable
fun GuidesScreen(navController: NavController?) {
    val ctx = LocalContext.current
    val guidesBundle = remember { loadGuidesBundle(ctx) }
    val healthTips = remember { loadHealthTips(ctx) }

    var selectedGuide by remember { mutableStateOf<LocalGuide?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (healthTips.isNotEmpty()) {
            GuideSection(
                title = "Health Tips",
                guides = healthTips,
                onReadMore = { g -> selectedGuide = g; showDialog = true }
            )
            item { Spacer(Modifier.height(24.dp)) }
        }

        if (guidesBundle.howToUse.isNotEmpty()) {
            GuideSection(
                title = "How to Use the App",
                guides = guidesBundle.howToUse,
                onReadMore = { g -> selectedGuide = g; showDialog = true }
            )
            item { Spacer(Modifier.height(24.dp)) }
        }

        if (guidesBundle.standards.isNotEmpty()) {
            GuideSection(
                title = "Health Stnadards",
                guides = guidesBundle.standards,
                onReadMore = { g -> selectedGuide = g; showDialog = true }
            )
            item { Spacer(Modifier.height(24.dp)) }
        }

        if (guidesBundle.dietPlans.isNotEmpty()) {
            GuideSection(
                title = "Diet Plans",
                guides = guidesBundle.dietPlans,
                onReadMore = { g -> selectedGuide = g; showDialog = true }
            )
            item { Spacer(Modifier.height(24.dp)) }
        }

        if (guidesBundle.workoutTips.isNotEmpty()) {
            GuideSection(
                title = "Workout Tips",
                guides = guidesBundle.workoutTips,
                onReadMore = { g -> selectedGuide = g; showDialog = true }
            )
            item { Spacer(Modifier.height(64.dp)) }
        }
    }

    if (showDialog && selectedGuide != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false; selectedGuide = null },
            confirmButton = {
                TextButton(onClick = { showDialog = false; selectedGuide = null }) {
                    Text(text = "Close")
                }
            },
            title = {
                Text(text = selectedGuide!!.title)
            },
            text = {
                // Make the dialog content scrollable if long
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = selectedGuide!!.description)
                }
            }
        )
    }
}

/**
 * Helper extension on LazyListScope to emit a section
 * with sticky header + items. Avoids nested scrollables.
 */
@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.GuideSection(
    title: String,
    guides: List<LocalGuide>,
    onReadMore: (LocalGuide) -> Unit
) {
    stickyHeader {
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.background(color = Color.Green, shape = RoundedCornerShape(24))
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            )
        }
    }
    items(guides) { g ->
        GuideCard(g = g, onReadMore = { onReadMore(g) })
    }
}

val standards = listOf(
    LocalGuide("Pulse (Heart Rate)", "Normal 60–100 BPM. Use smartwatch, fitness band, or fingertip method."),
    LocalGuide("Blood Pressure", "Normal ~100–129 systolic / 60–85 diastolic mmHg. Use digital BP monitor."),
    LocalGuide("Temperature", "Normal 36.5–37.5 °C. Measured using a digital thermometer."),
    LocalGuide("SpO₂", "Normal ≥95%. Measured using a fingertip pulse oximeter."),
    LocalGuide("Hydration", "Daily goal: 2000–3000 ml. The app tracks intake and prevents over-hydration."),
    LocalGuide("BMI", "Calculated using height & weight. Categories: underweight, normal, overweight, obese.")
)
@Composable
fun GuideCard(g: LocalGuide, onReadMore: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReadMore),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(text = g.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            val desc = g.description
            if (desc.length <= 200) {
                Text(text = desc, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    text = desc.take(200) + "...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(onClick = onReadMore) {
                    Text(text = "Read more")
                }
            }
        }
    }
}

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
