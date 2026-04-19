package my.slowfixxit.salarystats.domain.models

import java.time.LocalDate
import java.util.UUID

data class Visit(
    val id: String = UUID.randomUUID().toString(),
    val clientName: String,
    val date: LocalDate = LocalDate.now(),
    val services: List<VisitService>,
    val note: String = ""
) {
    val totalClientPayment: Double get() = services.sumOf { it.clientPayment }
    val totalEarned: Double get() = services.sumOf { it.earnedAmount }
}