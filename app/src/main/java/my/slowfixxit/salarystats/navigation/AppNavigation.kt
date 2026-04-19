package my.slowfixxit.salarystats.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import my.slowfixxit.salarystats.ui.addvisit.AddVisitScreen
import my.slowfixxit.salarystats.ui.detail.VisitDetailScreen
import my.slowfixxit.salarystats.ui.edit.EditVisitScreen
import my.slowfixxit.salarystats.ui.history.HistoryScreen
import my.slowfixxit.salarystats.ui.history.HistoryViewModel
import my.slowfixxit.salarystats.ui.stats.ClientStatsScreen
import my.slowfixxit.salarystats.ui.stats.DayStatsScreen
import my.slowfixxit.salarystats.ui.stats.ServiceStatsScreen
import my.slowfixxit.salarystats.ui.stats.StatsScreen
import my.slowfixxit.salarystats.ui.theme.Green400
import my.slowfixxit.salarystats.ui.theme.JetBrainsMono
import my.slowfixxit.salarystats.ui.theme.OnSurfaceMuted
import my.slowfixxit.salarystats.ui.theme.SurfaceVariant

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: HistoryViewModel = viewModel()
    val visits by viewModel.visits.collectAsState()

    data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)

    val items = listOf(
        NavItem(Screen.History, "история", Icons.Filled.History),
        NavItem(Screen.Stats, "статистика", Icons.Filled.BarChart),
        NavItem(Screen.AddVisit, "добавить", Icons.Filled.Add),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val selected = currentRoute == item.screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) Green400 else OnSurfaceMuted
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                color = if (selected) Green400 else OnSurfaceMuted,
                                fontFamily = JetBrainsMono,
                                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.History.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
            }
        ) {
            composable(Screen.History.route) {
                HistoryScreen(
                    visits = visits,
                    onVisitClick = { visit ->
                        navController.navigate(Screen.VisitDetail.createRoute(visit.id))
                    }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(
                    visits = visits,
                    onServiceStatsClick = { navController.navigate(Screen.ServiceStats.route) },
                    onClientStatsClick = { navController.navigate(Screen.ClientStats.route) },
                    onDayStatsClick = { navController.navigate(Screen.DayStats.route) }
                )
            }
            composable(Screen.AddVisit.route) {
                AddVisitScreen(onVisitAdded = { visit ->
                    viewModel.addVisit(visit)
                    navController.navigate(Screen.History.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                })
            }
            composable(Screen.VisitDetail.route) { backStackEntry ->
                val visitId = backStackEntry.arguments?.getString("visitId") ?: return@composable
                val visit = visits.find { it.id == visitId } ?: return@composable
                VisitDetailScreen(
                    visit = visit,
                    onEdit = { navController.navigate(Screen.EditVisit.createRoute(visitId)) },
                    onDelete = {
                        viewModel.removeVisit(visitId)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.EditVisit.route) { backStackEntry ->
                val visitId = backStackEntry.arguments?.getString("visitId") ?: return@composable
                val visit = visits.find { it.id == visitId } ?: return@composable
                EditVisitScreen(
                    visit = visit,
                    onVisitUpdated = { updated ->
                        viewModel.updateVisit(updated)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ServiceStats.route) {
                ServiceStatsScreen(visits = visits, onBack = { navController.popBackStack() })
            }
            composable(Screen.ClientStats.route) {
                ClientStatsScreen(visits = visits, onBack = { navController.popBackStack() })
            }
            composable(Screen.DayStats.route) {
                DayStatsScreen(visits = visits, onBack = { navController.popBackStack() })
            }
        }
    }
}