package com.lfssolutions.retialtouch.presentation.ui.common.dialogs.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.AppDialogChoiceFromList
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppLanguage
import com.outsidesource.oskitcompose.lib.rememberInjectForRoute
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.choose_an_language


@Composable
fun SelectLanguageDialog(
    isVisible: Boolean,
    selectedLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    //interactor: ChangeLanguageViewInteractor = rememberInjectForRoute<ChangeLanguageViewInteractor>()
) {
    //val state by interactor.languageState.collectAsStateWithLifecycle()

    AppDialogChoiceFromList(
        isVisible = isVisible,
        list = AppLanguage.entries.map { it.toStringValue() },
        selectedIndex = AppLanguage.entries.indexOf(selectedLanguage),
        title = stringResource(Res.string.choose_an_language),
        onDismissRequest = onDismiss,
        onDialogResult = {
            onSelectLanguage.invoke(AppLanguage.entries[it])
            //interactor.changeLanguage(AppLanguage.entries[it])
            onDismiss()
        },
        contentMaxWidth = AppTheme.dimensions.contentMaxSmallWidth
    )
}