package my.slowfixxit.salarystats.navigation

sealed class Screen(val route: String) {
    object History : Screen("history")
    object Stats : Screen("stats")
    object AddVisit : Screen("add_visit")
    object VisitDetail : Screen("visit_detail/{visitId}") {
        fun createRoute(visitId: String) = "visit_detail/$visitId"
    }
    object EditVisit : Screen("edit_visit/{visitId}") {
        fun createRoute(visitId: String) = "edit_visit/$visitId"
    }
    object ServiceStats : Screen("service_stats")
    object ClientStats : Screen("client_stats")
    object DayStats : Screen("day_stats")
}