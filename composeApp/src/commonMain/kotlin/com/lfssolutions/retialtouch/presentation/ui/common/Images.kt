package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.app_logo

@Composable
fun VectorIcons(
    modifier: Modifier = Modifier.wrapContentHeight(),
    icons: DrawableResource,
    iconColor : Color =AppTheme.colors.textError,
    onClick: ()-> Unit
)
{
    Row( modifier = Modifier.padding(horizontal = 10.dp).clickable{
         onClick.invoke()
       },
        horizontalArrangement = Arrangement.Center){
        Image(
            imageVector = vectorResource(icons),
            contentDescription = "",
            modifier = modifier,
            colorFilter = ColorFilter.tint(iconColor)
        )
    }
}


@Composable
fun ImagePlaceholder() {
    Image(
        painter = painterResource(Res.drawable.app_logo),
        contentDescription = null,
        modifier = Modifier.size(AppTheme.dimensions.mediumIcon),
        contentScale = ContentScale.Crop
    )
}
