package com.huawei.godot.iap

interface HuaweiIAPCallback {
    fun onPurchaseSuccess(productId: String)
    fun onPurchaseFailed(productId: String, error: IAPError)
    fun onRestoreSuccess(productIds: List<String>)
    fun onRestoreFailed(error: IAPError)
}
