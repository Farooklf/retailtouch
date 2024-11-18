package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lfssolutions.retialtouch.theme.AppTheme

@Composable
fun AppCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = AppTheme.colors.brand,
            uncheckedColor = AppTheme.colors.appGrey,
            checkmarkColor = AppTheme.colors.appWhite,
        ),
    )
}