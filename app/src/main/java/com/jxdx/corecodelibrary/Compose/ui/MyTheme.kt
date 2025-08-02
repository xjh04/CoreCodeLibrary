package com.jxdx.corecodelibrary.Compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 浅蓝色调颜色定义
private val LightBlue50 = Color(0xFFE3F2FD)
private val LightBlue100 = Color(0xFFBBDEFB)
private val LightBlue200 = Color(0xFF90CAF9)
private val LightBlue300 = Color(0xFF64B5F6)
private val LightBlue400 = Color(0xFF42A5F5)
private val LightBlue500 = Color(0xFF2196F3)
private val LightBlue600 = Color(0xFF1E88E5)
private val LightBlue700 = Color(0xFF1976D2)
private val LightBlue800 = Color(0xFF1565C0)
private val LightBlue900 = Color(0xFF0D47A1)

// 辅助颜色
private val BlueGrey50 = Color(0xFFECEFF1)
private val BlueGrey100 = Color(0xFFCFD8DC)
private val BlueGrey200 = Color(0xFFB0BEC5)
private val BlueGrey500 = Color(0xFF607D8B)
private val BlueGrey700 = Color(0xFF455A64)
private val BlueGrey800 = Color(0xFF37474F)
private val BlueGrey900 = Color(0xFF263238)

// 浅色主题颜色方案
private val LightBlueColorScheme = lightColorScheme(
    primary = LightBlue600,
    onPrimary = Color.White,
    primaryContainer = LightBlue100,
    onPrimaryContainer = LightBlue800,

    secondary = BlueGrey500,
    onSecondary = Color.White,
    secondaryContainer = BlueGrey100,
    onSecondaryContainer = BlueGrey700,

    tertiary = LightBlue300,
    onTertiary = Color.White,
    tertiaryContainer = LightBlue50,
    onTertiaryContainer = LightBlue700,

    background = Color(0xFFFAFCFF),
    onBackground = BlueGrey900,

    surface = Color.White,
    onSurface = BlueGrey900,
    surfaceVariant = BlueGrey50,
    onSurfaceVariant = BlueGrey700,

    surfaceTint = LightBlue600,

    inverseSurface = BlueGrey700,
    inverseOnSurface = Color.White,
    inversePrimary = LightBlue200,

    outline = BlueGrey200,
    outlineVariant = BlueGrey100,

    scrim = Color.Black,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

// 深色主题颜色方案
private val DarkBlueColorScheme = darkColorScheme(
    primary = LightBlue200,
    onPrimary = LightBlue900,
    primaryContainer = LightBlue700,
    onPrimaryContainer = LightBlue100,

    secondary = BlueGrey200,
    onSecondary = BlueGrey800,
    secondaryContainer = BlueGrey700,
    onSecondaryContainer = BlueGrey100,

    tertiary = LightBlue100,
    onTertiary = LightBlue800,
    tertiaryContainer = LightBlue600,
    onTertiaryContainer = LightBlue50,

    background = Color(0xFF0F1419),
    onBackground = Color(0xFFE1E3E6),

    surface = Color(0xFF191C20),
    onSurface = Color(0xFFE1E3E6),
    surfaceVariant = Color(0xFF40484C),
    onSurfaceVariant = Color(0xFFC0C8CC),

    surfaceTint = LightBlue200,

    inverseSurface = Color(0xFFE1E3E6),
    inverseOnSurface = Color(0xFF2E3135),
    inversePrimary = LightBlue600,

    outline = Color(0xFF8A9296),
    outlineVariant = Color(0xFF40484C),

    scrim = Color.Black,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun LightBlueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkBlueColorScheme
        else -> LightBlueColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}