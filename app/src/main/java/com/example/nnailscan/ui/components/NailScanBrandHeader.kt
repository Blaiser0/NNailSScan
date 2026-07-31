package com.example.nnailscan.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography

enum class BrandHeaderSize {
    Large,
    Compact,
    Auth,
}

@Composable
fun NailScanBrandHeader(
    modifier: Modifier = Modifier,
    size: BrandHeaderSize = BrandHeaderSize.Large,
    showTagline: Boolean = true,
    showTitle: Boolean = true,
    onLogoTripleClick: (() -> Unit)? = null,
) {
    val logoSize: Dp
    val titleStyle = when (size) {
        BrandHeaderSize.Large -> Typography.displayMedium
        BrandHeaderSize.Compact -> Typography.titleLarge
        BrandHeaderSize.Auth -> Typography.displayMedium
    }
    val taglineStyle = when (size) {
        BrandHeaderSize.Large -> Typography.bodyLarge
        BrandHeaderSize.Compact -> Typography.bodyMedium
        BrandHeaderSize.Auth -> Typography.bodyMedium
    }

    logoSize = when (size) {
        BrandHeaderSize.Large -> 140.dp
        BrandHeaderSize.Compact -> 96.dp
        BrandHeaderSize.Auth -> 333.dp // 75 % más grande que 190.dp
    }

    var clickCount by remember { mutableIntStateOf(0) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(logoSize)
                .background(NailScanBackground)
                .then(
                    if (onLogoTripleClick != null) {
                        Modifier.clickable {
                            val now = System.currentTimeMillis()
                            if (now - lastClickTime > 700) clickCount = 0
                            lastClickTime = now
                            clickCount++
                            if (clickCount >= 3) {
                                clickCount = 0
                                onLogoTripleClick()
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.nailscan_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
        if (showTitle) {
            Spacer(
                modifier = Modifier.height(
                    when (size) {
                        BrandHeaderSize.Large -> 16.dp
                        BrandHeaderSize.Auth -> 14.dp
                        BrandHeaderSize.Compact -> 12.dp
                    },
                ),
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = NailScanTextPrimary, fontWeight = FontWeight.Bold)) {
                        append("Nail")
                    }
                    withStyle(SpanStyle(color = NailScanAccent, fontWeight = FontWeight.Bold)) {
                        append("Scan")
                    }
                },
                style = titleStyle,
                textAlign = TextAlign.Center,
            )
        }
        if (showTagline) {
            Spacer(modifier = Modifier.height(if (size == BrandHeaderSize.Large) 10.dp else 8.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                style = taglineStyle.copy(color = NailScanTextSecondary),
                textAlign = TextAlign.Center,
            )
        }
    }
}
