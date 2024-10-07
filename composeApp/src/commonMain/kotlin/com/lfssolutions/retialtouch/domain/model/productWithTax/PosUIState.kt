package com.lfssolutions.retialtouch.domain.model.productWithTax

import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.utils.DiscountType


data class PosUIState(


    var isLoading : Boolean = false,
    var searchQuery :String = "",
    var showDialog : Boolean = false,
    var isDiscountDialog : Boolean = false,
    var isPriceDialog : Boolean = false,
    var currencySymbol : String = "$",
    var inputDiscount : String = "",
    var inputDiscountError : String? =null,
    var selectedDiscountType : DiscountType = DiscountType.FIXED_AMOUNT,

    var isRemoveDialog : Boolean = false,

    var selectedProduct : ProductTaxItem = ProductTaxItem(),
    val uiPosList: List<ProductTaxItem> = listOf(),
    val dialogPosList : List<ProductTaxItem> = listOf(),

    var itemsDiscount : Double = 0.0,
    var promoDiscount : Double = 0.0,
    val totalQty: Double = 0.0,
    val totalTax: Double = 0.0,
    var discounts: Double = 0.0,
    val subTotal: Double = 0.0,
    val grandTotal: Double = 0.0,
    val originalTotal: Double = 0.0,


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


