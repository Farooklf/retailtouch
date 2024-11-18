package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lfssolutions.retialtouch.presentation.viewModels.PaymentCollectorViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.PaymentCollectorButtonType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.ic_tagcross


@Composable
fun PaymentCollectorDialog(
    isVisible: Boolean = false,
    totalValue:Double=0.0,
    onDismiss: () -> Unit = {},
    onPayClick: (Double) -> Unit = {},
    paymentName: String,
    interactor: PaymentCollectorViewModel = koinInject()
){

    val state by interactor.paymentCollectorState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        if(isVisible)
         interactor.initialState(totalValue)
    }

    LaunchedEffect(state.paymentSuccess) {
        if (state.paymentSuccess) {
            onPayClick(state.total.toDouble())
        }
    }


    AppDialog(
        isVisible = isVisible,
        modifier = Modifier.systemBarsPadding(),
        onDismissRequest = onDismiss,
        contentMaxWidth = AppTheme.dimensions.contentMaxWidth,
        isFullScreen = false
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.appWhite)
                .padding(
                    vertical = 20.dp,
                    horizontal = 40.dp
                )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(AppIcons.closeIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(AppTheme.dimensions.smallIcon)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss
                        )
                )
            }

            Text(
                text = paymentName,
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                style = AppTheme.typography.h1Normal().copy(fontSize = 30.sp),
                color = AppTheme.colors.primaryColor,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .border(
                        width = 5.dp,
                        color = AppTheme.colors.greyButtonBg,
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = state.total,
                    style = AppTheme.typography.h1Black(),
                    color = AppTheme.colors.primaryText
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .padding(top = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(Modifier.weight(1.5f), PaymentCollectorButtonType.Hundred) { interactor.onButtonClick(it) }
                    Button(Modifier.weight(1f), PaymentCollectorButtonType.Fifty) { interactor.onButtonClick(it) }
                    Button(Modifier.weight(1f), PaymentCollectorButtonType.Twenty) { interactor.onButtonClick(it) }
                    Button(Modifier.weight(1f), PaymentCollectorButtonType.Ten) { interactor.onButtonClick(it) }
                    Button(Modifier.weight(1f), PaymentCollectorButtonType.Five) { interactor.onButtonClick(it) }
                    Button(Modifier.weight(1f), PaymentCollectorButtonType.Two) { interactor.onButtonClick(it) }
                }

                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.One) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Two) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Three) { interactor.onButtonClick(it) }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Four) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Five) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Six) { interactor.onButtonClick(it) }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Seven) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Eight) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Nine) { interactor.onButtonClick(it) }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.DoubleZero) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Zero) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Dot) { interactor.onButtonClick(it) }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1.2f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(Modifier.weight(1f), PaymentCollectorButtonType.Delete) { interactor.onButtonClick(it) }
                        Button(Modifier.weight(2f), PaymentCollectorButtonType.Pay) { interactor.onButtonClick(it) }
                    }
                }
            }
        }
    }


}

@Composable
fun Button(
    modifier: Modifier = Modifier,
    type: PaymentCollectorButtonType,
    onClick: (PaymentCollectorButtonType) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(AppTheme.appShape.card)
            .background(if (type == PaymentCollectorButtonType.Pay) AppTheme.colors.buttonPrimaryBgColor else if(type == PaymentCollectorButtonType.Delete) AppTheme.colors.textError else AppTheme.colors.greyButtonBg)
            .clickable(
                role = Role.Button,
                onClick = { onClick(type) }
            ),
        contentAlignment = Alignment.Center
    ) {

        if (type == PaymentCollectorButtonType.Delete) {
            Image(
                painter = painterResource(Res.drawable.ic_tagcross),
                colorFilter = ColorFilter.tint(AppTheme.colors.appWhite),
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimensions.mediumIcon)
            )
        } else {
            Text(
                text = type.toString(),
                style = AppTheme.typography.h1Medium().copy(fontSize = 18.sp),
                color = if (type == PaymentCollectorButtonType.Pay) AppTheme.colors.appWhite else AppTheme.colors.primaryText
            )
        }
    }
}

data class PaymentCollectorDialogState(
    val total: String = "",
    val paymentSuccess: Boolean = false
)