package my.slowfixxit.salarystats.domain.models

data class ServiceType(
    val id: String,
    val name: String,
    val percentage: Double
)

val DefaultServiceTypes = listOf(
    ServiceType("1", "Массаж", 0.35),
    ServiceType("2", "СПА", 0.25),
    ServiceType("3", "Доп. услуга", 0.30)
)