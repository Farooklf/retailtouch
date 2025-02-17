package com.hashmato.retailtouch.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun isAndroid()=false
actual fun isIos()=true
actual fun loadGifImage(
    imageName: String,
    modifier: Modifier
): @Composable () -> Unit {
    return {
        UIViewController {
            val imageView = UIImageView().apply {
                contentMode = platform.UIKit.UIViewContentMode.UIViewContentModeScaleAspectFit
            }

            val bundlePath = NSBundle.mainBundle.pathForResource(imageName, "gif")
            val gifData = bundlePath?.let { NSData.dataWithContentsOfFile(it) }

            if (gifData != null) {
                imageView.image = UIImage(data = gifData)
            } else {
                imageView.image = UIImage(named = "error_image") // Fallback image
            }

            imageView
        }
    }
}