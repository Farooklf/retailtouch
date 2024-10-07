package com.lfssolutions.retialtouch.presentation.ui.common



import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.theme.AppTheme
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOutlinedDropDown1(
    selectedValue: String,
    options: List<MemberGroupItem>,
    label: String,
    contentColor: Color = AppTheme.colors.textColor,
    focusedColor: Color = AppTheme.colors.textPrimaryBlue,
    unFocusedColor: Color = AppTheme.colors.textPrimaryBlue.copy(alpha = 0.8f),
    onValueChangedEvent: (MemberGroupItem) -> Unit,
    modifier: Modifier = Modifier,
){
    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }


    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ){

        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isFocused) focusedColor else unFocusedColor, // Set the focused color and unfocused color
                unfocusedBorderColor = unFocusedColor,
                focusedLabelColor = focusedColor,
                unfocusedLabelColor = unFocusedColor,
                focusedTrailingIconColor = focusedColor,
                unfocusedTrailingIconColor = unFocusedColor,
                cursorColor = focusedColor
            ),
            modifier = modifier
                .menuAnchor()
                .wrapContentWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: MemberGroupItem ->
                DropdownMenuItem(
                    text = { Text(
                        text = option.name?:"",
                        style = AppTheme.typography.bodyMedium(),
                        color = contentColor
                    ) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppOutlinedDropDown(
    selectedValue: String,
    options: List<T>,
    label: String,
    labelExtractor: @Composable (T) -> String,
    contentColor: Color = AppTheme.colors.textColor,
    focusedColor: Color = AppTheme.colors.textPrimaryBlue,
    unFocusedColor: Color = AppTheme.colors.textPrimaryBlue.copy(alpha = 0.8f),
    onValueChangedEvent: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isFocused) focusedColor else unFocusedColor,
                unfocusedBorderColor = unFocusedColor,
                focusedLabelColor = focusedColor,
                unfocusedLabelColor = unFocusedColor,
                focusedTrailingIconColor = focusedColor,
                unfocusedTrailingIconColor = unFocusedColor,
                cursorColor = focusedColor
            ),
            modifier = modifier
                .menuAnchor()
                .wrapContentWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: T ->
                DropdownMenuItem(
                    text = { Text(
                        text = labelExtractor(option),  // Use the labelExtractor to get the text
                        style = AppTheme.typography.bodyMedium(),
                        color = contentColor
                    ) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)  // Return the generic item on click
                    }
                )
            }
        }
    }
}

