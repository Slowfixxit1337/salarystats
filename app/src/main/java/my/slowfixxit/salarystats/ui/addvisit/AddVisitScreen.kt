package my.slowfixxit.salarystats.ui.addvisit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.slowfixxit.salarystats.domain.models.DefaultServiceTypes
import my.slowfixxit.salarystats.domain.models.ServiceType
import my.slowfixxit.salarystats.domain.models.Visit
import my.slowfixxit.salarystats.domain.models.VisitService
import my.slowfixxit.salarystats.ui.theme.Green400
import my.slowfixxit.salarystats.ui.theme.JetBrainsMono
import my.slowfixxit.salarystats.ui.theme.OnSurfaceMuted
import my.slowfixxit.salarystats.ui.theme.SurfaceVariant
import java.time.LocalDate

@Composable
fun AddVisitScreen(onVisitAdded: (Visit) -> Unit) {
    var clientName by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedServices by remember { mutableStateOf<Map<ServiceType, String>>(emptyMap()) }

    val totalClientPayment = selectedServices.values
        .mapNotNull { it.toDoubleOrNull() }
        .sum()
    val totalEarned = selectedServices.entries
        .mapNotNull { (type, amount) -> amount.toDoubleOrNull()?.times(type.percentage) }
        .sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                Text(
                    text = "> новый визит",
                    color = Green400,
                    fontFamily = JetBrainsMono,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("клиент", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    placeholder = {
                        Text("Иванова А.В.", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 14.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green400,
                        unfocusedBorderColor = SurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = Green400
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = JetBrainsMono, fontSize = 14.sp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("услуги", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(DefaultServiceTypes) { serviceType ->
                val isSelected = selectedServices.containsKey(serviceType)
                val amount = selectedServices[serviceType] ?: ""
                ServiceRow(
                    serviceType = serviceType,
                    isSelected = isSelected,
                    amount = amount,
                    onToggle = {
                        selectedServices = if (isSelected) {
                            selectedServices - serviceType
                        } else {
                            selectedServices + (serviceType to "")
                        }
                    },
                    onAmountChange = { newAmount ->
                        selectedServices = selectedServices + (serviceType to newAmount)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("заметка", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = {
                        Text("необязательно...", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 14.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green400,
                        unfocusedBorderColor = SurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = Green400
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = JetBrainsMono, fontSize = 14.sp),
                    minLines = 2,
                    maxLines = 4
                )
            }

            if (totalClientPayment > 0) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                Text("клиент платит", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                                Text("${totalClientPayment.toInt()} ₽", color = MaterialTheme.colorScheme.onBackground, fontFamily = JetBrainsMono, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("твой заработок", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 11.sp)
                                Text("+${totalEarned.toInt()} ₽", color = Green400, fontFamily = JetBrainsMono, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (clientName.isNotBlank() && selectedServices.isNotEmpty()) {
                    val visit = Visit(
                        clientName = clientName,
                        date = LocalDate.now(),
                        services = selectedServices.mapNotNull { (type, amount) ->
                            amount.toDoubleOrNull()?.let { VisitService(type, it) }
                        },
                        note = note
                    )
                    onVisitAdded(visit)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green400)
        ) {
            Text(
                text = "сохранить",
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = JetBrainsMono,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ServiceRow(
    serviceType: ServiceType,
    isSelected: Boolean,
    amount: String,
    onToggle: () -> Unit,
    onAmountChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Green400,
                uncheckedColor = OnSurfaceMuted,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Text(
            text = serviceType.name,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else OnSurfaceMuted,
            fontFamily = JetBrainsMono,
            fontSize = 14.sp,
            modifier = Modifier.width(100.dp)
        )
        if (isSelected) {
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = {
                    Text("сумма ₽", color = OnSurfaceMuted, fontFamily = JetBrainsMono, fontSize = 13.sp)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green400,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = Green400
                ),
                textStyle = LocalTextStyle.current.copy(fontFamily = JetBrainsMono, fontSize = 13.sp),
                singleLine = true
            )
            Text(
                text = "${(serviceType.percentage * 100).toInt()}%",
                color = OnSurfaceMuted,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp
            )
        }
    }
}