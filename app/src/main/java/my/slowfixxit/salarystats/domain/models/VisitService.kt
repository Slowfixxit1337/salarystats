package my.slowfixxit.salarystats.domain.models

data class VisitService(
    val serviceType: ServiceType,
    val clientPayment: Double
) {
    val earnedAmount: Double get() = clientPayment * serviceType.percentage
}