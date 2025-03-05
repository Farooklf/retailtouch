package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.common.AppCircleProgressIndicatorWithMessage
import com.hashmato.retailtouch.presentation.common.AppOutlinedTextField
import com.hashmato.retailtouch.presentation.common.AppPrimaryButton
import com.hashmato.retailtouch.presentation.common.dialogs.ErrorDialog
import com.hashmato.retailtouch.presentation.common.ResponsiveBox
import com.hashmato.retailtouch.presentation.common.ScreenHeaderText
import com.hashmato.retailtouch.presentation.viewModels.LoginViewModel
import com.hashmato.retailtouch.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.location_id
import retailtouch.composeapp.generated.resources.password
import retailtouch.composeapp.generated.resources.retail_pos
import retailtouch.composeapp.generated.resources.server_address
import retailtouch.composeapp.generated.resources.sign_in
import retailtouch.composeapp.generated.resources.tenant_name
import retailtouch.composeapp.generated.resources.user_name
class LoginScreen(val mRTLoginUser: RTLoginUser?=null):Screen{
    @Composable
    override fun Content() {
        val navigator = AppTheme.context.getAppNavigator()
        Login(onNavigateDashBoard = {
            NavigatorActions.navigateToHomeScreen(navigator,true)
        })
    }

    @Composable
    @Preview
    fun Login(
        onNavigateDashBoard: @Composable () -> Unit,
        loginViewModel: LoginViewModel = koinInject()
    ){
        val loginScreenState by loginViewModel.loginScreenState.collectAsState()
        //val syncState by loginViewModel.syncDataState.collectAsState()

        LaunchedEffect(mRTLoginUser){
            if(mRTLoginUser!=null)
              loginViewModel.updateLoginDetails(mRTLoginUser)
        }

        ResponsiveBox(
            modifier = Modifier.fillMaxSize(),
            isForm = true
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                //Screen Header Text
                ScreenHeaderText(
                    label = stringResource(Res.string.retail_pos)
                )

                //server address
                AppOutlinedTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = loginScreenState.server,
                    onValueChange = { url ->
                        loginViewModel.updateServer(url)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    label = stringResource(Res.string.server_address),
                    placeholder = stringResource(Res.string.server_address),
                    singleLine = true,
                    error = loginScreenState.serverError,
                    enabled = !loginScreenState.isLoading
                )

                //tenant
                AppOutlinedTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = loginScreenState.tenant,
                    onValueChange = { tenant ->
                        loginViewModel.updateTenant(tenant)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    label = stringResource(Res.string.tenant_name),
                    placeholder = stringResource(Res.string.tenant_name),
                    singleLine = true,
                    error = loginScreenState.tenantError,
                    enabled = !loginScreenState.isLoading
                )

                //User Name
                AppOutlinedTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = loginScreenState.username,
                    onValueChange = { userName ->
                        loginViewModel.updateUsername(userName)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    placeholder = stringResource(Res.string.user_name),
                    label = stringResource(Res.string.user_name),
                    error = loginScreenState.userNameError,
                    singleLine = true,
                    enabled = !loginScreenState.isLoading
                )



                //User Password
                AppOutlinedTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = loginScreenState.password,
                    onValueChange = { password ->
                        loginViewModel.updatePassword(password)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    label = stringResource(Res.string.password),
                    placeholder = stringResource(Res.string.password),
                    error = loginScreenState.passwordError,
                    singleLine = true,
                    enabled = !loginScreenState.isLoading)

                //Location Id
                AppOutlinedTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    value = loginScreenState.location,
                    onValueChange = { locationId ->
                        loginViewModel.updateLocation(locationId)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    label = stringResource(Res.string.location_id),
                    placeholder = stringResource(Res.string.location_id),
                    singleLine = true,
                    enabled = !loginScreenState.isLoading)

                AppPrimaryButton(
                    enabled = !loginScreenState.isLoading,
                    onClick = {
                        loginViewModel.onLoginClick()
                    },
                    label = stringResource(Res.string.sign_in) ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )

            }

            // Show loader when loading
            if (loginScreenState.isLoading ) {
                AppCircleProgressIndicatorWithMessage(
                    isError = loginScreenState.isLoginError,
                    modifier = Modifier.size(200.dp),
                    errorTitle = loginScreenState.loginErrorTitle,
                    message = loginScreenState.loadingMessage,
                    errorMsg = if (loginScreenState.isLoginError) loginScreenState.loginErrorMessage else null,
                    onErrorTimeout = {
                        loginViewModel.backToLogin() // Clear the error after 3 seconds
                    }
                )
            }

            // Show error dialog if there's an error
            if (loginScreenState.isLoginError && !loginScreenState.isLoading) {
                ErrorDialog(
                    errorMessage = loginScreenState.loginErrorMessage,
                    onDismiss = { loginViewModel.dismissErrorDialog() }
                )
            }

            // Navigate on successful login
            if (loginScreenState.isSuccessfulLogin){
                println("afterLogin ${loginScreenState.isSuccessfulLogin}")
                onNavigateDashBoard()
            }
        }

    }

}


