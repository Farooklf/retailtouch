package com.hashmato.retailtouch.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.hashmato.retailtouch.R


actual fun isAndroid(): Boolean {
    return true
}

actual fun isIos(): Boolean {
   return false
}


actual fun loadGifImage(
    imageName: String,
    modifier: Modifier
): @Composable () -> Unit {
    return {
        val context = LocalContext.current
        val gifUri = Uri.parse("android.resource://${context.packageName}/raw/$imageName") // Load from res/raw

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.raw.loading)
                .crossfade(true)
                .error(R.drawable.error_image) // Error fallback
                .placeholder(R.drawable.error_image) // Loading placeholder
                .build(),
            contentDescription = "GIF Image",
            modifier = modifier
        )
    }
}