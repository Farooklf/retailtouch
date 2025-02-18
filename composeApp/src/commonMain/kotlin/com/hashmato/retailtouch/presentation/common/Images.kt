package com.hashmato.retailtouch.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.isAndroid
import com.hashmato.retailtouch.utils.isIos
import com.hashmato.retailtouch.utils.loadGifImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.app_logo

@Composable
fun VectorIcons(
    modifier: Modifier = Modifier.wrapContentHeight(),
    icons: DrawableResource,
    iconSize: Dp =AppTheme.dimensions.smallXIcon,
    iconColor : Color =AppTheme.colors.appRed,
    alignment: Arrangement.Horizontal = Arrangement.End,
    onClick: ()-> Unit
)
{
    Row(modifier = modifier.clickable{
         onClick.invoke()
       },
        horizontalArrangement = alignment){
        Image(
            imageVector = vectorResource(icons),
            contentDescription = "",
            modifier = Modifier.size(iconSize),
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

@Composable
fun ImagePlaceholderWithUrl() {
    Image(
        painter = painterResource(Res.drawable.app_logo),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

/*@Composable
fun GifImageLoader(imageName: String, modifier: Modifier = Modifier) {
    when {
        isAndroid() -> AndroidGifImage(imageName, modifier)
        isIos() -> IosGifImage(imageName, modifier)
    }
}*/

@Composable
fun GifImageLoader(imageName: String, modifier: Modifier = Modifier) {
    loadGifImage(imageName, modifier).invoke()
}
