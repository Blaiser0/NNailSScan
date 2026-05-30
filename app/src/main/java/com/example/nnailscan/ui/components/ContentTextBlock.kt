package com.example.nnailscan.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun ContentTextBlock(
    text: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
        textAlign = if (centered) TextAlign.Center else TextAlign.Start,
    )
}

@Composable
fun ContentSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier.fillMaxWidth(),
        style = Typography.titleSmall.copy(color = NailScanTextPrimary),
    )
}
