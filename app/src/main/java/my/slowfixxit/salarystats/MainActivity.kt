package my.slowfixxit.salarystats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import my.slowfixxit.salarystats.navigation.AppNavigation
import my.slowfixxit.salarystats.ui.theme.SalaryStatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalaryStatsTheme {
                AppNavigation()
            }
        }

    }
}