package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons.closeIcon
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AppCircleProgressIndicator(
    isVisible:Boolean,
    color: Color = AppTheme.colors.progressBlue,
    message: String? = null,
    modifier: Modifier = Modifier
){

    if(isVisible){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = modifier,
                    color = color,
                    strokeWidth = AppTheme.dimensions.progressWidth
                )
                message?.let {
                    Text(
                        text = it,
                        color = color,
                        style = AppTheme.typography.bodyNormal()
                    )
                }
            }
        }
    }

}

@Composable
fun AppScreenCircleProgressIndicator(
    isVisible: Boolean
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { },
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier,
                color = AppTheme.colors.brand,
                strokeWidth = 5.dp
            )
        }
    }
}

@Composable
fun AppCircleProgressIndicatorWithMessage(
    isError:Boolean,
    color: Color = AppTheme.colors.progressPrimary,
    message: String? = null,
    errorMsg: String? = null,
    errorTitle: String? = null,
    modifier: Modifier = Modifier,
    onErrorTimeout: (() -> Unit)? = null // Callback for resetting error
) {
    // LaunchedEffect to clear error after a delay
    if (errorMsg != null) {
        LaunchedEffect(key1 = errorMsg) {
            delay(5000)
            onErrorTimeout?.invoke()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AppTheme.colors.loaderBgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            if(!isError){
                CircularProgressIndicator(
                    modifier = modifier.padding(20.dp),
                    color = color
                )
                Spacer(modifier = Modifier.height(20.dp))

                message?.let {
                    Text(
                        text = it,
                        color = color,
                        style = AppTheme.typography.bodyNormal()
                    )
                }

            }else{
                Column(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Image(
                        imageVector = vectorResource(closeIcon),
                        contentDescription = "close",
                        modifier = Modifier.size(100.dp).padding(top = 5.dp),
                        colorFilter = ColorFilter.tint(AppTheme.colors.textError)
                    )

                    errorTitle?.let {
                        Text(
                            text = errorTitle,
                            color = AppTheme.colors.textError,
                            style = AppTheme.typography.titleBold()
                        )
                    }
                    errorMsg?.let {
                        Text(
                            text = errorMsg,
                            color = AppTheme.colors.primaryText,
                            style = AppTheme.typography.titleMedium()
                        )
                    }
                }
            }
        }
    }
}