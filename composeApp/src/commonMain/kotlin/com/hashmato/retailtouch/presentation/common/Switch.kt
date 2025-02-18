package com.hashmato.retailtouch.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hashmato.retailtouch.theme.AppTheme

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = AppTheme.colors.switchCheckedThumbColor,
            uncheckedThumbColor = AppTheme.colors.switchUncheckedThumbColor,
        )
    )
}


@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    width: Dp = 30.dp,
    height: Dp = 15.dp,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val animatedBackground by animateColorAsState(
        targetValue = if (checked) Color.LightGray else Color.White,
        animationSpec = tween(durationMillis = 300),
    )
    val animatedAlignment by animateFloatAsState(
        targetValue = if (checked) 1f else -1f,
        animationSpec = tween(durationMillis = 300),
    )

    Column(
        modifier
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange(it) },
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
            )
            .wrapContentSize(Alignment.Center)
            .requiredSize(width, height)
            .clip(CircleShape)
            .border(
                width = 1.5.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .background(animatedBackground)
            .padding(1.5.dp),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .align(BiasAlignment.Horizontal(animatedAlignment))
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                )
                .background(Color.Black),
        )
    }
}