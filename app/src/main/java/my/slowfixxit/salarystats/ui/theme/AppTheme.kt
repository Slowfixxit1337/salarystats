package my.slowfixxit.salarystats.ui.theme

import my.slowfixxit.salarystats.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val Green400 = Color(0xFF4ADE80)
val Green900 = Color(0xFF14532D)
val Background = Color(0xFF111111)
val Surface = Color(0xFF1A1A1A)
val SurfaceVariant = Color(0xFF222222)
val OnSurface = Color(0xFFE8E8E8)
val OnSurfaceMuted = Color(0xFF555555)

private val DarkColorScheme = darkColorScheme(
    primary = Green400,
    onPrimary = Color(0xFF111111),
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrainsmono_regular, FontWeight.Normal)
)

@Composable
fun SalaryStatsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}