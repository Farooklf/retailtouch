package com.lfssolutions.retialtouch.utils

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun getDeliveryType() : Flow<List<DeliveryType>> {
    return flow {
        val deliveryTypes = listOf(
            DeliveryType(id = 0, name = "All"),
            DeliveryType(id = 1, name = "Delivery"),
            DeliveryType(id = 2, name = "Self Collection"),
            DeliveryType(id = 3, name = "Rental Delivery"),
            DeliveryType(id = 4, name = "Rental Collection")
        )
        emit(deliveryTypes)  // Emit the list as a Flow
    }
}
 suspend fun getStatusType() : Flow<List<StatusType>> {
    return flow {
        val statusType = listOf(
            StatusType(id = 0, name = "All"),
            StatusType(id = 1, name = "Pending"),
            StatusType(id = 2, name = "Delivered"),
            StatusType(id = 3, name = "Self Connected"),
            StatusType(id = 4, name = "Returned")
        )
        emit(statusType)  // Emit the list as a Flow
    }
}