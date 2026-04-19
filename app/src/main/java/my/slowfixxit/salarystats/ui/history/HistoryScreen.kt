package my.slowfixxit.salarystats.ui.history

import androidx.compose.foundation.clickable
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
fun HistoryScreen(visits: List<Visit>, onVisitClick: (Visit) -> Unit) {
    val totalEarned = visits.sumOf { it.totalEarned }
    val formatter = DateTimeFormatter.ofPattern("dd MMM")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "апрель 2026",
                fontSize = 12.sp,
                color = Green400,
                fontFamily = JetBrainsMono,
                letterSpacing = 0.1.sp
            )
            Text(
                text = "${totalEarned.toInt()} ₽",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = JetBrainsMono
            )
            Text(
                text = "${visits.size} визитов",
                fontSize = 13.sp,
                color = OnSurfaceMuted,
                fontFamily = JetBrainsMono
            )
        }

        HorizontalDivider(color = SurfaceVariant)

        LazyColumn {
            items(visits) { visit ->
                VisitCard(
                    visit = visit,
                    formatter = formatter,
                    onClick = { onVisitClick(visit) }
                )
                HorizontalDivider(
                    color = SurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun VisitCard(visit: Visit, formatter: DateTimeFormatter, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(0.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceVariant
                ) {}
                Text(
                    text = visit.clientName.first().toString(),
                    color = Green400,
                    fontFamily = JetBrainsMono,
                    fontSize = 16.sp
                )
            }

            Column {
                Text(
                    text = visit.clientName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = JetBrainsMono,
                    fontSize = 14.sp
                )
                Text(
                    text = "${visit.date.format(formatter)} · ${visit.services.joinToString { it.serviceType.name }}",
                    color = OnSurfaceMuted,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${visit.totalEarned.toInt()} ₽",
                color = Green400,
                fontFamily = JetBrainsMono,
                fontSize = 14.sp
            )
            Text(
                text = "${visit.totalClientPayment.toInt()} ₽",
                color = OnSurfaceMuted,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp
            )
        }
    }
}