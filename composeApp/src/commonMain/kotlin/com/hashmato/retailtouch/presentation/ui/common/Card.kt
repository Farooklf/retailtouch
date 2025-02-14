package com.hashmato.retailtouch.presentation.ui.common


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.DiscountType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.discount

@Composable
fun AppBaseCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(4.dp),
    content: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(paddingValues),
        border = BorderStroke(width = 1.4.dp, color = AppTheme.colors.borderColor),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBgColor),
        elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
        shape = AppTheme.appShape.card
    ) {
        content()
    }
}

@Composable
fun ButtonCard(
    label:String,
    discountType:String="",
    icons:DrawableResource?=null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.appGreen,
    contentColor: Color = AppTheme.colors.appWhite,
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
            AppTheme.colors.primaryText
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
    isPortrait:Boolean=true,
    icons:DrawableResource?=null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    backgroundColor: Color = AppTheme.colors.appGreen,
    disableBgColor: Color = AppTheme.colors.greyButtonBg,
    contentColor: Color = AppTheme.colors.appWhite,
    elevation: CardElevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
    iconSize: Dp = AppTheme.dimensions.smallIcon,
    isEnabled:Boolean=true,
    isColorChange:Boolean=false,
    isDropdownExpanded:Boolean=false,
){
    val (vertPadding,horPadding)=if(isPortrait)
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20

    val innerPaddingValues = PaddingValues(vertical = vertPadding, horizontal = horPadding)

    // Conditional color: gray if disabled, white if enabled
    val cardColor = if (isEnabled) {
        CardDefaults.cardColors(containerColor = backgroundColor)
    } else {
        if(isColorChange){
            CardDefaults.cardColors(containerColor = AppTheme.colors.greyDarkButtonBg)
        }else{
            CardDefaults.cardColors(containerColor = disableBgColor)
        }
    }

    val carTextColor = if (isEnabled) {
        contentColor
    } else {
        if(isColorChange){
            AppTheme.colors.primaryText
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
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(innerPaddingValues), // Inner padding of the card
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Row(modifier=Modifier.wrapContentWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically){

                if (icons != null) {
                    Icon(
                        imageVector = vectorResource(icons),
                        contentDescription = null,
                        tint = carTextColor,
                        modifier = Modifier.size(iconSize)
                            .rotate(if (isDropdownExpanded) 180f else 0f))
                }
                Text(
                    text = label,
                    style = AppTheme.typography.bodyMedium(),
                    color = carTextColor,
                    minLines=1,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis)
            }
        }

    }
}

@Composable
fun DiscountTabCard(
    modifier: Modifier = Modifier,
    selectedDiscountType:DiscountType=DiscountType.FIXED_AMOUNT,
    elevation: CardElevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
    onTabClick: (DiscountType) -> Unit = {}
){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.appWhite),
        elevation = elevation,
        shape = AppTheme.appShape.card
    ){

        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()){
            // Row 1: Rounded corners on the left
            SelectableRow(
                modifier = Modifier.weight(1f).height(AppTheme.dimensions.defaultButtonSize).clickable{onTabClick.invoke(DiscountType.FIXED_AMOUNT)},
                text = stringResource(Res.string.discount),
                icons = AppIcons.dollarIcon,
                isSelected = selectedDiscountType==DiscountType.FIXED_AMOUNT,
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp) // Rounded left corners
            )
            // Row 2: Rounded corners on the right
            SelectableRow(
                modifier = Modifier.weight(1f).height(AppTheme.dimensions.defaultButtonSize).clickable{onTabClick.invoke(DiscountType.PERCENTAGE)},
                text = stringResource(Res.string.discount),
                icons = AppIcons.percentageIcon,
                isSelected = selectedDiscountType==DiscountType.PERCENTAGE,
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp) // Rounded right corners
            )
        }
    }
}


@Composable
fun CartHeaderImageButton(
    icon: DrawableResource,
    isVisible:Boolean=true,
    boxColor: Color =AppTheme.colors.textPrimary,
    onClick: () -> Unit = {}
) {
    if(isVisible){
        Card(modifier = Modifier.size(40.dp).clickable{onClick.invoke()},
            colors = CardDefaults.cardColors(boxColor),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = AppTheme.appShape.card)
        {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AppTheme.colors.appWhite),
                    modifier = Modifier
                        .size(AppTheme.dimensions.smallXIcon)
                )
            }
        }
    }
}



