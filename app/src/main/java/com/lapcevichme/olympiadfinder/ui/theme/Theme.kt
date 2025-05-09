package com.lapcevichme.olympiadfinder.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

@Composable
fun OlympiadFinderTheme(
    dynamicTheme: Theme,
    appFont: AppFont = AppFont.DEFAULT,
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

    val typographyFontFamily = getFontFamily(appFont)
    val typography = createAppTypography(typographyFontFamily)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

private fun getFontFamily(appFont: AppFont): FontFamily {
    return when (appFont) {
        AppFont.DEFAULT -> FontFamily.Default // Используем стандартный шрифт платформы
        AppFont.SERIF -> FontFamily.Serif     // Используем шрифт с засечками платформы
        AppFont.MONOSPACE -> FontFamily.Monospace // Используем моноширинный шрифт платформы
        // TODO: Если будешь добавлять СВОИ шрифты из файлов (.ttf, .otf), нужно загружать их так:
        // AppFont.MY_CUSTOM_FONT -> FontFamily(Font(R.font.my_custom_font_regular), Font(R.font.my_custom_font_bold, FontWeight.Bold), ...)
    }
}

private fun createAppTypography(fontFamily: FontFamily): Typography {
    return Typography(
        // Применяем заданный fontFamily ко всем стандартным стилям текста Material 3
        // Ты можешь настроить FontWeight, fontSize, lineHeight и letterSpacing индивидуально
        // для каждого стиля, но базовая fontFamily будет общей.
        // Если у твоих кастомных шрифтов есть разные начертания (Regular, Bold и т.д. в разных файлах),
        // нужно будет определить FontFamily с несколькими Font() и их FontWeight.
        // Здесь просто применяем базовый fontFamily ко всем стилям для простоты.

        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),

        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),

        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}

@Composable
fun PreviewTheme(
    dynamicTheme: Theme = Theme.SYSTEM,
    appFont: AppFont = AppFont.DEFAULT,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    OlympiadFinderTheme(
        dynamicTheme = dynamicTheme,
        appFont = appFont,
        dynamicColor = dynamicColor,
        content = content
    )
}
