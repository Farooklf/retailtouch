package com.hashmato.retailtouch.domain

object ApiRoutes {

    private const val BASE_API_POINT = "/api/services/app"
    const val LOGIN_API = "/api/Account/Authenticate"
    const val LOCATION_FOR_USER_API = "$BASE_API_POINT/location/ApiGetLocations"
    const val TERMINAL_API = "$BASE_API_POINT/terminal/ApiGetTerminals"
    const val EMPLOYEES_API = "$BASE_API_POINT/employee/ApiGetEmployees"
    const val EMPLOYEES_ROLE_API = "$BASE_API_POINT/employeeRole/ApiGetEmployeeRoles"
    const val EMPLOYEES_RIGHTS_API = "$BASE_API_POINT/employeeRole/ApiGetEmployeeRoleRights"
    const val NEXT_POS_SALE_INVOICE_API = "$BASE_API_POINT/PosInvoice/ApiGetNextPOSSaleInvoiceNo"
    const val POS_INVOICE_API = "$BASE_API_POINT/posinvoice/GetAll"
    const val GET_MEMBER_API = "$BASE_API_POINT/member/ApiGetMembers"
    const val GET_MEMBER_GROUP_API = "$BASE_API_POINT/membergroup/GetMemberGroups"
    const val CREATE_MEMBER_API = "$BASE_API_POINT/member/CreateOrUpdateMember"
    const val PRODUCT_WITH_TAX_API = "$BASE_API_POINT/product/ApiGetProductsWithTaxByLocation"
    const val GET_PRODUCT_LOCATION_API = "$BASE_API_POINT/productLocation/ApiGetProductLocations"
    const val GET_PRODUCT_BARCODE_API = "$BASE_API_POINT/product/ApiGetProductBarcodeByLocations"
    const val GET_MENU_CATEGORIES_API = "$BASE_API_POINT/menuCategory/ApiGetMenuCategories"
    const val GET_MENU_PRODUCTS_API = "$BASE_API_POINT/menuProduct/ApiGetMenuProducts"
    const val GET_PROMOTIONS_API = "$BASE_API_POINT/promotion/ApiGetPromotions"
    const val GET_FIXED_QTY_PROMOTIONS_API = "$BASE_API_POINT/promotion/ApiGetFixedQtyPromotions"
    const val GET_FIXED_PRICE_PROMOTIONS_API = "$BASE_API_POINT/promotion/ApiGetFixedPricePromotions"
    const val GET_PAYMENT_TYPE_API = "$BASE_API_POINT/paymentType/ApiGetPaymentTypes"
    const val GET_ALL_SYNC_API = "$BASE_API_POINT/sync/ApiGetAll"
    const val POS_INVOICE_EDIT_API = "$BASE_API_POINT/posinvoice/GetPosInvoiceForEdit"
    const val CREATE_UPDATE_POS_INVOICE = "$BASE_API_POINT/posinvoice/CreateOrUpdatePosInvoice"
    const val GET_RECEIPT_TEMPLATE_LOCATION = "$BASE_API_POINT/receipttemplate/GetReceiptTemplateByLocation"
    const val GET_POS_PAYMENT_SUMMARY = "$BASE_API_POINT/posInvoicereport/GetForApiPOSPaymentSummary" //GetPOSPaymentSummary
    const val CREATE_UPDATE_POS_SETTLEMENT = "$BASE_API_POINT/PosSettlement/CreateOrUpdatePosSettlement"
    const val CREATE_UPDATE_PAYOUTIN = "$BASE_API_POINT/PayOutIn/CreateOrUpdatePayOutIn"

}