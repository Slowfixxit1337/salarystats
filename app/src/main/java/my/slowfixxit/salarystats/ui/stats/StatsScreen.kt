package my.slowfixxit.salarystats.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer.ColumnProvider
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import my.slowfixxit.salarystats.domain.models.Visit
import my.slowfixxit.salarystats.ui.theme.Green400
import my.slowfixxit.salarystats.ui.theme.JetBrainsMono
import my.slowfixxit.salarystats.ui.theme.OnSurfaceMuted
import my.slowfixxit.salarystats.ui.theme.SurfaceVariant
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    visits: List<Visit>,
    onServiceStatsClick: () -> Unit,
    onClientStatsClick: () -> Unit,
    onDayStatsClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    val selectedStart = dateRangePickerState.selectedStartDateMillis?.toLocalDate()
    val selectedEnd = dateRangePickerState.selectedEndDateMillis?.toLocalDate()
    val rangeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val dayFormatter = DateTimeFormatter.ofPattern("dd.MM")

    val filteredVisits = visits.filter { visit ->
        when {
            selectedStart != null && selectedEnd != null ->
                !visit.date.isBefore(selectedStart) && !visit.date.isAfter(selectedEnd)
            selectedStart != null -> !visit.date.isBefore(selectedStart)
            else -> true
        }
    }

    val totalEarned = filteredVisits.sumOf { it.totalEarned }
    val totalClientPayment = filteredVisits.sumOf { it.totalClientPayment }
    val avgCheck = if (filteredVisits.isNotEmpty()) totalClientPayment / filteredVisits.size else 0.0

    val byService = filteredVisits
        .flatMap { it.services }
        .groupBy { it.serviceType.name }
        .map { (name, services) -> Triple(name, services.sumOf { it.earnedAmount }, services.size) }
        .sortedByDescending { it.second }

    val byClient = filteredVisits
        .groupBy { it.clientName }
        .map { (name, v) -> Pair(name, v.sumOf { it.totalEarned }) }
        .sortedByDescending { it.second }
        .take(5)

    val byDay = filteredVisits
        .groupBy { it.date }
        .map { (date, v) -> Pair(date, v.sumOf { it.totalEarned }) }
        .sortedBy { it.first }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(byDay) {
        if (byDay.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries { series(*byDay.map { it.second.toFloat() }.toTypedArray()) }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("готово", color = Green400, fontFamily = JetBrainsMono)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dateRangePickerState.setSelection(null, null)
                    showDatePicker = false
                }) {
                    Text("сбросить", color = OnSurfaceMuted, fontFamily = JetBrainsMono)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        "выбери период",
                        color = OnSurfaceMuted,
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )
                },
                headline = {
                    val headline = when {
                        selectedStart != null && selectedEnd != null ->
                            "${selectedStart.format(rangeFormatter)} — ${selectedEnd.format(rangeFormatter)}"
                        selectedStart != null -> "с ${selectedStart.format(rangeFormatter)}"
                        else -> "выбери даты"
                    }
                    Text(
                        headline,
                        color = Green400,
                        fontFamily = JetBrainsMono,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )
                },
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedDayContainerColor = Green400,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayContentColor = Green400,
                    todayDateBorderColor = Green400,
                    dayInSelectionRangeContainerColor = SurfaceVariant,
                    dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier.height(500.dp)
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Text("> статистика", color = Green400, fontFamily = JetBrainsMono, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showDatePicker = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green400),
                border = BorderStroke(1.dp, if (selectedStart != null) Green400 else SurfaceVariant)
            ) {
                val label = when {
                    selectedStart != null && selectedEnd != null ->
                        "${selectedStart.format(rangeFormatter)} — ${selectedEnd.format(rangeFormatter)}"
                    selectedStart != null -> "с ${selectedStart.format(rangeFormatter)}"
                    else -> "все время"
                }
                Text(label, fontFamily = JetBrainsMono, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text("итого", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("заработок", "${totalEarned.toInt()} ₽", Modifier.weight(1f))
                StatCard("визитов", "${filteredVisits.size}", Modifier.weight(1f))
                StatCard("ср. чек", "${avgCheck.toInt()} ₽", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionCard(
                title = "заработок по дням",
                subtitle = "нажми для деталей",
                onClick = onDayStatsClick
            ) {
                if (byDay.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberColumnCartesianLayer(),
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                valueFormatter = { _, x, _ ->
                                    byDay.getOrNull(x.toInt())?.first?.format(dayFormatter) ?: ""
                                }
                            )
                        ),
                        modelProducer = modelProducer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
                    Text("нет данных", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SectionCard(
                title = "по услугам",
                subtitle = "нажми для деталей",
                onClick = onServiceStatsClick
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                val maxEarned = byService.maxOfOrNull { it.second } ?: 1.0
                byService.forEach { (name, earned, count) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 13.sp)
                            Text("+${earned.toInt()} ₽ · $count шт", color = Green400, fontFamily = JetBrainsMono, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (earned / maxEarned).toFloat() },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = Green400,
                            trackColor = SurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SectionCard(
                title = "топ клиентов",
                subtitle = "нажми для деталей",
                onClick = onClientStatsClick
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                byClient.forEachIndexed { index, (name, earned) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "#${index + 1}",
                                color = if (index == 0) Green400 else OnSurfaceMuted,
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                modifier = Modifier.width(28.dp)
                            )
                            Text(name, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 13.sp)
                        }
                        Text("+${earned.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = SurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                Text(subtitle, color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
            }
            content()
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = SurfaceVariant, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
        }
    }
}

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()