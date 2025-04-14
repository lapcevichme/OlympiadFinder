package com.lapcevichme.olympiadfinder.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lapcevichme.olympiadfinder.domain.model.Theme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun OlympiadFinderTheme(
    dynamicTheme: Theme,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 1. Определяем, используется ли системная темная тема СЕЙЧАС (вызываем вне SideEffect)
    val systemIsDark = isSystemInDarkTheme()

    // 2. Определяем базовую цветовую схему (до применения динамических цветов)
    val baseColorScheme = when (dynamicTheme) {
        Theme.LIGHT -> LightColorScheme
        Theme.DARK -> DarkColorScheme
        Theme.SYSTEM -> if (systemIsDark) DarkColorScheme else LightColorScheme
    }

    // 3. Применяем динамические цвета поверх базовой схемы, если возможно и включено
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (baseColorScheme == DarkColorScheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
            context
        )
    } else {
        baseColorScheme
    }

    // 4. Определяем, какой стиль нужен для иконок статус-бара (true = светлый фон -> темные иконки)
    val useLightStatusBarIcons = when (dynamicTheme) {
        Theme.LIGHT -> true
        Theme.DARK -> false
        Theme.SYSTEM -> !systemIsDark
    }

    // 5. Применяем стиль иконок статус-бара через SideEffect
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                useLightStatusBarIcons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}