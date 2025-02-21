package com.hashmato.retailtouch.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.ToastManager
import kotlinx.coroutines.launch

@Composable
fun CustomToast(){
    val appThemeContext = AppTheme.context
    val message by ToastManager.toastMessage.collectAsState()

    if (message != null) {
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(message) {
            coroutineScope.launch { ToastManager.hideToast() }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedVisibility(
                visible = message != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = appThemeContext.dimensions._50sdp)
                        .shadow(appThemeContext.dimensions.space8, appThemeContext.appShape.card12)
                        .background(appThemeContext.colors.cardBgColor)
                        .padding(horizontal = appThemeContext.dimensions.padding10, vertical = appThemeContext.dimensions.padding10)
                ) {
                    Text(
                        text = message!!,
                        color = appThemeContext.colors.textBlack,
                        style = appThemeContext.typography.bodyMedium()
                    )
                }
            }
        }
    }
}