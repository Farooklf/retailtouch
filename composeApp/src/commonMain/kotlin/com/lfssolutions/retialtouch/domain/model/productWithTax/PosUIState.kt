package com.lfssolutions.retialtouch.domain.model.productWithTax

import com.lfssolutions.retialtouch.domain.model.inventory.CRShoppingCartItem
import com.lfssolutions.retialtouch.domain.model.inventory.Product
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByPriceBreak
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByQuantity
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.utils.DiscountApplied
import com.lfssolutions.retialtouch.utils.DiscountType
import kotlinx.coroutines.flow.MutableStateFlow


data class PosUIState(

    var isLoading : Boolean = false,
    var searchQuery :String = "",
    var showDialog : Boolean = false,
    var isDiscountDialog : Boolean = false,
    var isAppliedDiscountOnTotal : Boolean = false,
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
    val exchange :Boolean=false,
    val globalExchangeActivator :Boolean=false,
    val globalDiscountIsInPercent :Boolean=false,
    val globalDiscount: Double = 0.0,
    val quantityTotal: Double = 0.0,
    val cartTotal: Double = 0.0,
    val cartTotalWithoutDiscount: Double = 0.0,
    val cartPromotionDiscount: Double = 0.0,
    var cartItemsDiscount : Double = 0.0,
    val grandTotal: Double = 0.0,
    val grandTotalWithoutDiscount :Double?=0.0,
    val globalTax :Double?=0.0,
    val itemTotal :Double?=0.0,
    val itemDiscount :Double?=0.0,
    val discountIsInPercent :Boolean=false,
    val promoDiscount :Double?=0.0,

    //Dialog
    val stockList : List<Product> = listOf(),
    val dialogStockList : List<Product> = listOf(),
    var selectedProduct : CRShoppingCartItem = CRShoppingCartItem(),
    var itemPosition : Int = 0,
    var isRemoveDialog : Boolean = false,


    val shoppingCart: List<ProductTaxItem> = listOf(),
    val cartList: MutableList<CRShoppingCartItem> = mutableListOf(),
    var isCallScannedItems : Boolean = false,



    val invoiceTax: Double = 0.0,
    var discounts: Double = 0.0,


    val invoiceSubTotal: Double = 0.0,
    val originalTotal: Double = 0.0,
    val invoiceNetDiscountPerc: Double = 0.0,
    val invoiceNetDiscount: Double = 0.0,


    //Hold
    var isHoldSaleDialog : Boolean = false,
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
    val memberList : List<MemberItem> = listOf(),
    val memberGroupList : List<MemberGroupItem> = listOf(),

    //Payment
    var isPaymentScreen: Boolean = false,


    //promotion
    val promotions : MutableList<Promotion> = mutableListOf(),
    val promotionDetails : MutableList<PromotionDetails> = mutableListOf(),
    val promoByPriceBreak :MutableMap<Int, CRPromotionByPriceBreak> = HashMap(emptyMap()),
    val promoByQuantity : MutableMap<Int, CRPromotionByQuantity> = HashMap(emptyMap()),
    var promotionDiscount : Double=0.0,
    val promotionByQuantity :Boolean=false,
    val promotionActive :Boolean=false,
)

data class HeldCollection(
    var collectionId:Int,
    var items: List<ProductTaxItem>,
    var grandTotal: Double
)


data class Payment(
    var isLoading : Boolean = false,
)


