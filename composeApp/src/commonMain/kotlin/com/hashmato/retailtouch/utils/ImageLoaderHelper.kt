package com.hashmato.retailtouch.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Platform detection (expect/actual)
expect fun isAndroid(): Boolean
expect fun isIos(): Boolean
expect fun loadGifImage(imageName: String, modifier: Modifier): @Composable () -> Unit
