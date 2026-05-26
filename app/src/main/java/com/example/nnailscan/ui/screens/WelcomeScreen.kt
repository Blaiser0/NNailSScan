package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nnailscan.ui.components.BrandHeaderSize
import com.example.nnailscan.ui.components.NailScanBrandHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit,
    splashDelayMillis: Long = 2500L,
) {
    LaunchedEffect(Unit) {
        delay(splashDelayMillis)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        NailScanBrandHeader(
            size = BrandHeaderSize.Large,
            showTagline = true,
        )
    }
}
