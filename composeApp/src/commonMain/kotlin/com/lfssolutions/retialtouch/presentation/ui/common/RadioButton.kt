package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.lfssolutions.retialtouch.theme.AppTheme

@Composable
fun AppRadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = RadioButtonDefaults.colors(
            selectedColor = AppTheme.colors.brand,
            unselectedColor = AppTheme.colors.secondaryText,
        ),
    )
}

@Composable
fun AppRadioButtonWithText(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    enabled: Boolean = true,
    isClickable: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = isClickable,
                role = Role.Button,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = AppTheme.colors.brand,
                unselectedColor = AppTheme.colors.secondaryText,
            ),
        )

        Text(
            text = title,
            style = AppTheme.typography.bodyNormal(),
            color = AppTheme.colors.primaryText
        )
    }
}