package com.lfssolutions.retialtouch.domain.model.productWithTax

import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.utils.DiscountApplied
import com.lfssolutions.retialtouch.utils.DiscountType


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
    var selectedDiscountApplied : DiscountApplied = DiscountApplied.TOTAL,

    var itemPriceClickItem : ProductTaxItem = ProductTaxItem(),
    var isRemoveDialog : Boolean = false,

    var selectedProduct : ProductTaxItem = ProductTaxItem(),
    val shoppingCart: List<ProductTaxItem> = listOf(),
    val dialogPosList : List<ProductTaxItem> = listOf(),
    var isCallScannedItems : Boolean = false,

    var itemsDiscount : Double = 0.0,

    val quantityTotal: Double = 0.0,
    val invoiceTax: Double = 0.0,
    var discounts: Double = 0.0,
    var qty: Double = 0.0,

    val invoiceSubTotal: Double = 0.0,
    val originalTotal: Double = 0.0,
    val invoiceNetDiscountPerc: Double = 0.0,
    val invoiceNetDiscount: Double = 0.0,
    val exchange :Boolean=false,
    val isSalesTaxInclusive :Boolean=false,
    val promotionByQuantity :Boolean=false,
    val promotionActive :Boolean=false,
    val discountIsInPercent :Boolean=false,
    val posInvoiceRounded :Double?=0.0,
    val promoDiscount :Double?=0.0,
    val amount :Double?=0.0,
    val cprice :Double?=0.0,

    val grandTotal: Double = 0.0,
    val grandTotalWithoutDiscount :Double?=0.0,

    var isHoldSaleDialog : Boolean = false,
    // HashMap to hold collections
    var holdSaleCollections : MutableMap<Int, HeldCollection> = hashMapOf(),
    var currentCollectionId: Int = 1,

    // Tracks whether the dropdown is expanded or collapsed
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
)

data class HeldCollection(
    var collectionId:Int,
    var items: List<ProductTaxItem>,
    var grandTotal: Double
)


data class Payment(
    var isLoading : Boolean = false,
)


