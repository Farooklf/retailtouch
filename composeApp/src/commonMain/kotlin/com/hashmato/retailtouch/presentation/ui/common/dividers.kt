package com.hashmato.retailtouch.presentation.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hashmato.retailtouch.theme.AppTheme

@Composable
fun AppHorizontalDivider(
    modifier: Modifier = Modifier.fillMaxWidth(),
    color: Color = AppTheme.colors.listDivider,
    thickness: Dp = 1.dp
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness
    )
}