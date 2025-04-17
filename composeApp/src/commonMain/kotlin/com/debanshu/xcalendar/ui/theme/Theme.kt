package com.debanshu.xcalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf


val LocalDimensions = staticCompositionLocalOf { Dimensions }
val LocalCalendarColors = staticCompositionLocalOf { LightColorScheme }
val LocalTypography = staticCompositionLocalOf { Typography }
val LocalShapes = staticCompositionLocalOf { AppShapes }


/**
 * Main theme composable for the XCalendar app
 *
 * @param darkTheme Whether to use dark theme, defaults to system setting
 * @param content The content to be themed
 */
@Composable
fun XCalendarTheme(
    shapes: Shapes = XCalendarTheme.shapes,
    typography: Typography = XCalendarTheme.typography,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    CompositionLocalProvider(
        LocalDimensions provides Dimensions,
        LocalCalendarColors provides colorScheme,
        LocalTypography provides typography,
        LocalShapes provides shapes
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

object XCalendarTheme {
    val dimensions: Dimensions
        @Composable @ReadOnlyComposable get() = LocalDimensions.current

    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = LocalCalendarColors.current

    val typography: Typography
        @Composable @ReadOnlyComposable get() = LocalTypography.current

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = LocalShapes.current
}