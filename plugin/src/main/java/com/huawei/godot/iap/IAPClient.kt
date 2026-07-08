package com.huawei.godot.iap

import android.content.Context
import android.content.Intent
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapClient
import com.huawei.hms.iap.entity.OwnedPurchasesReq
import com.huawei.hms.iap.entity.OwnedPurchasesResult
import com.huawei.hms.iap.entity.ProductInfoReq
import com.huawei.hms.iap.entity.ProductInfoResult
import com.huawei.hms.iap.entity.PurchaseIntentReq
import com.huawei.hms.iap.entity.PurchaseIntentResult
import com.huawei.hms.iap.entity.PurchaseResultInfo
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult
import com.huawei.hmf.tasks.Task

class IAPClient(context: Context) {
    private val client: IapClient = Iap.getIapClient(context)

    fun obtainProductInfo(productIds: List<String>, priceType: Int): Task<ProductInfoResult> {
        val req = ProductInfoReq().apply {
            this.priceType = priceType
            this.productIds = productIds
        }
        return client.obtainProductInfo(req)
    }

    fun createPurchaseIntent(productId: String, priceType: Int, developerPayload: String): Task<PurchaseIntentResult> {
        val req = PurchaseIntentReq().apply {
            this.productId = productId
            this.priceType = priceType
            this.developerPayload = developerPayload
        }
        return client.createPurchaseIntent(req)
    }

    fun parsePurchaseResultInfoFromIntent(data: Intent): PurchaseResultInfo {
        return client.parsePurchaseResultInfoFromIntent(data)
    }

    fun obtainOwnedPurchases(priceType: Int): Task<OwnedPurchasesResult> {
        val req = OwnedPurchasesReq().apply {
            this.priceType = priceType
        }
        return client.obtainOwnedPurchases(req)
    }

    fun consumeOwnedPurchase(purchaseToken: String): Task<ConsumeOwnedPurchaseResult> {
        val req = ConsumeOwnedPurchaseReq().apply {
            this.purchaseToken = purchaseToken
        }
        return client.consumeOwnedPurchase(req)
    }
}
