package my.slowfixxit.salarystats.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.slowfixxit.salarystats.domain.models.Visit
import my.slowfixxit.salarystats.ui.theme.Green400
import my.slowfixxit.salarystats.ui.theme.JetBrainsMono
import my.slowfixxit.salarystats.ui.theme.OnSurfaceMuted
import my.slowfixxit.salarystats.ui.theme.SurfaceVariant

@Composable
fun ServiceStatsScreen(visits: List<Visit>, onBack: () -> Unit) {
    val byService = visits
        .flatMap { it.services }
        .groupBy { it.serviceType }
        .map { (type, services) ->
            Triple(type, services.sumOf { it.earnedAmount }, services.size)
        }
        .sortedByDescending { it.second }

    val maxEarned = byService.maxOfOrNull { it.second } ?: 1.0

    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("< назад", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("> по услугам", color = Green400, fontFamily = JetBrainsMono, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
        }

        items(byService) { (type, earned, count) ->
            Surface(
                color = SurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(type.name, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 16.sp)
                        Text("+${earned.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (earned / maxEarned).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = Green400,
                        trackColor = MaterialTheme.colorScheme.background
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column {
                            Text("визитов", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
                            Text("$count", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        }
                        Column {
                            Text("процент", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
                            Text("${(type.percentage * 100).toInt()}%", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        }
                        Column {
                            Text("ср. чек", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
                            val avgCheck = if (count > 0) earned / count else 0.0
                            Text("${avgCheck.toInt()} ₽", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}