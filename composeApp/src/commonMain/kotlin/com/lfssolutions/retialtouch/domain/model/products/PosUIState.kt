package com.lfssolutions.retialtouch.domain.model.products

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.menu.StockCategory
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
    var currencySymbol : String = "$",

    //CATEGORY AND MENU
    val showCartLoader: Boolean = false,
    val isList: Boolean = false,
    val isRtl: Boolean = false,
    val gridColumnCount: Int = 3,
    val selectedCategoryId: Int = -1,
    val cartValue: Double = 0.0,
    val cartSize: Int = 0,
    val categories: List<StockCategory> = emptyList(),
    val menuProducts: List<Stock> = emptyList(),
    val currentCategory:StockCategory=StockCategory(),
    val menuItemIndex: MutableMap<Int, Int> = mutableMapOf(),
    val loadingProductContent: Boolean = true,
    val animatedProductCard: AnimatedProductCard? = null,
    val shoppingCart: List<ProductItem> = listOf(),
    val cartList: MutableList<CartItem> = mutableListOf(),
    val stockList : List<Product> = listOf(),
    val dialogStockList : List<Product> = listOf(),
    var selectedProduct : CartItem = CartItem(),
    var itemPosition : Int = 0,
    var isRemoveDialog : Boolean = false,
    var isCallScannedItems : Boolean = false,
    val originalTotal: Double = 0.0,
    var showItemRemoveDialog : Boolean = false,

    //auth
    val loginUser: LoginResponse = LoginResponse(),
    val isDiscountGranted :Boolean=false,
    val posInvoiceRounded :Double?=0.0,
    val isSalesTaxInclusive :Boolean=false,
    val invoiceRounding :Double=0.0,

    //global value
    val syncInProgress :Boolean=false,
    val isCartEmpty :Boolean=false,
    val unSyncInvoices : Long=0,
    val exchange :Boolean=false,
    val globalExchangeActivator :Boolean=false,
    val isPrinterEnable :Boolean=false,
    val quantityTotal: Double = 0.0,
    val cartSubTotal: Double = 0.0,
    val cartTotalWithoutDiscount: Double = 0.0,
    val cartPromotionDiscount: Double = 0.0,
    var cartItemTotalDiscounts : Double = 0.0,
    var cartNetDiscounts : Double = 0.0,
    val grandTotal: Double = 0.0,
    val grandTotalWithoutDiscount :Double=0.0,
    val globalTax :Double=0.0,
    val itemTotal :Double=0.0,
    val discountIsInPercent :Boolean=false,
    val promoDiscount :Double?=0.0,

    //Discount Dialog
    var showDiscountDialog : Boolean = false,
    var isAppliedDiscountOnTotal : Boolean = false,
    var selectedDiscountType : DiscountType = DiscountType.FIXED_AMOUNT,
    var selectedDiscountApplied : DiscountApplied = DiscountApplied.GLOBAL,
    var selectedCartItem : CartItem = CartItem(),
    val globalDiscountIsInPercent :Boolean=false,
    val globalDiscount: Double = 0.0,
    var itemDiscount : String = "",
    var discounts: Double = 0.0,
    var inputDiscountError : String? =null,

    //promotion
    var showPromotionDiscountDialog : Boolean = false,
    val promotions : MutableList<Promotion> = mutableListOf(),
    val promotionDetails : MutableList<PromotionDetails> = mutableListOf(),
    val promoByPriceBreak :MutableMap<Int, CRPromotionByPriceBreak> = HashMap(emptyMap()),
    val promoByQuantity : MutableMap<Int, CRPromotionByQuantity> = HashMap(emptyMap()),
    val promotionByQuantity :Boolean=false,
    val promotionActive :Boolean=false,
    val promotionDiscountIsInPercent :Boolean=false,
    val promotionDiscount: Double = 0.0,

    //Hold Sale
    var isHoldSaleDialog : Boolean = false,
    var showHoldSalePopup : Boolean = false,
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

    //Payment
    val isFastPayment: Boolean = false,
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
    val paymentFromLib:Boolean=false,
    val paymentFromLibAmount:Double=0.0,
    val roundToDecimal: Int = 2,
    val posPayments: MutableList<PosPayment> = mutableListOf(),
    val createdPayments: MutableList<PaymentMethod> = mutableListOf(),
    val posInvoiceDetails: MutableList<PosInvoiceDetail> = mutableListOf(),
    val showEmailReceiptsDialog: Boolean = false,
    val showPhoneReceiptsDialog: Boolean = false
)

data class HeldCollection(
    var collectionId:Int,
    var items: List<ProductItem>,
    var grandTotal: Double
)




