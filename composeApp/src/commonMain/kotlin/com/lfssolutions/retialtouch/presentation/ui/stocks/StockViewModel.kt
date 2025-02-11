package com.lfssolutions.retialtouch.presentation.ui.stocks


import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class StockViewModel :BaseViewModel() , KoinComponent {

    private val _stockUiState = MutableStateFlow(StockUIState())
    val stockUiState: StateFlow<StockUIState> = _stockUiState.asStateFlow()


}