package com.lfssolutions.retialtouch.presentation.ui.common



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.LocalAppState
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOutlinedDropDown(
    selectedValue: String,
    options: List<MemberGroupItem>,
    label: String,
    contentColor: Color = AppTheme.colors.primaryText,
    focusedColor: Color = AppTheme.colors.primaryColor,
    unFocusedColor: Color = AppTheme.colors.primaryColor.copy(alpha = 0.8f),
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
fun <T> AppFilledDropDown(
    selectedValue: String,
    options: List<T>,
    label: String?,
    labelExtractor: @Composable (T) -> String,
    contentColor: Color = AppTheme.colors.primaryText,
    focusedColor: Color = AppTheme.colors.primaryColor,
    unFocusedColor: Color = AppTheme.colors.primaryColor.copy(alpha = 0.8f),
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

        TextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = {
                label?.let {
                    Text(
                        text = it
                    )
                }
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = AppTheme.colors.listRowBgColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = AppTheme.colors.textBlack,
                placeholderColor = AppTheme.colors.textDarkGrey,
                focusedLabelColor = AppTheme.colors.textDarkGrey,
                cursorColor = AppTheme.colors.textBlack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(BorderStroke(width = 1.dp, color = AppTheme.colors.listRowBorderColor))
                .menuAnchor()
                .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
            /*modifier = modifier
                .menuAnchor()
                .wrapContentWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }*/
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: T ->
                DropdownMenuItem(
                    text = { Text(
                        text = labelExtractor(option),  // Use the labelExtractor to get the text
                        style = AppTheme.typography.bodyMedium(),
                        color = contentColor
                    ) },
                    colors = MenuDefaults.itemColors(
                        textColor = contentColor
                    ),
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)  // Return the generic item on click
                    }
                )
            }
        }
    }
}



@Composable
fun <T> AppDropdownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    labelExtractor: @Composable (T) -> String,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (String, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        AppDropdownMenuItem(
            text = item,
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {

    var expanded by remember { mutableStateOf(false) }
    val tint =if(selectedIndex>-1) AppTheme.colors.textDarkGrey else AppTheme.colors.textLightGrey
    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = { Text(text = label, color = tint , style = AppTheme.typography.captionBold()) },
            value = items.getOrNull(selectedIndex)?.let { labelExtractor(it) } ?: "",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                //ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                val icon =if(expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                Icon(icon, "", tint = tint)
            },
            onValueChange = { },
            readOnly = true,
            maxLines = 1,
            minLines = 1,
            textStyle = AppTheme.typography.bodyMedium(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = AppTheme.colors.primaryColor,
                focusedLabelColor = AppTheme.colors.primaryColor,
                unfocusedLabelColor = AppTheme.colors.textDarkGrey,
                focusedBorderColor = AppTheme.colors.primaryColor,
                unfocusedBorderColor = AppTheme.colors.textDarkGrey
            )
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { expanded = true },
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
        ) {
            Surface(
                shape = AppTheme.appShape.dialog,
            ) {
                val listState = rememberLazyListState()
                if (selectedIndex > -1) {
                    LaunchedEffect("ScrollToSelected") {
                        listState.scrollToItem(index = selectedIndex)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                    if (notSetLabel != null) {
                        item {
                            AppDropdownMenuItem(
                                text = notSetLabel,
                                selected = false,
                                enabled = false,
                                onClick = { },
                            )
                        }
                    }
                    itemsIndexed(items) { index, item ->
                        val selectedItem = index == selectedIndex
                        drawItem(
                            labelExtractor(item),
                            selectedItem,
                            true
                        ) {
                            onItemSelected(index, item)
                            expanded = false
                        }

                        if (index < items.lastIndex) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val appState = LocalAppState.current
    val style = when(appState.isPortrait) {
        true -> AppTheme.typography.bodyMedium()
        else -> AppTheme.typography.titleMedium()
    }
    val contentColor = when {
        !enabled -> AppTheme.colors.textPrimary.copy(alpha = .9f)
        selected -> AppTheme.colors.textPrimary
        else -> AppTheme.colors.textPrimary.copy(alpha = .9f)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(
                text = text,
                style = style,
            )
        }
    }
}

