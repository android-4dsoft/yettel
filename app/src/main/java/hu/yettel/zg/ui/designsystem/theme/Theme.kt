package hu.yettel.zg.ui.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
    lightColorScheme(
        primary = Lime,
        onPrimary = Black,
        secondary = DarkBlue,
        onSecondary = White,
        error = Orange,
        onError = White,
        background = White,
        onBackground = Black,
        surface = LightGray,
        onSurface = DarkBlue,
        onTertiaryContainer = DarkGray,
    )

@Composable
fun YettelZGTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = YettelShapes,
        content = content,
    )
}
