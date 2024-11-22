package com.lfssolutions.retialtouch.presentation.ui.common


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.ic_add_24
import retailtouch.composeapp.generated.resources.ic_plus

@Composable
fun SmallTextComponent(text: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        modifier = modifier,
        text = text,
        color = AppTheme.colors.primaryText,
        style = AppTheme.typography.bodyMedium()
    )
}
@Composable
fun ScreenHeaderText(
    label:String,
    textStyle: TextStyle = AppTheme.typography.h1Normal(),
    color: Color = AppTheme.colors.headerTextColor
){

    Text(
        text = label,
        style = textStyle.copy(fontSize = 24.sp),
        textAlign = TextAlign.Center,
        color = color
    )
}

@Composable
fun ListItemText(
    label:String,
    textStyle: TextStyle = AppTheme.typography.bodyBold(),
    color: Color = AppTheme.colors.primaryText,
    modifier: Modifier=Modifier.wrapContentHeight(),
    arrangement: Arrangement.Horizontal = Arrangement.Start,
    isButton:Boolean=false,
    singleLine:Boolean=true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onButtonClick: () -> Unit={},
){

    Row(modifier = modifier,
        horizontalArrangement = arrangement, verticalAlignment = Alignment.CenterVertically) {
        if(isButton){
            GreyButtonWithElevation(
                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                label = label,
                contentColor = color,
                textStyle = textStyle,
                onClick = {onButtonClick.invoke()}
            )
        }else{
            Text(
                text = label,
                style = textStyle,
                color = color,
                minLines=minLines,
                maxLines = maxLines,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Composable
fun ListText(
    label:String,
    textStyle: TextStyle = AppTheme.typography.bodyBold(),
    color: Color = AppTheme.colors.primaryText,
    modifier: Modifier = Modifier.wrapContentHeight(),
    singleLine:Boolean=true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onButtonClick: () -> Unit={},
){
    Text(
        text = label,
        style = textStyle,
        color = color,
        minLines=minLines,
        maxLines = maxLines,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun QtyItemText(
    label:String,
    textStyle: TextStyle = AppTheme.typography.bodyMedium(),
    color: Color = AppTheme.colors.primaryText,
    modifier: Modifier=Modifier.wrapContentHeight(),
    isEven:Boolean=false,
    isPortrait:Boolean=false,
    onClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit
){
    val (cardSize,iconSize)=if(isPortrait){
        20.dp to 10.dp
    }else{
        30.dp to 15.dp
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
        //modifier icons
        // Decrease button
        val (cardColor,carTextColor) = if (isEven) {
            CardDefaults.cardColors(containerColor = AppTheme.colors.appWhite) to AppTheme.colors.mintGreenColor
        } else {
            CardDefaults.cardColors(containerColor = AppTheme.colors.textLightGrey) to AppTheme.colors.mintGreenColor
        }

        Card(
            modifier = Modifier.size(cardSize).clickable{onDecreaseClick() },
            colors = cardColor,
            elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
            shape = AppTheme.appShape.cardRound
        ){
            Box(modifier=Modifier.fillMaxSize()){
                Icon(
                    imageVector = vectorResource(AppIcons.minusIcon),
                    contentDescription = "Decrease Quantity",
                    modifier = Modifier.size(iconSize).align(Alignment.Center),
                    tint = carTextColor
                )
            }
        }
        ListText(
            label = label,
            textStyle = textStyle,
            color = color,
            modifier = Modifier.wrapContentWidth()
        )

        Card(
            modifier = Modifier.size(cardSize).clickable{onIncreaseClick() },
            colors = cardColor,
            elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
            shape = AppTheme.appShape.cardRound
        ){
            Box(modifier=Modifier.fillMaxSize()){
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_add_24),
                    contentDescription = "Increase Quantity",
                    modifier = Modifier.size(iconSize).align(Alignment.Center),
                    tint = AppTheme.colors.textDarkGrey
                )
            }
        }
    }

}

@Composable
fun BottomTex(
    label:String,
    color: Color = AppTheme.colors.primaryText,
    textStyle: TextStyle=AppTheme.typography.bodyBold(),
    modifier: Modifier=Modifier.wrapContentHeight(),
    singleLine:Boolean=false,
    isPortrait:Boolean=false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1
){


    Text(
        text = label,
        style = textStyle,
        color = color,
        minLines=minLines,
        maxLines = maxLines,
        softWrap = true,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TexWithClickableBg(
    modifier: Modifier=Modifier.wrapContentHeight(),
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
){

    Box(modifier = modifier
        .clip(RoundedCornerShape(5.dp))
        .border(
            width = 1.4.dp,
            color = AppTheme.colors.primaryButtonBg,
            shape = RoundedCornerShape(5.dp)
        )
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
        .background(AppTheme.colors.primaryButtonBg.copy(alpha = 0.8f))
        .padding(PaddingValues(5.dp))) {

        content()
    }
}
