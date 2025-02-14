package com.hashmato.retailtouch.presentation.ui.common.dialogs.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.theme.Language
import com.hashmato.retailtouch.utils.AppLanguage
import com.hashmato.retailtouch.utils.changeLang
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ChangeLanguageViewInteractor :ViewModel(),KoinComponent {

    private val _languageState = MutableStateFlow(ChangeLanguageDialogState())
    val languageState: StateFlow<ChangeLanguageDialogState> = _languageState.asStateFlow()

    fun changeLanguage(value: AppLanguage) {
        viewModelScope.launch {
            val updatedLang = when (value) {
                AppLanguage.English -> {
                    //homeScreenViewInteractor.updateScreenDirection(false)
                    Language.English.isoFormat
                }
                AppLanguage.Arabic -> {
                    // homeScreenViewInteractor.updateScreenDirection(true)
                    Language.Arabic.isoFormat
                }

                AppLanguage.French -> TODO()
            }

            changeLang(updatedLang)
        }
    }

}


data class ChangeLanguageDialogState(
    val selectedLanguage: AppLanguage = AppLanguage.English,
    val index: Int = 0
)