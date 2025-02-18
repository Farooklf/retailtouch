package com.hashmato.retailtouch.presentation.common

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hashmato.retailtouch.theme.AppTheme

@Composable
fun AppCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enabled: Boolean = true,
    checkedColor:Color=AppTheme.colors.primaryColor,
    uncheckedColor:Color=AppTheme.colors.textBlack,
    checkmarkColor:Color=AppTheme.colors.appWhite,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
            checkmarkColor = checkmarkColor,
        ),
    )
}