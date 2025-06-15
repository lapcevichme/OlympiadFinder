package com.lapcevichme.olympiadfinder.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.lapcevichme.olympiadfinder.domain.model.AppFont
import com.lapcevichme.olympiadfinder.domain.model.Theme

/**
 * Этот файл определяет основные Composable-функции для применения темы Material 3
 * к приложению. Он управляет выбором цветовой схемы (светлой/темной, динамической),
 * применением выбранных шрифтов и настройкой статус-бара.
 */

/**
 * Цветовая схема для темной темы Material 3.
 * Определяет набор цветов для различных ролей в UI (primary, secondary, background, surface и т.д.),
 * используемых по умолчанию Material 3 компонентами, когда активирована темная тема.
 * Цвета определены в [Color.kt].
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

/**
 * Цветовая схема для светлой темы Material 3.
 * Определяет набор цветов для различных ролей в UI (primary, secondary, background, surface и т.д.),
 * используемых по умолчанию Material 3 компонентами, когда активирована светлая тема.
 * Цвета определены в [Color.kt].
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

/**
 * Главная Composable-функция для применения темы Material 3 к приложению.
 *
 * Эта функция оборачивает все содержимое приложения, предоставляя ему
 * согласованную цветовую схему, типографику и другие стили Material 3.
 * Она также управляет поведением статус-бара и применением динамических цветов.
 *
 * @param dynamicTheme Выбранная тема приложения: [Theme.SYSTEM], [Theme.LIGHT] или [Theme.DARK].
 * Определяет базовую цветовую схему.
 * @param appFont Выбранный шрифт для всего приложения, используемый для настройки типографики.
 * По умолчанию [AppFont.DEFAULT].
 * @param dynamicColor Флаг, указывающий, следует ли использовать динамические цвета (Material You)
 * на устройствах с Android 12 (API 31) и выше.
 * Если `true` и устройство поддерживает, динамические цвета будут применены
 * поверх базовой цветовой схемы. По умолчанию `true`.
 * @param content Содержимое Composable, к которому применяется тема.
 */
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

    val typographyFontFamily = getAppFontFamily(appFont)
    val typography = createAppTypography(typographyFontFamily)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}


/**
 * Вспомогательная Composable-функция для использования в `@Preview` аннотациях.
 *
 * Оборачивает содержимое превью в [OlympiadFinderTheme] с заданными параметрами,
 * а также в [Surface] с цветом фона темы. Это обеспечивает корректный рендеринг
 * компонентов Material 3 в различных темах и шрифтах при просмотре превью,
 * имитируя реальное окружение приложения.
 *
 * @param dynamicTheme Тема для превью ([Theme.SYSTEM], [Theme.LIGHT], [Theme.DARK]).
 * По умолчанию [Theme.SYSTEM].
 * @param appFont Шрифт для превью. По умолчанию [AppFont.DEFAULT].
 * @param dynamicColor Флаг для включения динамических цветов в превью.
 * По умолчанию `false` для предсказуемости и независимости от устройства превью.
 * @param content Содержимое Composable, которое будет отображено в превью.
 */
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
        content = {
            Surface(color = MaterialTheme.colorScheme.background) {
                content()
            }
        }
    )
}
