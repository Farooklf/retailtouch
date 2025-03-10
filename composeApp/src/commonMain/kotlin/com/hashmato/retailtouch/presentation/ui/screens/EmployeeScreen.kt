package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.presentation.common.AppOutlinedTextFieldWithOuterIcon
import com.hashmato.retailtouch.presentation.common.ResponsiveBox
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.presentation.viewModels.EmployeeViewModel
import com.hashmato.retailtouch.utils.LocalAppState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.employee_code
import retailtouch.composeapp.generated.resources.logout
import retailtouch.composeapp.generated.resources.logout_from_this_server
import retailtouch.composeapp.generated.resources.pin



@Composable
fun EmployeeScreen(
    onNavigateLogout: @Composable (RTLoginUser?) -> Unit,
    onDismiss: () -> Unit,
    employeeViewModel: EmployeeViewModel = koinInject()
){
    val employeeScreenState by employeeViewModel.employeeScreenState.collectAsStateWithLifecycle()
    val isLogoutFromServer by employeeViewModel.logoutFromServer.collectAsStateWithLifecycle()
    val mRTUser by employeeViewModel.RTUser.collectAsStateWithLifecycle()
    println("RTUser :$mRTUser")
    val appState = LocalAppState.current
    val paddingValues = if (appState.isTablet) {
        AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
    } else{
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding10
    }

    ResponsiveBox(
        modifier = Modifier.fillMaxSize().background(Color.Transparent),
        isForm = true,
    ){

        Column(modifier = Modifier
            .wrapContentSize()
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(horizontal = paddingValues.first, vertical = paddingValues.second),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                // Lock Icon
                Icon(
                    imageVector = vectorResource(AppIcons.lockIcon),
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(100.dp),
                    tint = AppTheme.colors.appWhite
                )
                Spacer(modifier = Modifier.height(5.dp))

                //Employee Code TextField
                AppOutlinedTextFieldWithOuterIcon(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    value = employeeScreenState.employeeCode,
                    onValueChange = { value ->
                        employeeViewModel.updateEmployeeCode(value)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    label = stringResource(Res.string.employee_code),
                    placeholder = stringResource(Res.string.employee_code),
                    singleLine = true,
                    error = employeeScreenState.employeeCodeError,
                    enabled = !employeeScreenState.isLoading,
                    leadingIcon = AppIcons.empRoleIcon

                )

                //Employee Pin
                AppOutlinedTextFieldWithOuterIcon(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    value = employeeScreenState.pin,
                    onValueChange = { value ->
                        employeeViewModel.updateEmployeePin(value)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            employeeViewModel.onClick()
                        }
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    label = stringResource(Res.string.pin),
                    placeholder = stringResource(Res.string.pin),
                    singleLine = true,
                    error = employeeScreenState.pinError,
                    enabled = !employeeScreenState.isLoading,
                    leadingIcon = AppIcons.pinIcon
                )
                Spacer(modifier = Modifier.height(5.dp))
                //Logout From Server
                Row(modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.End)
                    .clickable{
                        employeeViewModel.logoutFromThisServer()
                   },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = vectorResource(AppIcons.logoutIcon),
                        contentDescription = stringResource(Res.string.logout),
                        modifier = Modifier.size(24.dp),
                        tint = AppTheme.colors.appWhite
                    )

                    Text(
                        text = stringResource(Res.string.logout_from_this_server),
                        color = AppTheme.colors.appWhite,
                        style = AppTheme.typography.bodyMedium(),
                        modifier = Modifier.padding(end = 2.dp)
                    )

                }
            }

            if(employeeScreenState.isEmployeeLoginSuccess){
                onDismiss()
            }

            if(isLogoutFromServer){
                onNavigateLogout(mRTUser)
            }
        }
    }


}