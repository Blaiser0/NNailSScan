package com.example.nnailscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nnailscan.R
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanBorder
import com.example.nnailscan.ui.theme.NailScanImagePlaceholder
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.util.rememberNailScanImageRequest

@Composable
fun ScanHistoryCard(
    dateLabel: String,
    result: String,
    imageUrl: String?,
    onClick: () -> Unit,
    userName: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(NailScanSurface, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScanThumbnail(
            imageUrl = imageUrl,
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result,
                style = Typography.bodyMedium.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            if (!userName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.scan_history_user_label, userName),
                    style = Typography.labelLarge.copy(
                        color = NailScanAccent,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateLabel,
                style = Typography.labelLarge.copy(color = NailScanTextSecondary),
            )
        }
    }
}

@Composable
fun ScanThumbnail(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val thumbnailSize = 64.dp
    val request = rememberNailScanImageRequest(
        data = imageUrl,
        size = thumbnailSize,
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(NailScanImagePlaceholder, shape)
            .border(1.dp, NailScanBorder, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Outlined.ImageNotSupported,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = NailScanAccent,
            )
        } else {
            AsyncImage(
                model = request,
                contentDescription = stringResource(R.string.scan_thumbnail_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

/** @deprecated Usar [ScanHistoryCard] */
@Composable
fun RecentActivityCard(
    dateLabel: String,
    result: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onClick: () -> Unit = {},
) {
    ScanHistoryCard(
        dateLabel = dateLabel,
        result = result,
        imageUrl = imageUrl,
        onClick = onClick,
        modifier = modifier,
    )
}
