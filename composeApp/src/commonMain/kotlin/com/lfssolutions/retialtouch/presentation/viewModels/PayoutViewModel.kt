package com.lfssolutions.retialtouch.presentation.viewModels

import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleTransactionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class PayoutViewModel : BaseViewModel(), KoinComponent {

    private val _screenState = MutableStateFlow(SaleTransactionState())
    val screenState: StateFlow<SaleTransactionState> = _screenState.asStateFlow()


}