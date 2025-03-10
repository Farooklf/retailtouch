package com.hashmato.retailtouch.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.amount_label
import retailtouch.composeapp.generated.resources.amount_placeholder
import retailtouch.composeapp.generated.resources.apply
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.clear_discount

@Composable
fun NumberPad(
    textValue: String = "",
    trailingIcon: DrawableResource? = null,
    inputError: String?=null,
    onValueChange: (String) -> Unit,
    onNumberPadClick: (String) -> Unit,
    onApplyClick: () -> Unit,
    onClearDiscountClick: () -> Unit,
    onCancelClick: () -> Unit,
){

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(AppTheme.dimensions.padding10),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth(),
            value = textValue,
            onValueChange = { discount ->
                onValueChange.invoke(discount)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            trailingIcon = trailingIcon,
            placeholder = stringResource(Res.string.amount_placeholder),
            label = stringResource(Res.string.amount_label),
            error = inputError,
            singleLine = true,
            enabled = true
        )

      // Number Pad
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(AppTheme.dimensions.padding10),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            for (row in listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "x")
            )) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.dimensions.padding20),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                    row.forEach { symbol ->
                        Button(
                            onClick = {
                                onNumberPadClick.invoke(symbol)
                            },
                            modifier = Modifier
                                .size(AppTheme.dimensions.icon60)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colors.greyButtonBg,
                            ),
                        ) {
                            Text(
                                text = symbol,
                                color = AppTheme.colors.primaryText,
                                style = AppTheme.typography.bodyMedium()
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.padding10),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            AppPrimaryButton(
                enabled = inputError==null,
                label = stringResource(Res.string.apply),
                leftIcon = AppIcons.applyIcon,
                backgroundColor = AppTheme.colors.appGreen,
                disabledBackgroundColor = AppTheme.colors.appGreen,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                onClick = {
                    onApplyClick.invoke()
                }
            )

            AppPrimaryButton(
                label = stringResource(Res.string.clear_discount),
                leftIcon = AppIcons.removeIcon,
                backgroundColor = AppTheme.colors.primaryColor,
                disabledBackgroundColor = AppTheme.colors.primaryColor,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                onClick = {
                    onClearDiscountClick.invoke()
                }
            )

            AppPrimaryButton(
                label = stringResource(Res.string.cancel),
                leftIcon = AppIcons.closeIcon,
                backgroundColor = AppTheme.colors.appRed,
                disabledBackgroundColor = AppTheme.colors.appRed,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                onClick = {
                    onCancelClick.invoke()
                }
            )

        }
    }


    //Spacer(modifier = Modifier.height(10.dp))
    //Spacer(modifier = Modifier.height(10.dp))


}