package com.example.nnailscan

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.example.nnailscan.util.NailScanImageLoader

class NNailScanApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        NailScanImageLoader.preloadDictionaryImages(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve(DISK_CACHE_DIR))
                    .maxSizePercent(DISK_CACHE_PERCENT)
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)
            .crossfade(CROSSFADE_MS)
            .build()
    }

    companion object {
        private const val MEMORY_CACHE_PERCENT = 0.25
        private const val DISK_CACHE_PERCENT = 0.05
        private const val DISK_CACHE_DIR = "image_cache"
        private const val CROSSFADE_MS = 120
    }
}
