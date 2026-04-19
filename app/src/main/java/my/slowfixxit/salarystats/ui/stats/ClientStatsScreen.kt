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
import java.time.format.DateTimeFormatter

@Composable
fun ClientStatsScreen(visits: List<Visit>, onBack: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val byClient = visits
        .groupBy { it.clientName }
        .map { (name, clientVisits) ->
            data class ClientStat(
                val name: String,
                val totalEarned: Double,
                val visitCount: Int,
                val lastVisit: java.time.LocalDate,
                val avgCheck: Double
            )
            ClientStat(
                name = name,
                totalEarned = clientVisits.sumOf { it.totalEarned },
                visitCount = clientVisits.size,
                lastVisit = clientVisits.maxOf { it.date },
                avgCheck = clientVisits.sumOf { it.totalClientPayment } / clientVisits.size
            )
        }
        .sortedByDescending { it.totalEarned }

    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("< назад", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("> по клиентам", color = Green400, fontFamily = JetBrainsMono, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
        }

        items(byClient) { client ->
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
                        Text(client.name, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 15.sp)
                        Text("+${client.totalEarned.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column {
                            Text("визитов", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
                            Text("${client.visitCount}", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        }
                        Column {
                            Text("ср. чек", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
                            Text("${client.avgCheck.toInt()} ₽", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        }
                        Column {
                            Text("последний визит", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
                            Text(client.lastVisit.format(formatter), color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}