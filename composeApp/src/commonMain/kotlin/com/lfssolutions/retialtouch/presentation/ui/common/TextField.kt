package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
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
    textColor: Color =AppTheme.colors.textColor,
    focusedBorderColor: Color = AppTheme.colors.textPrimary,
    unfocusedBorderColor: Color =AppTheme.colors.textSecondary,
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
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        painter = painterResource(leadingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
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
                focusedBorderColor = focusedBorderColor,
                focusedLabelColor = focusedBorderColor,
                cursorColor = focusedBorderColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.7f),
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
    background: Color = AppTheme.colors.textWhite
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
                textColor = AppTheme.colors.textSecondary,
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
    errorColor: Color =AppTheme.colors.textWhite,
    textColor: Color =AppTheme.colors.textWhite,
    focusedColor: Color =AppTheme.colors.textWhite,
    unfocusedColor: Color =AppTheme.colors.textOffWhite,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    leadingIcon: DrawableResource,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Row (
        modifier = Modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = vectorResource(leadingIcon),
            contentDescription = "employee code",
            modifier = Modifier.size(30.dp).padding(top = 5.dp),
            colorFilter = ColorFilter.tint(AppTheme.colors.textWhite)
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
                shape = AppTheme.appShape.textField,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = textColor,
                    focusedBorderColor = focusedColor,
                    focusedLabelColor = focusedColor,
                    cursorColor = focusedColor,
                    unfocusedLabelColor = unfocusedColor,
                    unfocusedBorderColor = unfocusedColor,
                )
            )

            error?.let {
                Text(
                    text = it,
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
    modifier: Modifier = Modifier.fillMaxWidth(),
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
        modifier = modifier.padding(10.dp),
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
                textColor = AppTheme.colors.textColor,
                focusedBorderColor = AppTheme.colors.textColor,
                focusedLabelColor = AppTheme.colors.textColor,
                placeholderColor = AppTheme.colors.textColor.copy(alpha = .8f),
                cursorColor = AppTheme.colors.textColor,
                unfocusedLabelColor = AppTheme.colors.textColor.copy(alpha = .8f),
                unfocusedBorderColor = AppTheme.colors.textColor.copy(alpha = 0.8f)
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
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    //var typeValue by remember(value) { mutableStateOf(value) }
    /*Card(modifier=modifier
        .wrapContentHeight()
        .padding(10.dp)
        .background(AppTheme.colors.searchBoxColor),
         shape = RoundedCornerShape(10.dp)
    ){

    }*/

    Column(modifier = modifier
        .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.padding(10.dp),
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
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
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
                textColor = AppTheme.colors.textColor,
                focusedBorderColor = AppTheme.colors.textPrimaryBlue,
                focusedLabelColor = AppTheme.colors.textPrimaryBlue,
                placeholderColor = AppTheme.colors.textColor.copy(alpha = .7f),
                cursorColor = AppTheme.colors.textPrimaryBlue,
                unfocusedLabelColor = AppTheme.colors.textPrimaryBlue.copy(alpha = .8f),
                unfocusedBorderColor = AppTheme.colors.textPrimaryBlue.copy(alpha = 0.8f)
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
fun ClickableAppOutlinedTextField(
    value: String,
    onClick: () -> Unit, // Callback for click events
    modifier: Modifier,
    enabled: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    textColor : Color = AppTheme.colors.textColor,
    errorColor: Color =AppTheme.colors.textError,
    focusedColor: Color =AppTheme.colors.textPrimaryBlue,
    unfocusedColor: Color =AppTheme.colors.textPrimaryBlue.copy(alpha = 0.8f),
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

    Column(
        modifier = modifier.clickable{
            onClick.invoke()
        },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.wrapContentWidth(),
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
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
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
                textColor = textColor,
                focusedBorderColor = focusedColor,
                focusedLabelColor = focusedColor,
                cursorColor = focusedColor,
                unfocusedLabelColor = textColor.copy(alpha = 0.7f),
                unfocusedBorderColor = unfocusedColor,
                placeholderColor = textColor.copy(alpha = 0.7f),
                errorLabelColor = errorColor,
                trailingIconColor = focusedColor,
                leadingIconColor = textColor.copy(alpha = 0.7f),
                disabledTrailingIconColor=textColor.copy(alpha = 0.7f)

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

enum class InputType {
    Any,
    OnlyDigital,
    Decimal
}