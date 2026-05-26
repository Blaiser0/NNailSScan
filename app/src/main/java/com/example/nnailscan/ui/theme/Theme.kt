package com.example.nnailscan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NailScanColorScheme = lightColorScheme(
    primary = NailScanButton,
    onPrimary = NailScanSurface,
    background = NailScanBackground,
    onBackground = NailScanTextPrimary,
    surface = NailScanSurface,
    onSurface = NailScanTextPrimary,
    outline = NailScanBorder,
)

@Composable
fun NNailScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NailScanColorScheme,
        typography = Typography,
        content = content,
    )
}
