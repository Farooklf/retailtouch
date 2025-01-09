package com.lfssolutions.retialtouch.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources._58mm
import retailtouch.composeapp.generated.resources._80mm
import retailtouch.composeapp.generated.resources.arabic
import retailtouch.composeapp.generated.resources.bluetooth
import retailtouch.composeapp.generated.resources.english
import retailtouch.composeapp.generated.resources.ethernet
import retailtouch.composeapp.generated.resources.french
import retailtouch.composeapp.generated.resources.usb


enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT;
}

enum class DiscountApplied {
    GLOBAL,
    SUB_ITEMS;
}

// Enum for Device Type
enum class DeviceType {
    SMALL_PHONE, LARGE_PHONE, SMALL_TABLET, LARGE_TABLET
}


enum class CustomerDetailsDialogType{
    Email,
    Phone,
}

enum class PaymentCollectorButtonType {
    One,
    Two,
    Three,
    Four,
    Five,
    Six,
    Seven,
    Eight,
    Nine,
    Ten,
    Zero,
    DoubleZero,
    Dot,
    Twenty,
    Fifty,
    Hundred,
    Delete,
    Pay;

    override fun toString(): String {
        return when(this) {
            One -> "1"
            Two -> "2"
            Three -> "3"
            Four -> "4"
            Five -> "5"
            Six -> "6"
            Seven -> "7"
            Eight -> "8"
            Nine -> "9"
            Ten -> "10"
            Zero -> "0"
            DoubleZero -> "00"
            Dot -> "."
            Twenty -> "20"
            Fifty -> "50"
            Hundred -> "100"
            Delete -> ""
            Pay -> "Pay"
        }
    }
}

enum class TemplateType {

    POSInvoice,
    SalesInvoice,
    SalesReturn,
    SalesReceipt,
    PosSettlement,
    Settlement,
    PaymentReceipt,
    StockTransfer,
    SettlementSalesInvoice,
    SettlementTemplateInvoice,
    LabelTemplate;

    fun toInt(): Int {
        return when(this) {
            POSInvoice -> 1
            SalesInvoice -> 2
            SalesReturn -> 3
            SalesReceipt -> 4
            PosSettlement -> 5
            Settlement -> 6
            PaymentReceipt -> 7
            StockTransfer -> 8
            SettlementSalesInvoice -> 9
            SettlementTemplateInvoice -> 10
            LabelTemplate -> 12
        }
    }
}

enum class PrinterType {
    Ethernet,
    USB,
    Bluetooth;

    @Composable
    fun toStringValue(): String {
        val value = when(this) {
            Ethernet -> Res.string.ethernet
            USB -> Res.string.usb
            Bluetooth -> Res.string.bluetooth
        }
        return stringResource(value)
    }
}

enum class PaperSize {
    Size58mm,
    Size80mm;

    @Composable
    fun toStringValue(): String {
        val value = when(this) {
            Size58mm -> Res.string._58mm
            Size80mm -> Res.string._80mm
        }
        return stringResource(value)
    }
}

enum class AppLanguage {
    English,
    Arabic,
    French;

    @Composable
    fun toStringValue(): String {
        return stringResource(
            when (this) {
                English -> Res.string.english
                Arabic -> Res.string.arabic
                French -> Res.string.french
            }
        )
    }
}

