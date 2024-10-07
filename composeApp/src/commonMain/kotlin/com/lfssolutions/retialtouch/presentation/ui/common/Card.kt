package com.lfssolutions.retialtouch.presentation.ui.common


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource


@Composable
fun ButtonCard(
    label:String,
    discountType:String="",
    icons:DrawableResource?=null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.appGreen,
    contentColor: Color = AppTheme.colors.textWhite,
    elevation: CardElevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
    innerPaddingValues: PaddingValues = PaddingValues(AppTheme.dimensions.buttonSquarePadding),
    iconSize: Dp = AppTheme.dimensions.smallIcon,
    isEnabled:Boolean=true,
    isColorChange:Boolean=false,
){
    // Conditional color: gray if disabled, white if enabled
    val cardColor = if (isEnabled) {
        CardDefaults.cardColors(containerColor = backgroundColor)
    } else {
        if(!isColorChange){
            CardDefaults.cardColors(containerColor = AppTheme.colors.greyDarkButtonBg)
        }else{
            CardDefaults.cardColors(containerColor = backgroundColor)
        }

    }


    val carTextColor = if (isEnabled) {
        contentColor
    } else {
        if(!isColorChange){
            AppTheme.colors.textColor
        }else{
            contentColor
        }
    }

    Card(
       modifier = modifier
           .clickable(enabled = isEnabled) { if (isEnabled) onClick() },
        colors = cardColor,
        elevation = elevation,
        shape = AppTheme.appShape.card
    ){

        Column(modifier = Modifier
                .wrapContentHeight()
                .padding(innerPaddingValues), // Inner padding of the card
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (icons != null) {
                Icon(
                    imageVector = vectorResource(icons),
                    contentDescription = null,
                    tint = carTextColor,
                    modifier = Modifier.size(iconSize)
                )
            }else{
                Text(
                    text = discountType,
                    style = AppTheme.typography.bodyMedium(),
                    color = carTextColor,
                    minLines=1,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier=Modifier.height(7.dp))

            Text(
                text = label,
                style = AppTheme.typography.bodyMedium(),
                color = carTextColor,
                minLines=1,
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )

        }

    }
}


@Composable
fun ButtonRowCard(
    label:String,
    discountType:String="",
    icons:DrawableResource?=null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.appGreen,
    contentColor: Color = AppTheme.colors.textWhite,
    elevation: CardElevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
    innerPaddingValues: PaddingValues = PaddingValues(AppTheme.dimensions.buttonSquarePadding),
    iconSize: Dp = AppTheme.dimensions.smallIcon,
    isEnabled:Boolean=true,
    isColorChange:Boolean=false,
    isDropdownExpanded:Boolean=false,
){
    // Conditional color: gray if disabled, white if enabled
    val cardColor = if (isEnabled) {
        CardDefaults.cardColors(containerColor = backgroundColor)
    } else {
        if(!isColorChange){
            CardDefaults.cardColors(containerColor = AppTheme.colors.greyDarkButtonBg)
        }else{
            CardDefaults.cardColors(containerColor = backgroundColor)
        }

    }

    val carTextColor = if (isEnabled) {
        contentColor
    } else {
        if(!isColorChange){
            AppTheme.colors.textColor
        }else{
            contentColor
        }
    }

    Card(
        modifier = modifier
            .clickable(enabled = isEnabled) { if (isEnabled) onClick() },
        colors = cardColor,
        elevation = elevation,
        shape = AppTheme.appShape.card
    ){

        Row(modifier = Modifier
            .wrapContentHeight()
            .padding(innerPaddingValues), // Inner padding of the card
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = label,
                style = AppTheme.typography.titleMedium(),
                color = carTextColor,
                minLines=1,
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis)

            //Spacer(modifier=Modifier.height(20.dp))

            if (icons != null) {
                Icon(
                    imageVector = vectorResource(icons),
                    contentDescription = null,
                    tint = carTextColor,
                    modifier = Modifier.size(iconSize)
                        .rotate(if (isDropdownExpanded) 180f else 0f)

                )
            }

        }

    }
}

