package com.example.nnailscan.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.imageLoader
import coil.request.ImageRequest
import com.example.nnailscan.data.model.DictionaryContent

object NailScanImageLoader {

    fun request(
        context: Context,
        data: Any?,
        sizePx: Int,
        crossfade: Boolean = false,
    ): ImageRequest {
        if (data == null) {
            return ImageRequest.Builder(context).data("").build()
        }

        return ImageRequest.Builder(context)
            .data(data)
            .size(sizePx)
            .crossfade(crossfade)
            .memoryCacheKey("${data}_$sizePx")
            .diskCacheKey("${data}_$sizePx")
            .build()
    }

    fun dpToPx(context: Context, size: Dp): Int {
        val density = context.resources.displayMetrics.density
        return (size.value * density).toInt().coerceAtLeast(1)
    }

    fun preloadDictionaryImages(context: Context) {
        val loader = context.imageLoader
        val listSizePx = dpToPx(context, Dp(96f))
        val detailSizePx = dpToPx(context, Dp(220f))

        DictionaryContent.terms.forEach { term ->
            val assetPath = DictionaryContent.assetImagePath(term.id)
            loader.enqueue(request(context, assetPath, listSizePx))
            loader.enqueue(request(context, assetPath, detailSizePx))
        }
    }
}

@Composable
fun rememberNailScanImageRequest(
    data: Any?,
    size: Dp,
    crossfade: Boolean = false,
): ImageRequest {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx().coerceAtLeast(1) }

    return remember(data, sizePx, crossfade) {
        NailScanImageLoader.request(context, data, sizePx, crossfade)
    }
}
