package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.LocalAppState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = AppTheme.appShape.button,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
    isVisible:Boolean=true
) {
    if (isVisible) {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            interactionSource = interactionSource,
            elevation = elevation,
            shape = shape,
            colors = colors,
            contentPadding = contentPadding,
            content = content
        )
    }
}

@Composable
fun BaseOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = AppTheme.appShape.button,
    borderWidth: Dp = 2.dp,
    borderColor: Color = AppTheme.colors.primaryColor,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
    isVisible:Boolean=true
) {
    if (isVisible) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            border = BorderStroke(borderWidth, borderColor),
            enabled = enabled,
            shape = shape,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
            content = content
        )
    }
}



@Composable
fun AppPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style:TextStyle?=null,
    label: String,
    enabled: Boolean = true,
    rightIcon: DrawableResource? = null,
    leftIcon: DrawableResource? = null,
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    backgroundColor: Color = AppTheme.colors.primaryButtonBg,
    contentColor: Color = AppTheme.colors.appWhite,
    disabledBackgroundColor: Color = AppTheme.colors.primaryButtonBg,
    isVisible: Boolean = true,
    syncInProgress: Boolean = false,

) {
    val appState = LocalAppState.current
    val (verticalInnerPadding,horizontalInnerPadding)=if(appState.isPortrait)
        AppTheme.dimensions.padding15 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding15 to AppTheme.dimensions.padding20

    val portraitStyle=style ?:AppTheme.typography.bodyBold()
    val landScapeStyle=style ?:AppTheme.typography.titleBold()

    val (space,textStyle)=if(appState.isPortrait)
        AppTheme.dimensions.padding5 to portraitStyle
    else
        AppTheme.dimensions.padding20 to landScapeStyle

    val iconSize=if(appState.isPortrait)
        AppTheme.dimensions.smallXIcon
    else
        AppTheme.dimensions.small24Icon

    // Create an Animatable to control rotation angle
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(syncInProgress) {
        if (syncInProgress) {
            scope.launch {
                // Infinite rotation loop
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        } else {
            // Reset rotation when stopped
            rotation.snapTo(0f)
        }
    }

    BaseButton(
        isVisible = isVisible,
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = horizontalInnerPadding, vertical = verticalInnerPadding) ,// Set inner padding
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = disabledBackgroundColor
        ),
        elevation = elevation,
        content = {
            if (leftIcon != null) {
                Icon(
                    painter = painterResource(leftIcon),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize).rotate(rotation.value) // Apply rotation
                )
                Spacer(modifier=Modifier.width(space))
            }
            Text(
                text = label,
                style = textStyle,
                color = contentColor,
                textAlign = TextAlign.Center,
                softWrap = true,
                minLines=1,
                maxLines = 1
            )

            if (rightIcon != null) {
                Spacer(modifier=Modifier.width(space))
                Icon(
                    painter = painterResource(rightIcon),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    )

}

@Composable
fun AppCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    rightIcon: DrawableResource? = null,
    leftIcon: DrawableResource? = null,
    textStyle: TextStyle = AppTheme.typography.bodyMedium(),
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    backgroundColor: Color = AppTheme.colors.closeButtonBg,
    contentColor: Color = AppTheme.colors.closeButtonContent,
    disabledBackgroundColor: Color = AppTheme.colors.primaryButtonBg,
    isVisible: Boolean=true,
    horizontalInnerPadding: Dp = 10.dp,
    verticalInnerPadding: Dp = 10.dp,
) {

    BaseButton(
        isVisible = isVisible,
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = horizontalInnerPadding, vertical = verticalInnerPadding) ,// Set inner padding
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = disabledBackgroundColor
        ),
        elevation = elevation,
        content = {
            if (leftIcon != null) {
                Icon(
                    painter = painterResource(leftIcon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = label,
                style = textStyle,
                textAlign = TextAlign.Center,
            )

            if (rightIcon != null) {
                Icon(
                    painter = painterResource(rightIcon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )

}

@Composable
fun AppBorderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    rightIcon: DrawableResource? = null,
    textStyle: TextStyle = AppTheme.typography.bodyMedium(),
    leftIcon: DrawableResource? = null,
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    contentColor: Color = AppTheme.colors.primaryColor,
    isVisible: Boolean=true,
    horizontalInnerPadding: Dp = 30.dp,
    verticalInnerPadding: Dp = 10.dp,
) {
    BaseOutlineButton(
        isVisible = isVisible,
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = horizontalInnerPadding, vertical = verticalInnerPadding) ,// Set inner padding
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        elevation = elevation,
        content = {
            if (leftIcon != null) {
                Icon(
                    painter = painterResource(leftIcon),
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimensions.standerIcon)
                )
            }

            Text(
                text = label,
                style = textStyle,
                color = contentColor,
                textAlign = TextAlign.Center,
            )

            if (rightIcon != null) {
                Icon(
                    painter = painterResource(rightIcon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )

}


@Composable
fun GreyButtonWithElevation(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    textStyle: TextStyle = AppTheme.typography.titleMedium(),
    contentColor: Color = AppTheme.colors.primaryColor,
    buttonBgdColor: Color = AppTheme.colors.textLightGrey,
    horizontalInnerPadding: Dp = 10.dp,
    verticalInnerPadding: Dp = 10.dp,
) {
    Button(
        onClick = { onClick() },
        contentPadding = PaddingValues(horizontal = horizontalInnerPadding, vertical = verticalInnerPadding),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 4.dp,         // Default elevation
            pressedElevation = 8.dp,         // Elevation when pressed
            disabledElevation = 0.dp         // Elevation when disabled
        ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonBgdColor,    // Set background to grey
            contentColor = contentColor, // Set content color to white
            disabledBackgroundColor = AppTheme.colors.greyButtonBg
        ),
        modifier = modifier,
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(
            text = label,
            style = textStyle,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun RowButtonWithIcons(
    label: String,
    contentColor: Color = AppTheme.colors.primaryText,
    textStyle: TextStyle=AppTheme.typography.titleMedium(),
    icons:DrawableResource?=null,
    iconSize: Dp = AppTheme.dimensions.smallIcon,
    iconColor: Color = AppTheme.colors.appWhite,
    modifier: Modifier = Modifier,
    innerPaddingValues: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
    onClick: () -> Unit,
){

    Row(modifier = modifier
        .padding(innerPaddingValues)
        .clickable{onClick()},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        if (icons != null) {
            Icon(
                imageVector = vectorResource(icons),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
        }

        Text(
            text = label,
            style = textStyle,
            color = contentColor,
            minLines=1,
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
            )
    }
}
