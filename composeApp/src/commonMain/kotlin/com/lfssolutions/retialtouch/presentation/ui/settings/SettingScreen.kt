package com.lfssolutions.retialtouch.presentation.ui.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.ActionTextFiledDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.AppSwitch
import com.lfssolutions.retialtouch.presentation.ui.common.BasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.GridViewOptionsDialog
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.RoundOffOptionsDialog
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.language.SelectLanguageDialog
import com.lfssolutions.retialtouch.presentation.ui.common.InputType
import com.lfssolutions.retialtouch.presentation.ui.common.fillScreenHeight
import com.lfssolutions.retialtouch.sync.SyncViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.app_version
import retailtouch.composeapp.generated.resources.cart_item_merge
import retailtouch.composeapp.generated.resources.cart_item_merge_description
import retailtouch.composeapp.generated.resources.categories
import retailtouch.composeapp.generated.resources.confirm_popup_description
import retailtouch.composeapp.generated.resources.confirm_popup_title
import retailtouch.composeapp.generated.resources.disconnect_device
import retailtouch.composeapp.generated.resources.ean_codes
import retailtouch.composeapp.generated.resources.error_log
import retailtouch.composeapp.generated.resources.export_log
import retailtouch.composeapp.generated.resources.fast_paymode
import retailtouch.composeapp.generated.resources.fast_paymode_description
import retailtouch.composeapp.generated.resources.general
import retailtouch.composeapp.generated.resources.grid_view_options
import retailtouch.composeapp.generated.resources.grid_view_options_description
import retailtouch.composeapp.generated.resources.inventory
import retailtouch.composeapp.generated.resources.ip_address_with_port
import retailtouch.composeapp.generated.resources.language
import retailtouch.composeapp.generated.resources.last_sync
import retailtouch.composeapp.generated.resources.logged_in_staff
import retailtouch.composeapp.generated.resources.master_user
import retailtouch.composeapp.generated.resources.menu_items
import retailtouch.composeapp.generated.resources.menu_settings
import retailtouch.composeapp.generated.resources.misc
import retailtouch.composeapp.generated.resources.network_config
import retailtouch.composeapp.generated.resources.payment_settings
import retailtouch.composeapp.generated.resources.pos_link
import retailtouch.composeapp.generated.resources.products
import retailtouch.composeapp.generated.resources.role_str
import retailtouch.composeapp.generated.resources.round_off_description
import retailtouch.composeapp.generated.resources.round_off_option
import retailtouch.composeapp.generated.resources.rsync_timer
import retailtouch.composeapp.generated.resources.run_complete_sync
import retailtouch.composeapp.generated.resources.sales
import retailtouch.composeapp.generated.resources.sales_pending
import retailtouch.composeapp.generated.resources.save
import retailtouch.composeapp.generated.resources.server
import retailtouch.composeapp.generated.resources.settings
import retailtouch.composeapp.generated.resources.staff_list
import retailtouch.composeapp.generated.resources.status
import retailtouch.composeapp.generated.resources.superuser
import retailtouch.composeapp.generated.resources.sync_progress
import retailtouch.composeapp.generated.resources.sync_staff
import retailtouch.composeapp.generated.resources.tenant_name
import retailtouch.composeapp.generated.resources.unlink


object SettingScreen : Screen {

    @Composable
    override fun Content() {
       SettingUI()
    }


    @Composable
    private fun SettingUI(
        viewModel : SettingViewModel = koinInject()
    ) {

        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
        val navigator = LocalNavigator.currentOrThrow
        val appState = LocalAppState.current
        val state by viewModel.settingUiState.collectAsStateWithLifecycle()

        BasicScreen(
            modifier = Modifier.systemBarsPadding(),
            screenBackground = AppTheme.colors.screenBackground,
            title = stringResource(Res.string.settings),
            isTablet = appState.isTablet,
            contentMaxWidth = Int.MAX_VALUE.dp,
            onBackClick = {
                navigator.pop()
            }
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillScreenHeight()
                    .align(Alignment.TopCenter)
            ){
                val scope = rememberCoroutineScope()
                val pagerState = rememberPagerState(pageCount = { state.tabs.size })
                val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

                // Scrollable Tabs Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    containerColor = AppTheme.colors.secondaryBg,
                    contentColor = AppTheme.colors.textPrimary,
                    modifier = Modifier.fillMaxWidth().background(
                        color = AppTheme.colors.secondaryBg,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp) // Custom background shape
                    ).padding(top = 5.dp),
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                            color = AppTheme.colors.textPrimary,
                            height = 4.dp
                        )
                    }
                ){
                    state.tabs.forEachIndexed  { index, currentTab ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            selectedContentColor=AppTheme.colors.textPrimary,
                            unselectedContentColor =AppTheme.colors.textDarkGrey,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = currentTab.title,
                                    style = if(index==selectedTabIndex.value) AppTheme.typography.bodyBold() else AppTheme.typography.bodyMedium()
                                )
                            },
                            icon = {
                               Icon(
                                   imageVector = vectorResource(currentTab.icon) ,
                                   contentDescription = currentTab.title,
                                   modifier = Modifier.size(AppTheme.dimensions.standerIcon),
                                   )
                                //tint = if(index==selectedTabIndex.value) AppTheme.colors.textPrimary else AppTheme.colors.textDarkGrey
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)

                ){ page ->
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        when (page) {
                            0 -> SettingMainPage(state = state,viewModel= viewModel)
                            1 -> SettingProductPage(state = state,viewModel= viewModel)
                            2 -> SettingEmployeesPage(state = state,viewModel= viewModel)
                            3 -> SettingDataStatsPage(state = state,viewModel= viewModel)
                        }
                    }
                }
            }

         }



        ActionTextFiledDialog(
            isVisible = state.showNetworkConfigDialog,
            value = state.networkConfig,
            title = stringResource(Res.string.network_config),
            onCloseDialog = {
                viewModel.updateNetworkConfigDialogVisibility(false)
            },
            onDialogResult = {
                viewModel.updateNetworkConfig(it)
            }
        )



        GridViewOptionsDialog(
            title = stringResource(Res.string.grid_view_options),
            isVisible = state.showGridViewOptionsDialog,
            values = state.availableGridViewOptions,
            selectedValue = state.gridViewOption,
            onCloseDialog = { viewModel.updateGridViewOptionsDialogVisibility(false) },
            onDialogResult = { viewModel.updateGridViewOption(it) }
        )

        RoundOffOptionsDialog(
            title = stringResource(Res.string.round_off_option),
            isVisible = state.showRoundOffDialog,
            values = state.availableRoundOffOptions,
            selectedValue = state.roundOffOption,
            onCloseDialog = { viewModel.updateRoundOffOptionsDialogVisibility(false) },
            onDialogResult = { viewModel.updateRoundOffOption(it) }
        )

    }


    @Composable
    fun CustomTabIndicator(currentTabPosition: TabPosition) {
        val indicatorWidth by animateDpAsState(targetValue = currentTabPosition.width)
        val indicatorOffset by animateDpAsState(targetValue = currentTabPosition.left)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
                Modifier
                    .offset(x = indicatorOffset)
                    .width(indicatorWidth)
                    .height(2.dp)
                    .background(AppTheme.colors.textPrimary)
            )
        }
    }

    @Composable
    fun SettingMainPage(state: SettingUIState, viewModel: SettingViewModel) {
        val navigator = LocalNavigator.currentOrThrow
        val isLogoutFromServer by viewModel.logoutFromServer.collectAsStateWithLifecycle()

        if(isLogoutFromServer){
            NavigatorActions.navigateToLoginScreen(navigator)
        }

        SelectLanguageDialog(
            isVisible = state.showSelectLanguageDialog,
            selectedLanguage=state.selectedLanguage,
            onDismiss = { viewModel.updateSelectLanguageDialogVisibility(false) },
            onSelectLanguage = {language->
                viewModel.changeLanguage(language)
            }
        )

        //Language
        SettingsGroupItem(
            title = stringResource(Res.string.general)
        ){
            SettingsItem(
                title = stringResource(Res.string.language),
                description = state.selectedLanguage.name,
                icon = AppIcons.languageIcon,
                isSwitchable = false,
                showDivider = false,
                onClick = {
                    viewModel.updateSelectLanguageDialogVisibility(true)
                }
            )
        }


        /*SettingsGroupItem(
            title = stringResource(Res.string.error_log)
        ){
            SettingsItem(
                title = stringResource(Res.string.export_log),
                icon = AppIcons.exportIcon,
                onClick = {},
                isSwitchable = false,
                showDivider = false
            )
        }*/


        //network_config
        SettingsGroupItem(
                title = stringResource(Res.string.network_config)
        ){
            SettingsItem(
                title = stringResource(Res.string.ip_address_with_port),
                description = state.networkConfig,
                icon = AppIcons.serverIcon,
                onClick = {
                    viewModel.updateNetworkConfigDialogVisibility(true)
                },
                isSwitchable = false,
                showDivider = false
            )
        }

        //App Version
        SettingsGroupItem(
            title = stringResource(Res.string.app_version)
        ){
            SettingsItem(
                title = state.appVersion,
                icon = AppIcons.versionIcon,
                onClick = {
                },
                isSwitchable = false,
                showDivider = false
            )
        }

        //POS Link
        SettingsGroupItem(
            title = stringResource(Res.string.pos_link)
        ){
            SettingsItem(
                title = stringResource(Res.string.server),
                description = state.serverUrl,
                icon = AppIcons.serverIcon,
                onClick = {

                },
                isSwitchable = false
            )

            SettingsItem(
                title = stringResource(Res.string.tenant_name),
                description = state.tenant.uppercase(),
                icon = AppIcons.empRoleIcon,
                onClick = {

                },
                isSwitchable = false
            )

            SettingsItem(
                title = stringResource(Res.string.master_user),
                description = state.user.uppercase(),
                icon = AppIcons.empRoleIcon,
                onClick = {

                },
                isSwitchable = false
            )

            SettingsItem(
                title = stringResource(Res.string.unlink),
                description = stringResource(Res.string.disconnect_device),
                icon = AppIcons.unlinkIcon,
                onClick = {
                  viewModel.logoutFromThisServer()
                },
                isSwitchable = false,
                showDivider = false
            )

        }
    }

    @Composable
    fun SettingProductPage(state: SettingUIState, viewModel: SettingViewModel) {

       val roundOffText = when (state.roundOffOption) {
            1 -> "Default"
            2 -> "Round Up"
            3 -> "Round Down"
            else -> stringResource(Res.string.round_off_description)
        }
        //Menu Setting
        SettingsGroupItem(title = stringResource(Res.string.menu_settings)){
            SettingsItem(
                title = stringResource(Res.string.grid_view_options),
                description = stringResource(Res.string.grid_view_options_description),
                icon = AppIcons.gridIcon,
                onClick = {
                    viewModel.updateGridViewOptionsDialogVisibility(true)
                },
                isSwitchable = false,
            )

            SettingsItem(
                title = stringResource(Res.string.cart_item_merge),
                description = stringResource(Res.string.cart_item_merge_description),
                icon = AppIcons.cartMergeIcon,
                onClick = { viewModel.updateMergeCartItems() },
                checked = state.mergeCartItems,
                showDivider = false
            )
        }

        //Payment Setting
        /*SettingsGroupItem(title = stringResource(Res.string.payment_settings)){

            SettingsItem(
                title = stringResource(Res.string.confirm_popup_title),
                description = stringResource(Res.string.confirm_popup_description),
                icon = AppIcons.payment,
                onClick = { viewModel.updatePaymentConfirmPopup() },
                checked = state.paymentConfirmPopup
            )

            SettingsItem(
                title = stringResource(Res.string.fast_paymode),
                description = stringResource(Res.string.fast_paymode_description),
                icon = AppIcons.fastPaymentIcon,
                onClick = { viewModel.updateFastPayMode() },
                checked = state.fastPaymode
            )

            SettingsItem(
                title = stringResource(Res.string.round_off_option),
                description = roundOffText,
                icon = AppIcons.roundOffIcon,
                isSwitchable = false,
                showDivider = false,
                onClick = { viewModel.updateRoundOffOptionsDialogVisibility(true) }
            )
        }*/
    }


    @Composable
    fun SettingEmployeesPage(state: SettingUIState, viewModel: SettingViewModel) {

        //Employees
        SettingsGroupItem(
            title = stringResource(Res.string.staff_list)
        ){
           state.posEmployees.forEachIndexed{ index,employee->
               val currentStaff = if(employee.isPosEmployee) "- ${stringResource(Res.string.logged_in_staff)}" else ""
               val employeeRole = if(employee.isAdmin) "${employee.employeeRoleName.lowercase()} , ${stringResource(Res.string.superuser)}" else employee.employeeRoleName.lowercase()

               SettingsItem(
                   title = "${index+1}. ${employee.employeeName.uppercase()} $currentStaff ",
                   description =  stringResource(Res.string.role_str,employeeRole),
                   icon = AppIcons.empRoleIcon,
                   onClick = {

                   },
                   isSwitchable = false,
                   showDivider = state.posEmployees.size!=(index+1)
               )
           }
        }

        //Sync staff
        SettingsGroupItem(
            title = stringResource(Res.string.misc)
        ){
            SettingsItem(
                title = stringResource(Res.string.sync_staff),
                icon = AppIcons.syncIcon,
                onClick = {
                  viewModel.syncStaff()
                },
                isSwitchable = false,
                showDivider = false,
                syncInProgress = state.syncLoader
            )
        }
    }

    @Composable
    fun SettingDataStatsPage(state: SettingUIState, viewModel: SettingViewModel,syncViewModel: SyncViewModel= koinInject()) {
        val syncDataState by syncViewModel.syncDataState.collectAsStateWithLifecycle()

        LaunchedEffect(syncDataState.syncComplete){
            if(syncDataState.syncComplete){
                viewModel._readStats()
                syncViewModel.updateSyncCompleteStatus(false)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                // Stop the timer when the composable is removed from the composition
                syncViewModel.stopPeriodicSync()
            }
        }

        ActionTextFiledDialog(
            isVisible = state.showSyncTimerDialog,
            value = state.reSyncTime.toString(),
            title = stringResource(Res.string.rsync_timer),
            inputType = InputType.OnlyDigital,
            onCloseDialog = {
                viewModel.updateSyncTimerDialogVisibility(false)
            },
            onDialogResult = {
                viewModel.updateReSyncTime(it)
                syncViewModel.updateReSyncTime(it.toInt())
            }
        )

        //Inventory
        SettingsGroupItem(
            title = stringResource(Res.string.inventory)
        ){
            SettingsItem(
                title = stringResource(Res.string.products),
                description = "${state.statesInventory}",
                icon = AppIcons.chartIcon,
                isSwitchable = false
            )

            SettingsItem(
                title = stringResource(Res.string.categories),
                description = "${state.statsMenuCategories}",
                icon = AppIcons.chartIcon,
                isSwitchable = false
            )

            SettingsItem(
                title = stringResource(Res.string.menu_items),
                description = "${state.statsMenuItems}",
                icon = AppIcons.chartIcon,
                isSwitchable = false
            )

            SettingsItem(
                title = stringResource(Res.string.ean_codes),
                description = "${state.statsBarcodes}",
                icon = AppIcons.chartIcon,
                isSwitchable = false,
                showDivider = false
            )
        }

        //Sales
        SettingsGroupItem(
            title = stringResource(Res.string.sales)
        ){
            SettingsItem(
                title = stringResource(Res.string.sales_pending),
                description = "${state.statsUnSyncedSales}",
                icon = AppIcons.pendingIcon,
                isSwitchable = false,
                showDivider = false
            )
        }

        //Sales
        SettingsGroupItem(
            title = stringResource(Res.string.status)
        ){
            val (syncTitle,syncDescription)= if(syncDataState.syncInProgress) stringResource(Res.string.sync_progress) to "(${syncDataState.syncCount} /8 ${syncDataState.syncProgressStatus})" else stringResource(Res.string.last_sync) to state.statsLastSyncTs

            SettingsItem(
                title = syncTitle,
                description = syncDescription,
                icon = AppIcons.syncIcon,
                isSwitchable = false,
                syncInProgress = syncDataState.syncInProgress
            )
            SettingsItem(
                title = stringResource(Res.string.rsync_timer),
                description = "${state.reSyncTime} minute",
                icon = AppIcons.syncTimeIcon,
                isSwitchable = false,
                showDivider = false,
                onClick = {
                   viewModel.updateSyncTimerDialogVisibility(true)
                }
            )
        }

        Column(modifier = Modifier.fillMaxWidth().padding(2.dp), verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally){

            AppPrimaryButton(
                label = stringResource(Res.string.run_complete_sync),
                leftIcon = AppIcons.syncIcon,
                backgroundColor = AppTheme.colors.primaryColor,
                disabledBackgroundColor = AppTheme.colors.primaryColor,
                syncInProgress = syncDataState.syncInProgress,
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .wrapContentHeight(),
                onClick = {
                    syncViewModel.reSync(true)
                }
            )

        }
    }

    @Composable
    private fun SettingsGroupItem(
        title: String,
        content: @Composable ColumnScope.() -> Unit = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {

            Text(
                text = title,
                modifier = Modifier.padding(start = 20.dp, bottom = 10.dp),
                style = AppTheme.typography.bodyMedium(),
                color = AppTheme.colors.textPrimary
            )

            Card(modifier = Modifier.fillMaxWidth().padding(5.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBgColor),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ){
                content()
            }
        }
    }

    @Composable
    private fun SettingsItem(
        title: String,
        icon: DrawableResource?=null,
        description: String? = null,
        onClick: () -> Unit = {},
        isSwitchable: Boolean = true,
        checked: Boolean = false,
        showDivider: Boolean = true,
        syncInProgress: Boolean = false,
    ) {
        val rotation = remember { Animatable(0f) }
        LaunchedEffect(syncInProgress) {
            if (syncInProgress) {
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            } else {
                rotation.snapTo(0f)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(2.dp), verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        role = Role.Button,
                        onClick = onClick
                    )
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = vectorResource(icon),
                        contentDescription = title,
                        tint = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.size(AppTheme.dimensions.standerIcon).rotate(rotation.value)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleNormal(),
                        color = AppTheme.colors.textDarkGrey
                    )

                    description?.let {
                        Text(
                            text = it ,
                            style = AppTheme.typography.bodyNormal().copy(fontSize = 14.sp),
                            color = AppTheme.colors.textDarkGrey.copy(alpha = .8f)
                        )
                    }
                }

                if (isSwitchable) {
                    AppSwitch(
                        checked = checked,
                        onCheckedChange = { onClick() }
                    )
                }
            }

            if (showDivider) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppTheme.colors.listBorderColor,
                    thickness = 1.dp
                )
            }
        }
    }

}
