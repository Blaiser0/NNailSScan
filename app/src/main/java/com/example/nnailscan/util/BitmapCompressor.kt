package com.example.nnailscan.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object BitmapCompressor {
    private const val JPEG_QUALITY = 88
    private const val PROFILE_MAX_DIMENSION = 800

    fun scaleDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val scale = minOf(
            maxDimension.toFloat() / width,
            maxDimension.toFloat() / height,
        )
        val newWidth = (width * scale).toInt().coerceAtLeast(1)
        val newHeight = (height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun toJpeg(bitmap: Bitmap, quality: Int = JPEG_QUALITY): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    fun toProfileJpeg(bitmap: Bitmap): ByteArray {
        val scaled = scaleDown(bitmap, PROFILE_MAX_DIMENSION)
        val jpeg = toJpeg(scaled)
        if (scaled !== bitmap) {
            scaled.recycle()
        }
        return jpeg
    }
}
