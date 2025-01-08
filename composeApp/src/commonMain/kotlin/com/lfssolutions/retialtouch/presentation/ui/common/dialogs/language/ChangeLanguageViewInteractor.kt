package com.lfssolutions.retialtouch.presentation.ui.common.dialogs.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.theme.Language
import com.lfssolutions.retialtouch.utils.AppLanguage
import com.lfssolutions.retialtouch.utils.changeLang
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
            }

            changeLang(updatedLang)
        }
    }

}


data class ChangeLanguageDialogState(
    val selectedLanguage: AppLanguage = AppLanguage.English,
    val index: Int = 0
)