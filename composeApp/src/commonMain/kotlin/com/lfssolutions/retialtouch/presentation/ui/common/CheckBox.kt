package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
            checkedColor = AppTheme.colors.primaryColor,
            uncheckedColor = AppTheme.colors.textLightGrey,
            checkmarkColor = AppTheme.colors.appWhite,
        ),
    )
}