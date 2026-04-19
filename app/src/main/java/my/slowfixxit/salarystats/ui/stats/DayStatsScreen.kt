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
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import my.slowfixxit.salarystats.domain.models.Visit
import my.slowfixxit.salarystats.ui.theme.Green400
import my.slowfixxit.salarystats.ui.theme.JetBrainsMono
import my.slowfixxit.salarystats.ui.theme.OnSurfaceMuted
import my.slowfixxit.salarystats.ui.theme.SurfaceVariant
import java.time.format.DateTimeFormatter

@Composable
fun DayStatsScreen(visits: List<Visit>, onBack: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM")
    val fullFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    val byDay = visits
        .groupBy { it.date }
        .map { (date, v) ->
            Triple(date, v.sumOf { it.totalEarned }, v.size)
        }
        .sortedBy { it.first }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(byDay) {
        if (byDay.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries { series(*byDay.map { it.second.toFloat() }.toTypedArray()) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("< назад", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("> по дням", color = Green400, fontFamily = JetBrainsMono, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            if (byDay.isNotEmpty()) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = { _, x, _ ->
                                byDay.getOrNull(x.toInt())?.first?.format(formatter) ?: ""
                            }
                        )
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text("детали", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(byDay.sortedByDescending { it.first }) { (date, earned, count) ->
            Surface(
                color = SurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp, 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(date.format(fullFormatter), color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                        Text("$count визитов", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                    }
                    Text("+${earned.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}