package com.hashmato.retailtouch.utils


import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.hashmato.retailtouch.domain.model.AppState
import com.hashmato.retailtouch.utils.DoubleExtension.roundTo
import kotlinx.serialization.json.Json
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.amount_error
import retailtouch.composeapp.generated.resources.cashier
import retailtouch.composeapp.generated.resources.delivered
import retailtouch.composeapp.generated.resources.delivery
import retailtouch.composeapp.generated.resources.desc_error
import retailtouch.composeapp.generated.resources.drawer
import retailtouch.composeapp.generated.resources.ic_add
import retailtouch.composeapp.generated.resources.ic_app_version
import retailtouch.composeapp.generated.resources.ic_back_arrow_circle
import retailtouch.composeapp.generated.resources.ic_calendar
import retailtouch.composeapp.generated.resources.ic_cancel
import retailtouch.composeapp.generated.resources.ic_card
import retailtouch.composeapp.generated.resources.ic_cart_merge
import retailtouch.composeapp.generated.resources.ic_cash
import retailtouch.composeapp.generated.resources.ic_cashier
import retailtouch.composeapp.generated.resources.ic_categories
import retailtouch.composeapp.generated.resources.ic_category
import retailtouch.composeapp.generated.resources.ic_chart
import retailtouch.composeapp.generated.resources.ic_check
import retailtouch.composeapp.generated.resources.ic_cross
import retailtouch.composeapp.generated.resources.ic_data_stats
import retailtouch.composeapp.generated.resources.ic_discount
import retailtouch.composeapp.generated.resources.ic_dollar
import retailtouch.composeapp.generated.resources.ic_down
import retailtouch.composeapp.generated.resources.ic_drawer
import retailtouch.composeapp.generated.resources.ic_e_exchange
import retailtouch.composeapp.generated.resources.ic_edit
import retailtouch.composeapp.generated.resources.ic_employees
import retailtouch.composeapp.generated.resources.ic_error
import retailtouch.composeapp.generated.resources.ic_exclamation
import retailtouch.composeapp.generated.resources.ic_fast_payment
import retailtouch.composeapp.generated.resources.ic_file_excel
import retailtouch.composeapp.generated.resources.ic_home
import retailtouch.composeapp.generated.resources.ic_indian_rupee
import retailtouch.composeapp.generated.resources.ic_ip_address
import retailtouch.composeapp.generated.resources.ic_language_exchange
import retailtouch.composeapp.generated.resources.ic_locations
import retailtouch.composeapp.generated.resources.ic_login_lock
import retailtouch.composeapp.generated.resources.ic_member
import retailtouch.composeapp.generated.resources.ic_membership
import retailtouch.composeapp.generated.resources.ic_minus
import retailtouch.composeapp.generated.resources.ic_minus_circle
import retailtouch.composeapp.generated.resources.ic_pause
import retailtouch.composeapp.generated.resources.ic_payment
import retailtouch.composeapp.generated.resources.ic_payment_card
import retailtouch.composeapp.generated.resources.ic_payout
import retailtouch.composeapp.generated.resources.ic_percentage
import retailtouch.composeapp.generated.resources.ic_person
import retailtouch.composeapp.generated.resources.ic_pin
import retailtouch.composeapp.generated.resources.ic_plus
import retailtouch.composeapp.generated.resources.ic_power
import retailtouch.composeapp.generated.resources.ic_printer
import retailtouch.composeapp.generated.resources.ic_promotion_discount
import retailtouch.composeapp.generated.resources.ic_receipt
import retailtouch.composeapp.generated.resources.ic_roundoff
import retailtouch.composeapp.generated.resources.ic_search
import retailtouch.composeapp.generated.resources.ic_selling_products
import retailtouch.composeapp.generated.resources.ic_server
import retailtouch.composeapp.generated.resources.ic_settings
import retailtouch.composeapp.generated.resources.ic_settlement
import retailtouch.composeapp.generated.resources.ic_star
import retailtouch.composeapp.generated.resources.ic_stock
import retailtouch.composeapp.generated.resources.ic_sync
import retailtouch.composeapp.generated.resources.ic_sync_timer
import retailtouch.composeapp.generated.resources.ic_trash
import retailtouch.composeapp.generated.resources.ic_unlink
import retailtouch.composeapp.generated.resources.ic_user_add
import retailtouch.composeapp.generated.resources.ic_wifi
import retailtouch.composeapp.generated.resources.logout
import retailtouch.composeapp.generated.resources.members
import retailtouch.composeapp.generated.resources.pay_to_error
import retailtouch.composeapp.generated.resources.payout
import retailtouch.composeapp.generated.resources.pending
import retailtouch.composeapp.generated.resources.printer
import retailtouch.composeapp.generated.resources.receipt
import retailtouch.composeapp.generated.resources.rental_collection
import retailtouch.composeapp.generated.resources.rental_delivery
import retailtouch.composeapp.generated.resources.returned
import retailtouch.composeapp.generated.resources.self_collection
import retailtouch.composeapp.generated.resources.self_connected
import retailtouch.composeapp.generated.resources.settings
import retailtouch.composeapp.generated.resources.settlement
import retailtouch.composeapp.generated.resources.stock
import retailtouch.composeapp.generated.resources.sync
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

val JsonObj = Json { encodeDefaults = true }
// Define the LocalAppState CompositionLocal
const val chars = "1234567890"
val random = Random.Default

val LocalAppState = compositionLocalOf<AppState> {
    error("No AppState provided")
}

fun String?.capitalizeFirstChar(): String {
    return this?.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    } ?: ""
}

fun getRandomString(length: Int): String {
    return (1..length)
        .map { chars[random.nextInt(chars.length)] }
        .joinToString("")
}

fun formatAmountForPrint(amount: Double?) :String{
    return NumberFormatter().format(amount?:0.0)
}

fun formatAmountForUI(amount: Double?,currencySymbol: String) :String{
    return  "${currencySymbol}${amount?.roundTo(2)}"
}

fun formatPrice(amount: Double?,currencySymbol:String) : String {
    return "${NumberFormatter().format(amount?:0.0)}$currencySymbol"
}

fun roundTwoDecimalPlaces(amount:Double) :Double{
    return amount.roundTo(2)
}

fun roundFourDecimalPlaces(amount:Double) :Double{
    return amount.roundTo(4)
}

object AppIcons {

    //Home screen icons
    val cashierIcon = Res.drawable.ic_cashier
    val membershipIcon = Res.drawable.ic_membership
    val stockIcon = Res.drawable.ic_stock
    val receiptIcon = Res.drawable.ic_receipt
    val syncIcon = Res.drawable.ic_sync
    val settlementIcon = Res.drawable.ic_settlement
    val payoutIcon = Res.drawable.ic_payout
    val printerIcon = Res.drawable.ic_printer
    val drawerIcon = Res.drawable.ic_drawer
    val settingIcon = Res.drawable.ic_settings
    val logoutIcon = Res.drawable.ic_power
    val categoryIcon = Res.drawable.ic_category

    val errorIcon = Res.drawable.ic_error
    val e_exchangeIcon = Res.drawable.ic_e_exchange
    val lockIcon = Res.drawable.ic_login_lock
    val memberIcon = Res.drawable.ic_member
    val locationIcon = Res.drawable.ic_locations
    val wifiIcon = Res.drawable.ic_wifi
    val addCustomer = Res.drawable.ic_user_add
    val empRoleIcon = Res.drawable.ic_person
    val minusIcon = Res.drawable.ic_minus
    val minusCircleIcon = Res.drawable.ic_minus_circle
    val plusIcon = Res.drawable.ic_plus
    val addIcon = Res.drawable.ic_add
    val pauseIcon = Res.drawable.ic_pause
    val percentageIcon = Res.drawable.ic_percentage
    val dollarIcon = Res.drawable.ic_dollar
    val indianRIcon = Res.drawable.ic_indian_rupee
    val pinIcon = Res.drawable.ic_pin
    val closeIcon = Res.drawable.ic_cross
    val cancelIcon = Res.drawable.ic_cancel
    val searchIcon = Res.drawable.ic_search
    val removeIcon = Res.drawable.ic_trash
    val applyIcon = Res.drawable.ic_check
    val excelIcon = Res.drawable.ic_file_excel
    val paymentIcon = Res.drawable.ic_payment_card
    val discountIcon = Res.drawable.ic_discount
    val promotionIcon = Res.drawable.ic_promotion_discount
    val downArrowIcon = Res.drawable.ic_down
    val calenderIcon by lazy { Res.drawable.ic_calendar }
    val calculatorIcon = Res.drawable.ic_settings
    val scanIcon by lazy {  Res.drawable.ic_settings }
    val starIcon by lazy {  Res.drawable.ic_star }
    val backIcon by lazy { Res.drawable.ic_back_arrow_circle }
    val cardIcon by lazy { Res.drawable.ic_card}
    val cashIcon by lazy { Res.drawable.ic_cash}
    val editIcon by lazy { Res.drawable.ic_edit}
    val employees by lazy { Res.drawable.ic_employees}
    val homeIcon by lazy { Res.drawable.ic_home}
    val gridIcon by lazy { Res.drawable.ic_categories}
    val payment by lazy { Res.drawable.ic_payment}
    val dataStats by lazy { Res.drawable.ic_data_stats}
    val exportIcon by lazy { Res.drawable.ic_exclamation}
    val serverIcon by lazy { Res.drawable.ic_server}
    val unlinkIcon by lazy { Res.drawable.ic_unlink}
    val languageIcon by lazy { Res.drawable.ic_language_exchange}
    val versionIcon by lazy { Res.drawable.ic_app_version}
    val cartMergeIcon by lazy { Res.drawable.ic_cart_merge}
    val sellingProductIcon by lazy { Res.drawable.ic_selling_products}
    val IPAddressIcon by lazy { Res.drawable.ic_ip_address}
    val fastPaymentIcon by lazy { Res.drawable.ic_fast_payment}
    val roundOffIcon by lazy { Res.drawable.ic_roundoff}
    val chartIcon by lazy { Res.drawable.ic_chart}
    val pendingIcon by lazy { Res.drawable.ic_exclamation}
    val syncTimeIcon by lazy { Res.drawable.ic_sync_timer}
}

object HomeItemId{
    const val CASHIER_ID = 1
    const val MEMBER_ID = 2
    const val STOCK_ID = 3
    const val RECEIPT_ID = 4
    const val SYNC_ID = 5
    const val SETTLEMENT_ID = 6
    const val PAYOUT_ID = 7
    const val PRINTER_ID = 8
    const val DRAWER_ID = 9
    const val SETTING_ID = 10
    const val LOGOUT_ID = 11
}

object AppStrings{
    val cashier = Res.string.cashier
    val members = Res.string.members
    val stock = Res.string.stock
    val receipt = Res.string.receipt
    val sync = Res.string.sync
    val settlement = Res.string.settlement
    val payout = Res.string.payout
    val printer = Res.string.printer
    val settings = Res.string.settings
    val drawer = Res.string.drawer
    val logout = Res.string.logout

    val delivery = Res.string.delivery
    val self_collection = Res.string.self_collection
    val rental_delivery = Res.string.rental_delivery
    val rental_collection = Res.string.rental_collection
    val pending = Res.string.pending
    val delivered = Res.string.delivered
    val self_connected = Res.string.self_connected
    val returned = Res.string.returned
    val errorAmount by lazy { Res.string.amount_error}
    val errorDescription by lazy { Res.string.desc_error}
    val errorPayTo  by lazy { Res.string.pay_to_error}


}

object AppConstants{
    const val LOCATION_ERROR_TITLE = "LOCATION FETCHING FAILED"
    const val EMPLOYEE_ERROR_TITLE = "EMPLOYEE FETCHING FAILED"
    const val EMPLOYEE_ROLE_ERROR_TITLE = "EMPLOYEE ROLE FETCHING FAILED"
    const val TERMINAL_ERROR_TITLE = "TERMINAL FETCHING FAILED"
    const val NEXT_SALE_ERROR_TITLE = "NEXT SALE FETCHING FAILED"
    const val MEMBER_ERROR_TITLE = "MEMBER FETCHING FAILED"
    const val INVENTORY_ERROR_TITLE = "INVENTORY FETCHING FAILED"
    const val MENU_CATEGORY_ERROR_TITLE = "MENU CATEGORIES FETCHING FAILED"
    const val MENU_PRODUCTS_ERROR_TITLE = "MENU PRODUCTS FETCHING FAILED"
    const val PROMOTIONS_ERROR_TITLE = "PROMOTION FETCHING FAILED"
    const val PAYMENT_TYPE_ERROR_TITLE = "PAYMENT TYPE FETCHING FAILED"
    const val SYNC_CHANGES_ERROR_TITLE = "SYNC CHANGES FAILED"
    const val SYNC_SALES_ERROR_TITLE = "SYNC SALES FAILED"
    const val SYNC_TEMPLATE_ERROR_TITLE = "SYNC TEMPLATE FAILED"

    const val POS_SCREEN = "pos"
    const val MEMBER_SCREEN = "member"
    const val PAYMENT_SCREEN = "payment"
    const val MEMBER = "MEMBER"
    const val MEMBER_GROUP = "MEMBERGROUP"
    const val PRODUCT = "PRODUCT"
    const val CATEGORY = "CATEGORY"
    const val MENU = "MENU"
    const val INVOICE = "INVOICE"
    const val PROMOTION = "PROMOTION"
    const val RECEIPT_TEMPLATE = "RECEIPTTEMPLATE"
    const val PAYMENT_TYPE = "PAYMENTTYPE"


    const val HOME_GRID = 4
    const val MIN_SCREEN_WIDTH = 600
    // Define your screen width breakpoints
    val SMALL_PHONE_MAX_WIDTH = 360.dp
    val LARGE_PHONE_MAX_WIDTH = 600.dp
    val SMALL_TABLET_MAX_WIDTH = 840.dp

}

object DoubleExtension{


    fun Double.roundTo(dec: Int = 2): Double {
        val multiplier = 10.0.pow(dec)
        return round(this * multiplier) / multiplier
    }

    fun Double.calculatePercentageByValue(discountedPrice: Double): Double {
        return ((this - discountedPrice) / this) * 100
    }

    fun Double.calculatePercentage(percentage: Double): Double {
        return if (percentage in 0.0..100.0) {
            val discountAmount = (percentage / 100) * this
            discountAmount
        } else {
            0.0 // Handle invalid discount percentages
        }
    }

    fun Double.calculateDiscountPercentage(discountPercentage: Double): Double {
        return if (discountPercentage in 0.0..100.0) {
            val discountAmount = (discountPercentage / 100) * this
            discountAmount
        } else {
            0.0 // Handle invalid discount percentages
        }
    }



}

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>

