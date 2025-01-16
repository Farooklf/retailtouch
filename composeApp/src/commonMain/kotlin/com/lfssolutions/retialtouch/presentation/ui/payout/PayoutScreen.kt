package com.lfssolutions.retialtouch.presentation.ui.payout


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedTextFieldWithOuterIcon
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.BasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.ScreenHeaderText
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.outsidesource.oskitcompose.router.KMPBackHandler
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.add_expense
import retailtouch.composeapp.generated.resources.amount
import retailtouch.composeapp.generated.resources.amount_placeholder
import retailtouch.composeapp.generated.resources.description
import retailtouch.composeapp.generated.resources.description_place_holder
import retailtouch.composeapp.generated.resources.pay_place_holder
import retailtouch.composeapp.generated.resources.pay_to
import retailtouch.composeapp.generated.resources.payout
import retailtouch.composeapp.generated.resources.sign_in
import retailtouch.composeapp.generated.resources.submit

object PayoutScreen :Screen  {

    @Composable
    override fun Content() {
     PayoutScreenContent()
    }

   @Composable
   fun PayoutScreenContent(
       viewModel: PayoutViewModel = koinInject()
   ) {
       val appThemeContext = AppTheme.context
       val navigator=appThemeContext.getAppNavigator()
       val state by viewModel.payoutUiState.collectAsStateWithLifecycle()
       val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

       LaunchedEffect(state.isError) {
           if (state.isError) {
               snackbarHostState.value.showSnackbar(state.errorMsg)
               delay(1000)
               viewModel.resetError()
           }
       }

       KMPBackHandler(true, onBack = {
           appThemeContext.navigateBackToHomeScreen(navigator,false)
       })

       val paddingValues = if (appThemeContext.isTablet) {
           AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
       } else{
           AppTheme.dimensions.padding10 to AppTheme.dimensions.padding10
       }

       BasicScreen(
           modifier = Modifier.systemBarsPadding(),
           gradientBg = AppTheme.colors.screenGradientVerticalBg,
           title = stringResource(Res.string.payout),
           isTablet = appThemeContext.isTablet,
           isForm = true,
           contentMaxWidth = Int.MAX_VALUE.dp,
           onBackClick = {
               appThemeContext.navigateBack(navigator)
           }
       ){
           Column(modifier = Modifier
               .fillMaxWidth()
               .verticalScroll(rememberScrollState()),
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.spacedBy(appThemeContext.dimensions.padding20)
           ){
               Column(modifier = Modifier
                   .wrapContentWidth()
                   .wrapContentHeight()
                   .padding(horizontal = paddingValues.first, vertical = paddingValues.second),
                   horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.spacedBy(20.dp)
               ){
                   //Screen Header Text
                   ScreenHeaderText(
                       label = stringResource(Res.string.add_expense),
                       color = appThemeContext.colors.appWhite
                   )

                   //Description
                   AppOutlinedTextFieldWithOuterIcon(
                       modifier = Modifier
                           .fillMaxWidth()
                           .wrapContentWidth(),
                       value = state.expenseDescription,
                       onValueChange = { value ->
                           viewModel.updateDescription(value)
                       },
                       keyboardOptions = KeyboardOptions(
                           imeAction = ImeAction.Next
                       ),
                       label = stringResource(Res.string.description),
                       placeholder = stringResource(Res.string.description_place_holder),
                       singleLine = true,
                       error = state.errorDescription,
                       enabled = !state.isSyncLoader,
                       leadingIcon = AppIcons.empRoleIcon
                   )

                   //Pay To
                   AppOutlinedTextFieldWithOuterIcon(
                       modifier = Modifier
                           .fillMaxWidth()
                           .wrapContentWidth(),
                       value = state.expensePayTo,
                       onValueChange = { value ->
                           viewModel.updatePayTo(value)
                       },
                       keyboardOptions = KeyboardOptions(
                           imeAction = ImeAction.Next
                       ),
                       label = stringResource(Res.string.pay_to),
                       placeholder = stringResource(Res.string.pay_place_holder),
                       singleLine = true,
                       error = state.errorPayTo,
                       enabled = !state.isSyncLoader,
                       leadingIcon = AppIcons.empRoleIcon
                   )

                   //Amount
                   AppOutlinedTextFieldWithOuterIcon(
                       modifier = Modifier
                           .fillMaxWidth()
                           .wrapContentWidth(),
                       value = state.expenseAmount,
                       onValueChange = { value ->
                           viewModel.updateAmount(value)
                       },
                       keyboardOptions = KeyboardOptions(
                           imeAction = ImeAction.Done,
                           keyboardType = KeyboardType.Decimal
                       ),
                       label = stringResource(Res.string.amount),
                       placeholder = stringResource(Res.string.amount_placeholder),
                       singleLine = true,
                       error = state.errorAmount,
                       enabled = !state.isSyncLoader,
                       leadingIcon = AppIcons.dollarIcon
                   )

                   AppPrimaryButton(
                       enabled = !state.isSyncLoader,
                       onClick = {
                           viewModel.onSubmitClick()
                       },
                       label = stringResource(Res.string.submit),
                       backgroundColor = appThemeContext.colors.primaryColor,
                       modifier = Modifier
                           .width(appThemeContext.dimensions._200sdp)
                           .wrapContentHeight()
                           .padding(vertical = appThemeContext.dimensions.padding10)
                   )
               }
           }

           SnackbarHost(
               hostState = snackbarHostState.value,
               modifier = Modifier
                   .align(Alignment.TopCenter))
       }
   }

}