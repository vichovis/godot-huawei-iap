package com.huawei.godot.iap

import java.util.concurrent.ConcurrentHashMap

class ProductCache {
    private val owned = ConcurrentHashMap<String, Boolean>()

    fun markOwned(productId: String) {
        owned.put(productId, true)
    }

    fun isOwned(productId: String): Boolean {
        return owned.getOrDefault(productId, false)
    }

    fun setOwnedProducts(productIds: Set<String>) {
        owned.clear()
        productIds.forEach { owned.put(it, true) }
    }

    fun clear() {
        owned.clear()
    }
}
