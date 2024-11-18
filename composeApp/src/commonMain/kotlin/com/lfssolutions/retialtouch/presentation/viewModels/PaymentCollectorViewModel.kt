package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.lfssolutions.retialtouch.presentation.ui.common.PaymentCollectorDialogState
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.lfssolutions.retialtouch.utils.PaymentCollectorButtonType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaymentCollectorViewModel : ViewModel() {

    private val _paymentCollectorState = MutableStateFlow(PaymentCollectorDialogState())
    val paymentCollectorState: StateFlow<PaymentCollectorDialogState> = _paymentCollectorState


    fun initialState(remainingBalance: Double) {
        _paymentCollectorState.update {
            it.copy(
                total = remainingBalance.roundTo(2).toString(),
                paymentSuccess = false
            )
        }

    }

    fun onButtonClick(buttonType: PaymentCollectorButtonType) {
        if (buttonType == PaymentCollectorButtonType.Pay) {
            _paymentCollectorState.update { state -> state.copy(paymentSuccess = true) }
        } else {
            if (buttonType == PaymentCollectorButtonType.Delete) {
                _paymentCollectorState.update { state -> state.copy(total = state.total.dropLast(1)) }
            }
            else {
                val str = when(buttonType) {
                    PaymentCollectorButtonType.One -> "1"
                    PaymentCollectorButtonType.Two -> "2"
                    PaymentCollectorButtonType.Three -> "3"
                    PaymentCollectorButtonType.Four -> "4"
                    PaymentCollectorButtonType.Five -> "5"
                    PaymentCollectorButtonType.Six -> "6"
                    PaymentCollectorButtonType.Seven -> "7"
                    PaymentCollectorButtonType.Eight -> "8"
                    PaymentCollectorButtonType.Nine -> "9"
                    PaymentCollectorButtonType.Ten -> "10"
                    PaymentCollectorButtonType.Zero -> "0"
                    PaymentCollectorButtonType.DoubleZero -> "00"
                    PaymentCollectorButtonType.Dot -> if (paymentCollectorState.value.total.contains(".")) "" else "."
                    PaymentCollectorButtonType.Twenty -> "20"
                    PaymentCollectorButtonType.Fifty -> "50"
                    PaymentCollectorButtonType.Hundred -> "100"
                    else -> ""
                }
                _paymentCollectorState.update { state -> state.copy(total = state.total + str)  }
            }
        }
    }
}