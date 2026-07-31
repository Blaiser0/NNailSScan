package com.example.nnailscan.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Espaciado compartido alrededor del bloque de marca en las 3 pantallas. */
object NailScanAuthBrandingDefaults {
    val screenHorizontalPadding = 24.dp
    val screenVerticalPadding = 32.dp
    val headerToBrandingSpacing = 8.dp
    val brandingToContentSpacing = 20.dp
}

/**
 * Logo + "NailScan" con el mismo tamaño que la pantalla de bienvenida al abrir la app.
 */
@Composable
fun NailScanAuthBranding(
    modifier: Modifier = Modifier,
    onLogoTripleClick: (() -> Unit)? = null,
) {
    NailScanBrandHeader(
        modifier = modifier.fillMaxWidth(),
        size = BrandHeaderSize.Large,
        showTagline = false,
        showTitle = true,
        onLogoTripleClick = onLogoTripleClick,
    )
}

/** Bloque de marca con espacio inferior estándar hacia el contenido siguiente. */
@Composable
fun NailScanAuthBrandingSection(
    modifier: Modifier = Modifier,
    contentSpacing: Dp = NailScanAuthBrandingDefaults.brandingToContentSpacing,
    onLogoTripleClick: (() -> Unit)? = null,
) {
    NailScanAuthBranding(modifier = modifier, onLogoTripleClick = onLogoTripleClick)
    Spacer(modifier = Modifier.height(contentSpacing))
}
