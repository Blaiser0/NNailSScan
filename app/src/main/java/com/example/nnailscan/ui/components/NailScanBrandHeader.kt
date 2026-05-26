package com.example.nnailscan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

enum class BrandHeaderSize {
    Large,
    Compact,
}

@Composable
fun NailScanBrandHeader(
    modifier: Modifier = Modifier,
    size: BrandHeaderSize = BrandHeaderSize.Large,
    showTagline: Boolean = true,
) {
    val logoSize: Dp
    val titleStyle = when (size) {
        BrandHeaderSize.Large -> Typography.displayMedium
        BrandHeaderSize.Compact -> Typography.titleLarge
    }
    val taglineStyle = when (size) {
        BrandHeaderSize.Large -> Typography.bodyLarge
        BrandHeaderSize.Compact -> Typography.bodyMedium
    }

    logoSize = when (size) {
        BrandHeaderSize.Large -> 132.dp
        BrandHeaderSize.Compact -> 96.dp
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.nailscan_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(logoSize),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(if (size == BrandHeaderSize.Large) 20.dp else 14.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = titleStyle.copy(color = NailScanTextPrimary, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        if (showTagline) {
            Spacer(modifier = Modifier.height(if (size == BrandHeaderSize.Large) 10.dp else 8.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                style = taglineStyle.copy(color = NailScanTextPrimary),
                textAlign = TextAlign.Center,
            )
        }
    }
}
