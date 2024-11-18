package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lfssolutions.retialtouch.theme.AppTheme

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