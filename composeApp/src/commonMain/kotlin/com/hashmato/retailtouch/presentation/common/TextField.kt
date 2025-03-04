package com.hashmato.retailtouch.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.LocalAppState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.ic_error

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth().wrapContentHeight(),
    textModifier: Modifier = Modifier.fillMaxWidth().wrapContentHeight(),
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    errorColor: Color =AppTheme.colors.textError,
    primaryText: Color =AppTheme.colors.primaryText,
    focusedBorderColor: Color = AppTheme.colors.textPrimary,
    unfocusedBorderColor: Color =AppTheme.colors.secondaryText,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    leadingIcon: DrawableResource? = null,
    trailingIcon: DrawableResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    //var typeValue by remember(value) { mutableStateOf(value) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textModifier,
            enabled = enabled,
            textStyle = textStyle,
            visualTransformation = visualTransformation,
            label = {
                label?.let {
                    Text(
                        text = it
                    )
                }
            },
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it
                    )
                }
            },
            trailingIcon = if (trailingIcon != null){
                {
                    Icon(
                        painter = painterResource(trailingIcon),
                        contentDescription = null,
                        tint = primaryText,
                        modifier = Modifier.size(AppTheme.dimensions.smallIcon)
                    )
                }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        painter = painterResource(leadingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.dimensions.smallIcon)
                    )
                }
            } else null,

            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = error != null,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = AppTheme.appShape.textField,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = primaryText,
                focusedBorderColor = focusedBorderColor,
                focusedLabelColor = focusedBorderColor,
                cursorColor = focusedBorderColor,
                unfocusedLabelColor = primaryText.copy(alpha = 0.7f),
                unfocusedBorderColor = unfocusedBorderColor,
                placeholderColor = unfocusedBorderColor,
                errorLabelColor = errorColor,
                trailingIconColor = focusedBorderColor,
                disabledTrailingIconColor=unfocusedBorderColor

            )
        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 10.dp),
                style = AppTheme.typography.captionNormal(),
                color = errorColor
            )
        }
    }
}


@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    leadingIcon: DrawableResource? = null,
    error: String? = null,
    inputType: InputType = InputType.Any,
    maxLength: Int = 0,
    textStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions(),
    background: Color = AppTheme.colors.appWhite
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextField(
            value = value,
            onValueChange = { str ->
                //str.filterInput(maxLength, inputType)?.let { onValueChange(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = {
                label?.let {
                    Text(
                        text = label,
                        style = AppTheme.typography.captionNormal()
                    )
                }
            },
            leadingIcon = if (leadingIcon!= null) {
                {
                    Icon(
                        painter = painterResource(leadingIcon),
                        contentDescription = label,
                        tint = AppTheme.colors.textFieldIcon
                    )
                }
            } else null,
            trailingIcon = {
                if (error != null) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_error),
                        contentDescription = label,
                        tint = MaterialTheme.colors.error
                    )
                }
            },
            isError = error != null,
            singleLine = true,
            maxLines = 1,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = TextFieldDefaults.textFieldColors(
                textColor = AppTheme.colors.secondaryText,
                backgroundColor = background,
                cursorColor = AppTheme.colors.textPrimary,
                leadingIconColor = AppTheme.colors.textPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                unfocusedLabelColor = if (error != null) MaterialTheme.colors.error else AppTheme.colors.textPrimary,
                focusedLabelColor = AppTheme.colors.textPrimary,
                disabledTextColor = textStyle.color,
            )
        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 10.dp),
                style = AppTheme.typography.captionNormal(),
                color = MaterialTheme.colors.error
            )
        }
    }
}



@Composable
fun AppOutlinedTextFieldWithOuterIcon(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    errorColor: Color =AppTheme.colors.appWhite,
    primaryText: Color =AppTheme.colors.appWhite,
    focusedColor: Color =AppTheme.colors.appWhite,
    unfocusedColor: Color =AppTheme.colors.appOffWhite,
    label: String? = null,
    placeholder: String? = null,
    error: StringResource? = null,
    leadingIcon: DrawableResource,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    val appThemeContext=AppTheme.context
    Row (
        modifier = Modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = vectorResource(leadingIcon),
            contentDescription = label,
            modifier = Modifier.size(appThemeContext.dimensions.small24Icon).padding(top = 5.dp),
            colorFilter = ColorFilter.tint(AppTheme.colors.appWhite)
        )
        Column (
            modifier=modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                textStyle = textStyle,
                visualTransformation = visualTransformation,
                label = {
                    label?.let {
                        Text(
                            text = it
                        )
                    }
                },
                placeholder = {
                    placeholder?.let {
                        Text(
                            text = it
                        )
                    }
                },
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                isError = error != null,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                shape = appThemeContext.appShape.textField,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = primaryText,
                    focusedBorderColor = focusedColor,
                    focusedLabelColor = focusedColor,
                    cursorColor = focusedColor,
                    unfocusedLabelColor = unfocusedColor,
                    unfocusedBorderColor = unfocusedColor,
                    placeholderColor = unfocusedColor
                )
            )

            error?.let {
                Text(
                    text = stringResource(it),
                    modifier = Modifier.padding(start = 15.dp),
                    style = AppTheme.typography.captionNormal(),
                    color = errorColor
                )
            }
        }
    }
}


@Composable
fun AppOutlinedSearch(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmittedClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth().padding(10.dp),
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    errorColor: Color =AppTheme.colors.textError,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    leadingIcon: DrawableResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    //var typeValue by remember(value) { mutableStateOf(value) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            textStyle = textStyle,
            visualTransformation = visualTransformation,
            label = {
                label?.let {
                    Text(
                        text = it
                    )
                }
            },
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it
                    )
                }
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = vectorResource(leadingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else null,

            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = {
                    // When done is pressed, open the dialog
                    onSubmittedClick(true)
                }
            ),
            isError = error != null,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = AppTheme.appShape.textField,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = AppTheme.colors.primaryText,
                focusedBorderColor = AppTheme.colors.primaryText,
                focusedLabelColor = AppTheme.colors.primaryText,
                placeholderColor = AppTheme.colors.primaryText.copy(alpha = .8f),
                cursorColor = AppTheme.colors.primaryText,
                unfocusedLabelColor = AppTheme.colors.primaryText.copy(alpha = .8f),
                unfocusedBorderColor = AppTheme.colors.primaryText.copy(alpha = 0.8f)
            )
        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 10.dp),
                style = AppTheme.typography.captionNormal(),
                color = errorColor
            )
        }
    }
}

@Composable
fun SearchableTextWithBg(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    errorColor: Color =AppTheme.colors.textError,
    backgroundColor:Color = AppTheme.colors.searchBoxColor,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    leadingIcon: DrawableResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    trailingIcon: DrawableResource? = null,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onClearedClick: () -> Unit={},
) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        TextField(
          value=value,
            onValueChange=onValueChange,
            modifier = Modifier.fillMaxWidth().border(BorderStroke(width = 1.dp, color = AppTheme.colors.borderColor)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = AppTheme.colors.textBlack,
                placeholderColor = AppTheme.colors.textDarkGrey,
                focusedLabelColor = AppTheme.colors.textDarkGrey,
                cursorColor = AppTheme.colors.textBlack
            ),
            enabled = enabled,
            textStyle = textStyle,
            visualTransformation = visualTransformation,
            label = {
                label?.let {
                    Text(
                        text = it
                    )
                }
            },
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it
                    )
                }
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = vectorResource(leadingIcon),
                        contentDescription = null,
                        tint = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.size(AppTheme.dimensions.smallXIcon)
                    )
                }
            } else null,
            trailingIcon = if (trailingIcon != null  && value.isNotEmpty()){
                {
                    IconButton(onClick = {
                        onClearedClick.invoke()
                    }){
                       Icon(painter = painterResource(trailingIcon),
                           contentDescription = null,
                           tint = AppTheme.colors.appRed,
                           modifier = Modifier.size(AppTheme.dimensions.smallIcon))
                    }
                }
            } else null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = error != null,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = AppTheme.appShape.textField,

        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 10.dp),
                style = AppTheme.typography.captionNormal(),
                color = errorColor
            )
        }

    }

}

@Composable
fun SearchableTextWithGradientBg(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    errorColor: Color =AppTheme.colors.appWhite,
    backgroundColor:Color = Color.Transparent,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    leadingIcon: DrawableResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        TextField(
            value=value,
            onValueChange=onValueChange,
            modifier = Modifier.fillMaxWidth().border(BorderStroke(width = 1.dp, color = AppTheme.colors.borderColor)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = AppTheme.colors.textBlack,
                placeholderColor = AppTheme.colors.textDarkGrey,
                focusedLabelColor = AppTheme.colors.textDarkGrey,
                cursorColor = AppTheme.colors.textBlack
            ),
            enabled = enabled,
            textStyle = textStyle,
            visualTransformation = visualTransformation,
            label = {
                label?.let {
                    Text(
                        text = it
                    )
                }
            },
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it
                    )
                }
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = vectorResource(leadingIcon),
                        contentDescription = null,
                        tint = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.size(AppTheme.dimensions.smallXIcon)
                    )
                }
            } else null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = error != null,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = AppTheme.appShape.textField,

            )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 10.dp),
                style = AppTheme.typography.captionNormal(),
                color = errorColor
            )
        }

    }

}

@Composable
fun PrinterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    maxLength: Int = 0,
    isDigitInputOnly: Boolean = false,
    fontSize: TextUnit = 14.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var displayedValue by remember(value) { mutableStateOf(value) }

    TextField(
        value = displayedValue,
        onValueChange = {
            val filtered = it.filter { symbol ->
                symbol.isDigit()
            }

            if (maxLength <= 0 || it.length <= maxLength) {
                val str = if (isDigitInputOnly) filtered else it
                displayedValue = str
                onValueChange(str)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = LocalTextStyle.current.copy(fontSize = fontSize),
        label = {
            label?.let {
                Text(
                    text = label,
                    style = AppTheme.typography.bodyNormal().copy(fontSize = fontSize)
                )
            }
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.textFieldColors(
            textColor = AppTheme.colors.primaryText,
            backgroundColor = AppTheme.colors.secondaryCardBg,
            cursorColor = AppTheme.colors.brandText,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedLabelColor = AppTheme.colors.secondaryText,
            focusedLabelColor = AppTheme.colors.brandText,
            disabledLabelColor = AppTheme.colors.secondaryText,
            disabledTextColor = AppTheme.colors.primaryText
        )
    )
}

@Composable
fun AppDialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
    placeholder: String = "",
    maxLength: Int = 0,
    inputType: InputType = InputType.Any,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    var displayedValue by remember(value) { mutableStateOf(value) }

    TextField(
        value = value,
        onValueChange = { str ->
            str.filterInput(maxLength, inputType)?.let {
                displayedValue = it
                onValueChange(it)
            }
        },
        modifier = modifier.fillMaxWidth(),
        textStyle = textStyle,
        placeholder = {
            Text(
                text = placeholder,
                style = textStyle,
                color = AppTheme.colors.secondaryText.copy(alpha = .8f)
            )
        },
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.textFieldColors(
            textColor = AppTheme.colors.primaryText,
            cursorColor = AppTheme.colors.activeColor,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = AppTheme.colors.brand,
            unfocusedLabelColor = AppTheme.colors.brand,
        )
    )
}


@Composable
fun ClickableAppOutlinedTextField(
    value: String,
    onClick: () -> Unit, // Callback for click events
    modifier: Modifier,
    enabled: Boolean = false,
    textColor : Color = AppTheme.colors.textBlack,
    errorColor: Color =AppTheme.colors.textError,
    focusedColor: Color =AppTheme.colors.textDarkGrey,
    unfocusedColor: Color =AppTheme.colors.textDarkGrey,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    leadingIcon: DrawableResource? = null,
    trailingIcon: DrawableResource? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val appState = LocalAppState.current
    val (labelStyle,textStyle)=if(appState.isPortrait){
        AppTheme.typography.captionMedium() to AppTheme.typography.bodyMedium()
    }else
        AppTheme.typography.bodyMedium() to AppTheme.typography.titleMedium()

    Column(
        modifier = modifier.clickable{
            onClick.invoke()
        },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            textStyle = textStyle,
            visualTransformation = visualTransformation,
            label = {
                label?.let {
                    Text(
                        text = it,
                        style = labelStyle
                    )
                }
            },
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it
                    )
                }
            },
            trailingIcon = if (trailingIcon != null){
                {
                    Icon(
                        painter = painterResource(trailingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.dimensions.small24Icon)
                    )
                }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        painter = painterResource(leadingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.dimensions.small24Icon)
                    )
                }
            } else null,

            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = error != null,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = AppTheme.appShape.textField,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = textColor,
                focusedBorderColor = focusedColor,
                focusedLabelColor = focusedColor,
                unfocusedLabelColor = unfocusedColor,
                unfocusedBorderColor = unfocusedColor,
                placeholderColor = unfocusedColor,
                errorLabelColor = errorColor,
                trailingIconColor = focusedColor,
                leadingIconColor = focusedColor,
                cursorColor = focusedColor,
                disabledTextColor = textColor,
                disabledBorderColor = focusedColor,
                disabledLabelColor = focusedColor
                )
        )

        error?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = 10.dp),
                style = AppTheme.typography.captionNormal(),
                color = errorColor
            )
        }
    }


}

fun String.filterInput(
    maxLength: Int,
    inputType: InputType
): String? {

    if (maxLength > 0 && this.length > maxLength) return null

    if (inputType == InputType.Decimal && (this.count { it == '.' } > 1) || (this.isNotEmpty() && this[0] == '.')) return null

    val filtered = this.filter { symbol ->
        when (inputType) {
            InputType.OnlyDigital -> symbol.isDigit()
            InputType.Decimal -> symbol.isDigit() || symbol == '.'
            else -> true
        }
    }

    if (inputType == InputType.Decimal && '.' in this) {
        val substring = this.substringAfter('.')
        if (substring.length > 2) return null
    }

    return filtered
}

enum class InputType {
    Any,
    OnlyDigital,
    Decimal
}