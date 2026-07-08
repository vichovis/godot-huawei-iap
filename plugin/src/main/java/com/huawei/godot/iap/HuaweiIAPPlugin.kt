package com.huawei.godot.iap

import android.app.Activity
import android.content.Intent
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class HuaweiIAPPlugin(godot: Godot) : GodotPlugin(godot), HuaweiIAPCallback {

    private val productCache = ProductCache()
    private val iapClient: IAPClient by lazy { IAPClient(getActivity()!!) }
    private val purchaseManager: PurchaseManager by lazy { PurchaseManager(iapClient, productCache, this) }

    override fun getPluginName(): String = "HuaweiIAP"

    override fun getPluginSignals(): Set<SignalInfo> {
        return setOf(
            SignalInfo("purchase_success", String::class.java),
            SignalInfo("purchase_failed", String::class.java, String::class.java),
            SignalInfo("purchase_restored", Array<String>::class.java)
        )
    }

    override fun onMainActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        purchaseManager.handleActivityResult(requestCode, resultCode, data)
        super.onMainActivityResult(requestCode, resultCode, data)
    }

    @UsedByGodot
    fun initialize() {
        purchaseManager.initialize(getActivity()!!)
    }

    @UsedByGodot
    fun purchase(productId: String) {
        purchaseManager.startPurchase(productId)
    }

    @UsedByGodot
    fun restore() {
        purchaseManager.restorePurchases()
    }

    @UsedByGodot
    fun isOwned(productId: String): Boolean {
        return productCache.isOwned(productId)
    }

    override fun onPurchaseSuccess(productId: String) {
        emitSignal("purchase_success", productId)
    }

    override fun onPurchaseFailed(productId: String, error: IAPError) {
        emitSignal("purchase_failed", productId, error.message)
    }

    override fun onRestoreSuccess(productIds: List<String>) {
        emitSignal("purchase_restored", productIds.toTypedArray())
    }

    override fun onRestoreFailed(error: IAPError) {
        emitSignal("purchase_failed", "", error.message)
    }
}
