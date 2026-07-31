package com.example.nnailscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nnailscan.R
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanLogoCircle
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.util.rememberNailScanImageRequest

fun categoryImageContainerColor(termId: String?): Color =
    when (termId) {
        DictionaryContent.PSORIASIS,
        DictionaryContent.UNA_SANA,
        DictionaryContent.MELANOMA,
        -> NailScanSurface
        else -> NailScanLogoCircle
    }

fun categoryImageContentScale(termId: String?): ContentScale =
    when (termId) {
        DictionaryContent.PSORIASIS,
        DictionaryContent.UNA_SANA,
        DictionaryContent.MELANOMA,
        -> ContentScale.Fit
        else -> ContentScale.Crop
    }

@Composable
fun CategoryImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    termId: String? = null,
    size: Dp = 48.dp,
    roundedCorners: Dp = 12.dp,
    circular: Boolean = false,
    contentScale: ContentScale = categoryImageContentScale(termId),
) {
    val shape = if (circular) CircleShape else RoundedCornerShape(roundedCorners)
    val containerColor = categoryImageContainerColor(termId)
    val request = rememberNailScanImageRequest(
        data = imageUrl.takeIf { it.isNotBlank() },
        size = size,
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = request,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                contentScale = contentScale,
                alignment = Alignment.Center,
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.45f),
                tint = NailScanAccent,
            )
        }
    }
}

@Composable
fun categoryImageDescription(title: String): String =
    stringResource(R.string.category_image_description, title)
