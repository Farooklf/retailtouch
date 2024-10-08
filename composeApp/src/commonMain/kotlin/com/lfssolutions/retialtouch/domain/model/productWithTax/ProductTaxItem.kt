package com.lfssolutions.retialtouch.domain.model.productWithTax


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductTaxItem(
    @SerialName("activeLastModifiedTime")
    val activeLastModifiedTime: String? = "",
    @SerialName("actualQty")
    val actualQty: Double? = 0.0,
    @SerialName("aliasName")
    val aliasName: String? = "",
    @SerialName("attributeList")
    val attributeList: String? = null,
    @SerialName("attributeName1")
    val attributeName1: String? = null,
    @SerialName("attributeName2")
    val attributeName2: String? = null,
    @SerialName("attributeName3")
    val attributeName3: String? = null,
    @SerialName("attributeValue1")
    val attributeValue1: String? = null,
    @SerialName("attributeValue2")
    val attributeValue2: String? = null,
    @SerialName("attributeValue3")
    val attributeValue3: String? = null,
    @SerialName("attributes")
    val attributes: String? = null,
    @SerialName("author")
    val author: String? = null,
    @SerialName("authorId")
    val authorId: String? = null,
    @SerialName("averageCost")
    val averageCost: Double? = 0.0,
    @SerialName("barCode")
    val barCode: String? = "",
    @SerialName("binLocation")
    val binLocation: String? = null,
    @SerialName("binLocationId")
    val binLocationId: String? = null,
    @SerialName("brand")
    val brand: String? = null,
    @SerialName("brandId")
    val brandId: Int? = 0,
    @SerialName("brandName")
    val brandName: String? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("categoryCode")
    val categoryCode: String? = null,
    @SerialName("categoryId")
    val categoryId: Int? = 0,
    @SerialName("categoryName")
    val categoryName: String? = null,
    @SerialName("color")
    val color: String? = null,
    @SerialName("consignmentMarginPerc")
    val consignmentMarginPerc: Double? = 0.0,
    @SerialName("costWithTax")
    val costWithTax: Double? = 0.0,
    @SerialName("creationTime")
    val creationTime: String? = "",
    @SerialName("creatorUserId")
    val creatorUserId: Int? = 0,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("department")
    val department: String? = null,
    @SerialName("departmentCode")
    val departmentCode: String? = null,
    @SerialName("departmentId")
    val departmentId: Int? = 0,
    @SerialName("departmentName")
    val departmentName: String? = null,
    @SerialName("erpInternalId")
    val erpInternalId: String? = null,
    @SerialName("expiryDate")
    val expiryDate: String? = null,
    @SerialName("exportToWeighScale")
    val exportToWeighScale: Int? = 0,
    @SerialName("grandQTotalValue")
    val grandQTotalValue: Double? = 0.0,
    @SerialName("grandQtyOnHand")
    val grandQtyOnHand: Double? = 0.0,
    @SerialName("grandSellingValue")
    val grandSellingValue: Double? = 0.0,
    @SerialName("height")
    val height: Double? = 0.0,
    @SerialName("hsnCode")
    val hsnCode: String? = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("imagePath")
    val imagePath: String? = null,
    @SerialName("inActiveLastModifiedTime")
    val inActiveLastModifiedTime: String? = null,
    @SerialName("inTransit")
    val inTransit: Double? = 0.0,
    @SerialName("inventoryCode")
    val inventoryCode: String? = "",
    @SerialName("invoiceNo")
    val invoiceNo: String? = null,
    @SerialName("isActive")
    val isActive: Boolean? = false,
    @SerialName("isAllowNegativeStock")
    val isAllowNegativeStock: Boolean? = false,
    @SerialName("isBatch")
    val isBatch: Boolean? = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerialName("isExportToWeighScale")
    val isExportToWeighScale: Boolean? = false,
    @SerialName("isMRPBatch")
    val isMRPBatch: Boolean? = false,
    @SerialName("isNEAEligible")
    val isNEAEligible: Boolean? = false,
    @SerialName("isPosInvoicesmanRequired")
    val isPosInvoicesmanRequired: Boolean? = false,
    @SerialName("isPosLoyalty")
    val isPosLoyalty: Boolean? = false,
    @SerialName("isPosMixSameItem")
    val isPosMixSameItem: Boolean? = false,
    @SerialName("isPosOpenItem")
    val isPosOpenItem: Boolean? = false,
    @SerialName("isPosPriceEdit")
    val isPosPriceEdit: Boolean? = false,
    @SerialName("isPosQtyEdit")
    val isPosQtyEdit: Boolean? = false,
    @SerialName("isPosTagBundle")
    val isPosTagBundle: Boolean? = false,
    @SerialName("isTradeIn")
    val isTradeIn: Boolean? = false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = "",
    @SerialName("lastModifierUserId")
    val lastModifierUserId: Int? = 0,
    @SerialName("lastPurchaseCost")
    val lastPurchaseCost: Double? = 0.0,
    @SerialName("length")
    val length: Double? = 0.0,
    @SerialName("locationCode")
    val locationCode: String? = null,
    @SerialName("locationGroupId")
    val locationGroupId: String? = null,
    @SerialName("locationId")
    val locationId: Int? = 0,
    @SerialName("minimumQty")
    val minimumQty: Double? = 0.0,
    @SerialName("minimumSellingPrice")
    val minimumSellingPrice: Double? = 0.0,
    @SerialName("mrp")
    val mrp: Double? = 0.0,
    @SerialName("name")
    val name: String? = "",
    @SerialName("noManualDiscount")
    val noManualDiscount: Boolean? = false,
    @SerialName("onlineSales")
    val onlineSales: String? = null,
    @SerialName("orderedQuantity")
    val orderedQuantity: Double? = 0.0,
    @SerialName("origin")
    val origin: String? = null,
    @SerialName("originId")
    val originId: String? = null,
    @SerialName("originPrice")
    val originPrice: Double? = 0.0,
    @SerialName("parentId")
    val parentId: Int? = 0,
    @SerialName("parentInventoryCode")
    val parentInventoryCode: String? = null,
    @SerialName("posWholesalePrice")
    val posWholesalePrice: Double? = 0.0,
    @SerialName("price")
    val price: Double? = 0.0,
    @SerialName("priceGroupMapping")
    val priceGroupMapping: String? = null,
    @SerialName("productExpiryDate")
    val productExpiryDate: String? = null,
    @SerialName("productGroup")
    val productGroup: String? = "",
    @SerialName("productGroupAttributes")
    val productGroupAttributes: String? = null,
    @SerialName("productLocations")
    val productLocations: String? = null,
    @SerialName("productName")
    val productName: String? = null,
    @SerialName("productType")
    val productType: String? = null,
    @SerialName("productTypeId")
    val productTypeId: String? = null,
    @SerialName("productTypeName")
    val productTypeName: String? = null,
    @SerialName("profitMargin")
    val profitMargin: Double? = 0.0,
    @SerialName("publisher")
    val publisher: String? = null,
    @SerialName("publisherId")
    val publisherId: String? = null,
    @SerialName("qty")
    val qty: String? = null,
    @SerialName("qtyOnHand")
    var qtyOnHand: Double = 0.0,
    @SerialName("qtyOnHandForLocation")
    val qtyOnHandForLocation: Double? = 0.0,
    @SerialName("reOrderLevel")
    val reOrderLevel: Double? = 0.0,
    @SerialName("remarks")
    val remarks: String? = "",
    @SerialName("reservedQty")
    val reservedQty: Double? = 0.0,
    @SerialName("retailTabSortOrder")
    val retailTabSortOrder: Int? = 0,
    @SerialName("salesCommisionPerc")
    val salesCommisionPerc: Double? = 0.0,
    @SerialName("salesOrderId")
    val salesOrderId: Int? = 0,
    @SerialName("sellingPriceTotalValue")
    val sellingPriceTotalValue: Double? = 0.0,
    @SerialName("serialNumber")
    val serialNumber: String? = null,
    @SerialName("size")
    val size: String? = null,
    @SerialName("soldQty")
    val soldQty: String? = null,
    @SerialName("specialPrice")
    val specialPrice: Double? = 0.0,
    @SerialName("subCategoryCode")
    val subCategoryCode: String? = null,
    @SerialName("subCategoryId")
    val subCategoryId: Int? = 0,
    @SerialName("subCategoryName")
    val subCategoryName: String? = null,
    @SerialName("tax")
    val tax: String? = "",
    @SerialName("taxId")
    val taxId: Int? = 0,
    @SerialName("taxName")
    val taxName: String? = null,
    @SerialName("taxPercentage")
    val taxPercentage: Double? = 0.0,
    @SerialName("taxValue")
    val taxValue: Double? = 0.0,
    @SerialName("totalValue")
    val totalValue: Double? = 0.0,
    @SerialName("totalValueWithTax")
    val totalValueWithTax: Double? = 0.0,
    @SerialName("tradeIn")
    val tradeIn: Int? = 0,
    @SerialName("transactionId")
    val transactionId: Int? = 0,
    @SerialName("transactionRemarks")
    val transactionRemarks: String? = null,
    @SerialName("type")
    val type: String? = "",
    @SerialName("unitCost")
    val unitCost: Double? = 0.0,
    @SerialName("unitId")
    val unitId: Int? = 0,
    @SerialName("unitName")
    val unitName: String? = null,
    @SerialName("vendorDiscountAmount")
    val vendorDiscountAmount: Double? = 0.0,
    @SerialName("vendorDiscountPerc")
    val vendorDiscountPerc: Double? = 0.0,
    @SerialName("vendorId")
    val vendorId: String? = null,
    @SerialName("vendorName")
    val vendorName: String? = null,
    @SerialName("weight")
    val weight: Double? = 0.0,
    @SerialName("weightUOM")
    val weightUOM: String? = null,
    @SerialName("weightUOMId")
    val weightUOMId: String? = null,
    @SerialName("width")
    val width: Double? = 0.0,

    val originalPrice: Double = price?:0.0,
    val originalSubTotal: Double = 0.0,
    val subtotal: Double? = 0.0,
    val discount: Double = 0.0,
)