package com.lfssolutions.retialtouch.presentation.ui.common


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    arrangement: Arrangement.Horizontal =Arrangement.Center,
    isButton:Boolean=false,
    singleLine:Boolean=true,
    verticalPadding:Dp=10.dp,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onButtonClick: () -> Unit={},
){

    Row(modifier = modifier.padding(vertical = verticalPadding),
        horizontalArrangement = arrangement) {
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
fun QtyItemText(
    label:String,
    textStyle: TextStyle = AppTheme.typography.titleBold(),
    color: Color = AppTheme.colors.primaryText,
    modifier: Modifier=Modifier.wrapContentHeight(),
    onClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit
){

    Row( modifier = modifier.padding(vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween) {

        //modifier icons
        // Decrease button
        IconButton(onClick = { onDecreaseClick() }) {
            Icon(
                imageVector = vectorResource(AppIcons.minusCircleIcon),
                contentDescription = "Decrease Quantity",
                modifier = Modifier.size(24.dp),
                tint = AppTheme.colors.appGreen
            )
        }
        //Spacer(modifier=Modifier.width(5.dp))

        GreyButtonWithElevation(
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            label = label,
            contentColor = color,
            textStyle = textStyle,
            onClick = {
                onClick.invoke()
            }
        )
        //Spacer(modifier=Modifier.width(5.dp))
        IconButton(onClick = { onIncreaseClick() }) {
            Icon(
                imageVector = vectorResource(AppIcons.addIcon),
                contentDescription = "Increase Quantity",
                modifier = Modifier.size(24.dp),
                tint = AppTheme.colors.secondaryText
            )
        }

    }

}

@Composable
fun BottomTex(
    label:String,
    textStyle: TextStyle = AppTheme.typography.titleMedium(),
    color: Color = AppTheme.colors.primaryText,
    modifier: Modifier=Modifier.wrapContentHeight(),
    singleLine:Boolean=false,
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
