package com.huawei.godot.iap

import com.huawei.hms.iap.entity.OrderStatusCode

sealed class IAPError {
    abstract val message: String
    data class UserCancelled(val productId: String) : IAPError() {
        override val message = "Payment cancelled by user"
    }
    data class ProductOwned(val productId: String) : IAPError() {
        override val message = "Product already owned"
    }
    data class NetworkError(override val message: String) : IAPError()
    data class HmsNotAvailable(override val message: String) : IAPError()
    data class Unknown(val code: Int, override val message: String) : IAPError()

    companion object {
        fun fromStatusCode(code: Int, message: String, productId: String = ""): IAPError {
            return when (code) {
                OrderStatusCode.ORDER_STATE_CANCEL -> UserCancelled(productId)
                OrderStatusCode.ORDER_PRODUCT_OWNED -> ProductOwned(productId)
                OrderStatusCode.ORDER_HWID_NOT_LOGIN -> HmsNotAvailable("HUAWEI ID not signed in")
                OrderStatusCode.ORDER_STATE_NET_ERROR -> NetworkError("Connection error")
                else -> Unknown(code, message)
            }
        }
    }
}
