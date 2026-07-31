package com.example.nnailscan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NailScanColorScheme = darkColorScheme(
    primary = NailScanButton,
    onPrimary = NailScanOnAccent,
    primaryContainer = NailScanCopperDeep,
    onPrimaryContainer = NailScanTextPrimary,
    secondary = NailScanButtonSecondary,
    onSecondary = NailScanOnAccent,
    secondaryContainer = NailScanNavyLight,
    onSecondaryContainer = NailScanTextPrimary,
    tertiary = NailScanAccent,
    onTertiary = NailScanOnAccent,
    background = NailScanBackground,
    onBackground = NailScanTextPrimary,
    surface = NailScanSurface,
    onSurface = NailScanTextPrimary,
    surfaceVariant = NailScanPrimaryLight,
    onSurfaceVariant = NailScanTextSecondary,
    outline = NailScanBorder,
    outlineVariant = NailScanBorder,
)

@Composable
fun NNailScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NailScanColorScheme,
        typography = Typography,
        content = content,
    )
}
