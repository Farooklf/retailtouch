package com.lfssolutions.retialtouch.domain.model.products

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByPriceBreak
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByQuantity
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.utils.DiscountApplied
import com.lfssolutions.retialtouch.utils.DiscountType


data class PosUIState(

    var isLoading : Boolean = false,
    var searchQuery :String = "",
    var showDialog : Boolean = false,
    var isDiscountDialog : Boolean = false,
    var isAppliedDiscountOnTotal : Boolean = false,
    val loginUser: LoginResponse =LoginResponse(),
    var currencySymbol : String = "$",
    var inputDiscount : String = "",
    var inputDiscountError : String? =null,
    var selectedDiscountType : DiscountType = DiscountType.FIXED_AMOUNT,
    var selectedDiscountApplied : DiscountApplied = DiscountApplied.GLOBAL,

    //auth
    val isDiscountGranted :Boolean=false,
    val posInvoiceRounded :Double?=0.0,
    val isSalesTaxInclusive :Boolean=false,
    val invoiceRounding :Double=0.0,

    //global value
    val isCartEmpty :Boolean=false,
    val unsyncInvoices :Long=0,
    val exchange :Boolean=false,
    val globalExchangeActivator :Boolean=false,
    val isPrinterEnable :Boolean=false,
    val globalDiscountIsInPercent :Boolean=false,
    val globalDiscount: Double = 0.0,
    val quantityTotal: Double = 0.0,
    val cartTotal: Double = 0.0,
    val cartTotalWithoutDiscount: Double = 0.0,
    val cartPromotionDiscount: Double = 0.0,
    var cartItemsDiscount : Double = 0.0,
    val grandTotal: Double = 0.0,
    val grandTotalWithoutDiscount :Double=0.0,
    val globalTax :Double=0.0,
    val itemTotal :Double=0.0,
    val itemDiscount :Double=0.0,
    val discountIsInPercent :Boolean=false,
    val promoDiscount :Double?=0.0,

    //Dialog
    val stockList : List<Product> = listOf(),
    val dialogStockList : List<Product> = listOf(),
    var selectedProduct : CRShoppingCartItem = CRShoppingCartItem(),
    var itemPosition : Int = 0,
    var isRemoveDialog : Boolean = false,


    val shoppingCart: List<ProductItem> = listOf(),
    val cartList: MutableList<CRShoppingCartItem> = mutableListOf(),
    var isCallScannedItems : Boolean = false,



    var discounts: Double = 0.0,
    val originalTotal: Double = 0.0,

    //Hold
    var isHoldSaleDialog : Boolean = false,
    var isHoldSalePopup : Boolean = false,
    var salesOnHold : MutableMap<Long, CRSaleOnHold> = hashMapOf(),
    // HashMap to hold collections
    var holdSaleCollections : MutableMap<Int, HeldCollection> = hashMapOf(),
    var currentCollectionId: Int = 1,
    var isDropdownExpanded: Boolean = false,

    //members
    var isMemberDialog: Boolean = false,
    var isCreateMemberDialog: Boolean = false,
    var selectedMember : String = "Select Member",
    var selectedMemberId : Int = 0,
    var searchMember : String = "",
    var selectedMemberGroup : String = "",
    var selectedMemberGroupId : Int = 0,
    var memberCode : String = "",
    var memberCodeError : String? = null,
    var memberName : String = "",
    var memberNameError : String? = null,
    var email : String = "",
    var mobileNo : String = "",
    var address : String = "",
    var zipCode : String = "",
    val memberItem:MemberItem=MemberItem(),
    val memberList : List<MemberItem> = listOf(),
    val memberGroupList : List<MemberGroupItem> = listOf(),

    //location
    val location : Location? =Location(),

    //promotion
    val promotions : MutableList<Promotion> = mutableListOf(),
    val promotionDetails : MutableList<PromotionDetails> = mutableListOf(),
    val promoByPriceBreak :MutableMap<Int, CRPromotionByPriceBreak> = HashMap(emptyMap()),
    val promoByQuantity : MutableMap<Int, CRPromotionByQuantity> = HashMap(emptyMap()),
    var promotionDiscount : Double=0.0,
    val promotionByQuantity :Boolean=false,
    val promotionActive :Boolean=false,

    //Payment
    val showPaymentSuccessDialog: Boolean = false,
    var isPaymentScreen: Boolean = false,
    var isError : Boolean = false,
    var errorMsg : String = "",
    var isPaymentClose : Boolean = false,
    val deliveryTypeList : List<DeliveryType> = listOf(),
    val statusTypeList : List<StatusType> = listOf(),
    val selectedDeliveryType : DeliveryType = DeliveryType(),
    val selectedStatusType : StatusType = StatusType(),
    val remark : String = "",
    val selectedDateTime : String ="",
    var minValue: Double = 1.0,
    var remainingLabel  : String = "Remaining",
    var totalLabel  : String = "Amount To Pay",
    val availablePayments : List<PaymentMethod> = listOf(),
    val selectedPayment: PaymentMethod = PaymentMethod(),
    val selectedPaymentTypesId: Int = 0,
    val paymentTotal: Double = 0.0,
    var remainingBalance : Double = 0.0,
    val selectedPaymentToDelete: Int = 0,
    val showDeletePaymentModeDialog: Boolean = false,
    val isCash: Boolean = false,
    val showPaymentCollectorDialog: Boolean = false,
    val isExecutePosSaving: Boolean = false,
    val startPaymentLib: Boolean = false,
    val roundToDecimal: Int = 2,
    val posPayments: MutableList<PosPayment> = mutableListOf(),
    val createdPayments: MutableList<PaymentMethod> = mutableListOf(),
    val posInvoiceDetails: MutableList<PosInvoiceDetail> = mutableListOf(),
    val showEmailReceiptsDialog: Boolean = false,
    val showPhoneReceiptsDialog: Boolean = false,

    )

data class HeldCollection(
    var collectionId:Int,
    var items: List<ProductItem>,
    var grandTotal: Double
)




