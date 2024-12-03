package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleTransactionState
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppDropdownMenu
import com.lfssolutions.retialtouch.presentation.ui.common.BasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.ClickableAppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.viewModels.SaleTransactionViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.end_date
import retailtouch.composeapp.generated.resources.members
import retailtouch.composeapp.generated.resources.start_date
import retailtouch.composeapp.generated.resources.status
import retailtouch.composeapp.generated.resources.transaction
import retailtouch.composeapp.generated.resources.type

object TransactionScreen : Screen {
    @Composable
    override fun Content() {
        TransactionUI()
    }
}

@Composable
fun TransactionUI(
    viewModel: SaleTransactionViewModel = koinInject()
){

    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit){
        viewModel.getAuthDetails()
        viewModel.loadFilterData()
        viewModel.getSales()
    }
    BasicScreen(
        modifier = Modifier.systemBarsPadding(),
        title = stringResource(Res.string.transaction),
        isTablet = appState.isTablet,
        contentMaxWidth = Int.MAX_VALUE.dp,
        onBackClick = {
            NavigatorActions.navigateBack(navigator)
        }
    ){

        if(appState.isPortrait){
            PortraitTransactionScreen(
                screenState=screenState,
                viewModel = viewModel
            )
        }else{
            LandScapeTransactionScreen()
        }


        SnackbarHost(
            hostState = snackbarHostState.value,
            modifier = Modifier
                .align(Alignment.TopCenter))

        AppCircleProgressIndicator(
            isVisible= screenState.isLoading
        )
    }
}

@Composable
fun PortraitTransactionScreen(
    screenState: SaleTransactionState,
    viewModel: SaleTransactionViewModel
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        //Filter first row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically,) {

            //Type
            AppDropdownMenu(
                modifier = Modifier.weight(1f),
                selectedIndex = screenState.type.id,
                label = stringResource(Res.string.type),
                labelExtractor = { it.name },
                items = screenState.typeList,
                onItemSelected = { index, selectedValue ->
                    viewModel.onSelectedType(selectedValue)
                })

            //Status
            AppDropdownMenu(
                modifier = Modifier.weight(1f),
                selectedIndex = screenState.status.id,
                label = stringResource(Res.string.status),
                labelExtractor = { it.name },
                items = screenState.statusList,
                onItemSelected = { index, selectedValue ->
                    viewModel.onSelectedStatus(selectedValue)
                })

        }

        //Members
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically,) {
            //Member
            AppDropdownMenu(
                modifier = Modifier.weight(1f),
                selectedIndex = screenState.member.id,
                label = stringResource(Res.string.members),
                labelExtractor = { it.name },
                items = screenState.memberList,
                onItemSelected = { index, selectedValue ->
                    viewModel.onSelectedMember(selectedValue)
                })
        }

        //Date Range
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically,){
          //start date
            ClickableAppOutlinedTextField(
                value = screenState.startDate,
                label = stringResource(Res.string.start_date),
                placeholder = stringResource(Res.string.start_date),
                leadingIcon = AppIcons.calenderIcon,
                enabled = false,
                singleLine = true,
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.updateDatePickerDialog(
                        newValue = true,
                        isFromDateValue = true
                    )
                }
            )

            ClickableAppOutlinedTextField(
                value = screenState.endDate,
                label = stringResource(Res.string.end_date),
                placeholder = stringResource(Res.string.end_date),
                leadingIcon = AppIcons.calenderIcon,
                enabled = false,
                singleLine = true,
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.updateDatePickerDialog(
                        newValue = true,
                        isFromDateValue = false
                    )
                }
            )
        }

     }
}

@Composable
fun LandScapeTransactionScreen(){

}





