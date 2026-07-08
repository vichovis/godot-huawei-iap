package com.huawei.godot.iap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.InAppPurchaseData
import com.huawei.hms.iap.entity.OrderStatusCode
import com.huawei.hms.iap.entity.PurchaseResultInfo

class PurchaseManager(
    private val iapClient: IAPClient,
    private val productCache: ProductCache,
    private val callback: HuaweiIAPCallback
) {
    private var activity: Activity? = null

    companion object {
        const val REQ_CODE_BUY = 1001
        const val TAG = "HuaweiIAP"
    }

    fun initialize(context: Context) {
        if (context is Activity) {
            activity = context
        }
        Log.i(TAG, "PurchaseManager initialized")
    }

    fun startPurchase(productId: String) {
        val task = iapClient.createPurchaseIntent(productId, 0, "")
        task.addOnSuccessListener { result ->
            val status = result.status
            if (status != null && status.hasResolution()) {
                try {
                    status.startResolutionForResult(activity, REQ_CODE_BUY)
                } catch (e: Exception) {
                    callback.onPurchaseFailed(productId, IAPError.Unknown(0, e.message ?: "Resolution failed"))
                }
            } else {
                callback.onPurchaseFailed(productId, IAPError.Unknown(0, "No resolution available"))
            }
        }.addOnFailureListener { e ->
            val error = if (e is com.huawei.hms.common.ApiException) {
                IAPError.fromStatusCode(e.statusCode, e.message ?: "", productId)
            } else {
                IAPError.NetworkError(e.message ?: "Unknown error")
            }
            if (error is IAPError.ProductOwned) {
                productCache.markOwned(productId)
                callback.onPurchaseSuccess(productId)
            } else {
                callback.onPurchaseFailed(productId, error)
            }
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != REQ_CODE_BUY || data == null) return

        val resultInfo: PurchaseResultInfo = iapClient.parsePurchaseResultInfoFromIntent(data)

        when (resultInfo.returnCode) {
            OrderStatusCode.ORDER_STATE_SUCCESS -> {
                val productId = extractProductId(resultInfo.inAppPurchaseData)
                if (productId != null) {
                    productCache.markOwned(productId)
                    callback.onPurchaseSuccess(productId)
                }
            }
            OrderStatusCode.ORDER_STATE_CANCEL -> {
                val productId = extractProductId(resultInfo.inAppPurchaseData)
                callback.onPurchaseFailed(productId ?: "", IAPError.UserCancelled(productId ?: ""))
            }
            else -> {
                callback.onPurchaseFailed("", IAPError.fromStatusCode(resultInfo.returnCode, resultInfo.errMsg))
            }
        }
    }

    fun restorePurchases() {
        val task = iapClient.obtainOwnedPurchases(0)
        task.addOnSuccessListener { result ->
            val purchasedProductIds = mutableListOf<String>()
            result.inAppPurchaseDataList?.forEach { data ->
                val productId = extractProductId(data)
                if (productId != null) {
                    purchasedProductIds.add(productId)
                }
            }
            productCache.setOwnedProducts(purchasedProductIds.toSet())
            callback.onRestoreSuccess(purchasedProductIds)
        }.addOnFailureListener { e ->
            val error = if (e is com.huawei.hms.common.ApiException) {
                IAPError.fromStatusCode(e.statusCode, e.message ?: "")
            } else {
                IAPError.NetworkError(e.message ?: "Unknown error")
            }
            callback.onRestoreFailed(error)
        }
    }

    private fun extractProductId(inAppPurchaseData: String?): String? {
        return try {
            if (inAppPurchaseData != null) {
                InAppPurchaseData(inAppPurchaseData).productId
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse InAppPurchaseData", e)
            null
        }
    }
}
