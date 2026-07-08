# Implementation Plan — Godot Huawei IAP Plugin

- [ ] 1. Crear estructura base del proyecto Gradle
  - Crear `build.gradle.kts` raíz con config de Kotlin y Android
  - Crear `settings.gradle.kts` con nombre `godot-huawei-iap`
  - Crear `gradle.properties` con versiones de SDK
  - Crear `plugin/build.gradle.kts` con dependencias: godot 4.7, huawei hms:iap
  - Crear `plugin/src/main/AndroidManifest.xml` con meta-data v2
  - _Requirements: 7_

- [ ] 2. Implementar HuaweiIAPPlugin (Godot bridge)
  - Crear clase `HuaweiIAPPlugin` extendiendo `GodotPlugin`
  - Implementar `getPluginName()` → `"HuaweiIAP"`
  - Implementar `getPluginSignals()` → `["purchase_success", "purchase_failed", "purchase_restored"]`
  - Exponer métodos con `@UsedByGodot`: `initialize()`, `purchase(productId: String)`, `restore()`, `isOwned(productId: String): Boolean`
  - Implementar `onMainActivityResult()` para delegar a PurchaseManager
  - _Requirements: 1, 5, 6_

- [ ] 3. Implementar IAPError (sealed class)
  - Crear `IAPError` como sealed class con variantes: `UserCancelled`, `ProductOwned`, `NetworkError`, `HmsNotAvailable`, `Unknown`
  - Implementar `companion object` con método `fromException(exception: Exception): IAPError`
  - Mapear códigos `OrderStatusCode` a variantes de error
  - _Requirements: 6_

- [ ] 4. Implementar ProductCache
  - Crear `ProductCache` con `ConcurrentHashMap<String, Boolean>` interno
  - Implementar `markOwned(productId: String)`
  - Implementar `isOwned(productId: String): Boolean`
  - Implementar `setOwnedProducts(productIds: Set<String>)`
  - Implementar `clear()`
  - _Requirements: 4, 6_

- [ ] 5. Implementar IAPClient (Huawei SDK wrapper)
  - Crear `IAPClient` que envuelve `Iap.getIapClient(context)`
  - Implementar `obtainProductInfo(productIds, priceType)` → Task
  - Implementar `createPurchaseIntent(productId, priceType, developerPayload)` → Task
  - Implementar `parsePurchaseResultInfoFromIntent(data)` → PurchaseResultInfo
  - Implementar `obtainOwnedPurchases(priceType)` → Task
  - _Requirements: 6_

- [ ] 6. Implementar PurchaseManager (orquestador)
  - Crear `PurchaseManager` con referencia a `IAPClient`, `ProductCache`, y callback interface
  - Implementar `initialize(context: Context)`
  - Implementar `startPurchase(productId: String)` → crea req NON_CONSUMABLE, llama a createPurchaseIntent, maneja resolution
  - Implementar `handleActivityResult(requestCode, resultCode, data)` → parsea, verifica firma, actualiza cache, emite señal
  - Implementar `restorePurchases()` → obtiene owned purchases, actualiza cache, emite señal
  - _Requirements: 2, 3, 6_

- [ ] 7. Crear HuaweiIAPCallback (interface)
  - Crear interface con métodos: `onPurchaseSuccess(productId: String)`, `onPurchaseFailed(productId: String, error: IAPError)`, `onRestoreSuccess(productIds: List<String>)`
  - HuaweiIAPPlugin implementa esta interface
  - _Requirements: 5, 6_

- [ ] 8. Empaquetar plugin (export scripts)
  - Crear `export_scripts_template/export_plugin.gd` con `EditorExportPlugin`
  - Configurar `_plugin_name = "HuaweiIAP"`
  - Implementar `_get_android_libraries()` apuntando al AAR
  - Implementar `_get_android_dependencies()` → `["com.huawei.hms:iap:+"]`
  - Implementar `_get_android_dependencies_maven_repos()` → Huawei Maven URL
  - Crear `export_scripts_template/plugin.cfg` con nombre, descripción, autor, versión
  - _Requirements: 7_

- [ ] 9. Crear demo GDScript
  - Crear `demo/project.godot` con configuración básica
  - Crear `demo/main.gd` que usa el singleton HuaweiIAP
  - Demostrar: initialize, purchase, restore, isOwned, y conexión a señales
  - _Requirements: 1, 2, 3, 4, 5_

- [ ] 10. Verificar build y documentación inicial
  - Ejecutar `./gradlew assemble` y verificar que genera el AAR
  - Crear `README.md` con instrucciones de instalación, API reference, build, y licencia MIT
  - _Requirements: 7_
