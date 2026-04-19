package my.slowfixxit.salarystats.ui.detail

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
fun VisitDetailScreen(
    visit: Visit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("удалить визит?", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono)
            },
            text = {
                Text("это действие нельзя отменить", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("удалить", color = MaterialTheme.colorScheme.error, fontFamily = JetBrainsMono)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("отмена", color = OnSurfaceMuted, fontFamily = JetBrainsMono)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                // Назад
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("< назад", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    text = visit.clientName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = JetBrainsMono,
                    fontSize = 22.sp
                )
                Text(
                    text = visit.date.format(formatter),
                    color = OnSurfaceMuted,
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Итог
            item {
                Surface(
                    color = SurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("клиент заплатил", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                            Text("${visit.totalClientPayment.toInt()} ₽", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("твой заработок", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                            Text("+${visit.totalEarned.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 20.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Услуги
            item {
                Text("услуги", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(visit.services) { service ->
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
                            Text(service.serviceType.name, color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 14.sp)
                            Text("${(service.serviceType.percentage * 100).toInt()}% от суммы", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${service.clientPayment.toInt()} ₽", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
                            Text("+${service.earnedAmount.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 13.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Заметка
            if (visit.note.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("заметка", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = SurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = visit.note,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = JetBrainsMono,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Кнопка удалить
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green400),
                border = androidx.compose.foundation.BorderStroke(1.dp, Green400)
            ) {
                Text("изменить", fontFamily = JetBrainsMono, fontSize = 14.sp)
            }
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("удалить", color = MaterialTheme.colorScheme.onError, fontFamily = JetBrainsMono, fontSize = 14.sp)
            }
        }
    }
}