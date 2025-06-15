package com.lapcevichme.olympiadfinder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lapcevichme.olympiadfinder.domain.model.AppFont

/**
 * Этот файл содержит функции для работы с типографикой приложения,
 * в частности для определения и создания объекта [Typography] Material 3,
 * основанного на выбранном пользователем шрифте.
 *
 * Он отделен от основного файла темы ([Theme.kt]) для лучшего разделения
 * ответственности и модульности кода.
 */

/**
 * Возвращает объект [FontFamily], соответствующий выбранному [AppFont].
 *
 * Эта функция является точкой расширения для добавления пользовательских шрифтов
 * из файлов ресурсов (например, .ttf, .otf).
 *
 * @param appFont Выбранный шрифт приложения из перечисления [AppFont].
 * @return Объект [FontFamily], который будет использоваться для применения шрифтов.
 */
fun getAppFontFamily(appFont: AppFont): FontFamily {
    return when (appFont) {
        AppFont.DEFAULT -> FontFamily.Default
        AppFont.SERIF -> FontFamily.Serif
        AppFont.MONOSPACE -> FontFamily.Monospace
        // TODO: Если будешь добавлять СВОИ шрифты из файлов (.ttf, .otf), нужно загружать их так:
        // AppFont.MY_CUSTOM_FONT -> FontFamily(Font(R.font.my_custom_font_regular), Font(R.font.my_custom_font_bold, FontWeight.Bold), ...)
    }
}

/**
 * Создает и возвращает объект [Typography] Material 3,
 * применяя заданное [fontFamily] ко всем стандартным стилям текста.
 *
 * Этот объект [Typography] используется в [MaterialTheme] для обеспечения
 * единообразного внешнего вида текста по всему приложению.
 * Каждый стиль текста ([displayLarge], [bodyMedium] и т.д.) имеет
 * предопределенные размеры, межстрочные и межбуквенные интервалы,
 * к которым применяется базовое семейство шрифтов.
 *
 * @param fontFamily [FontFamily], которое будет применено ко всем стилям текста в [Typography].
 * @return Настроенный объект [Typography] для использования в [MaterialTheme].
 */
fun createAppTypography(fontFamily: FontFamily): Typography {
    return Typography(
        // Применяем заданный fontFamily ко всем стандартным стилям текста Material 3
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