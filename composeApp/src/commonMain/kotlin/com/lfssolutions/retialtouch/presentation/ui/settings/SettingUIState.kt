package com.lfssolutions.retialtouch.presentation.ui.settings


import com.lfssolutions.retialtouch.domain.model.employee.POSEmployee
import com.lfssolutions.retialtouch.utils.AppIcons

data class SettingUIState(
  val isLoading:Boolean=false,
  val serverUrl:String="",
  val tenant:String="",
  val user:String="",
  val terminalCode: String="" ,
  val networkConfig: String ="",
  val appVersion: String = "1.0.0",
  val roundOffOption: Int=0,
  val gridViewOption: Int=0,
  val availableGridViewOptions: List<Int> = listOf(3,4),
  val availableRoundOffOptions: List<Int> = listOf(1,2,3),
  val mergeCartItems: Boolean = true,
  val fastPaymode: Boolean = false,
  val paymentConfirmPopup: Boolean = false,
  val showRoundOffDialog: Boolean = false,
  val showTerminalCodeDialog: Boolean = false,
  val showNetworkConfigDialog: Boolean = false,
  val showGridViewOptionsDialog: Boolean = false,

  val posEmployees: List<POSEmployee> = listOf(),

  //tab
  val tabs: MutableList<TabItem> = mutableListOf(
      TabItem(title ="Main" , icon = AppIcons.homeIcon),
      TabItem(title = "Product", icon = AppIcons.sellingProductIcon),
     /* TabItem(title = "Payment", icon = AppIcons.payment),*/
      TabItem(title = "Employees", icon = AppIcons.employees),
      TabItem(title = "Data Stats", icon = AppIcons.dataStats),
    )
)