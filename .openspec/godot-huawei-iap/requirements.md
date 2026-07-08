# Requirements Document — Godot Huawei IAP Plugin

## Introduction

Plugin para Godot 4.7+ que integra el SDK de Huawei IAP Kit como singleton `HuaweiIAP`, permitiendo compras no consumibles (desbloqueo de versión completa) desde GDScript. Desarrollado en Kotlin usando Godot Android Plugin v2. Licencia MIT.

## Requirements

### Requirement 1: Inicialización del plugin
**User Story:** As a game developer, I want to initialize the Huawei IAP plugin from GDScript, so that the SDK is ready to process purchases.

#### Acceptance Criteria
1. WHEN `Engine.get_singleton("HuaweiIAP")` is called THEN the singleton SHALL be available IF the plugin is properly installed and enabled
2. WHEN `initialize()` is called on the singleton THEN the plugin SHALL set up the Huawei IAP client context
3. IF the device does not support HMS Core THEN `initialize()` SHALL emit `purchase_failed` with an appropriate error code
4. WHEN `initialize()` completes successfully THEN the plugin SHALL be ready to process purchases and restores

### Requirement 2: Realizar una compra no consumible
**User Story:** As a player, I want to purchase a non-consumable product, so that I can unlock the full game permanently.

#### Acceptance Criteria
1. WHEN `purchase(productId)` is called THEN the plugin SHALL call `IapClient.createPurchaseIntent()` with price type NON_CONSUMABLE
2. WHEN the purchase is initiated THEN the plugin SHALL display the Huawei payment dialog via `status.startResolutionForResult()`
3. WHEN the user completes the payment successfully THEN the plugin SHALL parse the result via `parsePurchaseResultInfoFromIntent()`
4. WHEN payment is successful THEN the plugin SHALL verify the purchase data signature using the public key
5. WHEN signature verification succeeds THEN the plugin SHALL mark the product as owned in ProductCache
6. WHEN the product is marked as owned THEN the plugin SHALL emit `purchase_success(productId)` signal
7. IF the user cancels the payment THEN the plugin SHALL emit `purchase_failed(productId, "cancelled")`
8. IF the product is already owned THEN the plugin SHALL emit `purchase_success(productId)` without creating a new order
9. IF an error occurs during purchase THEN the plugin SHALL emit `purchase_failed(productId, errorMessage)` with the corresponding Huawei error code

### Requirement 3: Restaurar compras
**User Story:** As a player who reinstalled the game or changed devices, I want to restore my previous purchases, so that I can access the full game again.

#### Acceptance Criteria
1. WHEN `restore()` is called THEN the plugin SHALL call `IapClient.obtainOwnedPurchases()` with type NON_CONSUMABLE
2. WHEN owned purchases are retrieved THEN the plugin SHALL update the ProductCache with all owned product IDs
3. WHEN the cache is updated THEN the plugin SHALL emit `purchase_restored` signal
4. IF no owned purchases are found THEN the plugin SHALL emit `purchase_restored` with an empty list (not an error)
5. IF the network or HMS Core is unavailable THEN the plugin SHALL emit `purchase_failed` with the corresponding error

### Requirement 4: Consultar si un producto es poseído
**User Story:** As a game developer, I want to check if a product is already owned from GDScript, so that I can unlock features or show the correct UI.

#### Acceptance Criteria
1. WHEN `isOwned(productId)` is called THEN the plugin SHALL return `true` IF the product ID exists in the ProductCache
2. WHEN `isOwned(productId)` is called THEN the plugin SHALL return `false` IF the product ID does NOT exist in the ProductCache
3. IF the product was just purchased successfully THEN `isOwned(productId)` SHALL return `true` immediately after the `purchase_success` signal

### Requirement 5: Manejo de señales desde GDScript
**User Story:** As a game developer, I want to connect to plugin signals from GDScript, so that I can react to purchase events asynchronously.

#### Acceptance Criteria
1. WHEN the plugin initializes THEN the signals `purchase_success`, `purchase_failed`, and `purchase_restored` SHALL be registered via `getPluginSignals()`
2. GDScript SHALL be able to connect to these signals using `iap.purchase_success.connect(callable)`
3. The `purchase_success` signal SHALL carry the product ID as a String parameter
4. The `purchase_failed` signal SHALL carry the product ID (String) and error message (String)
5. The `purchase_restored` signal SHALL carry a `PackedStringArray` of owned product IDs

### Requirement 6: Separación de capas arquitectónica
**User Story:** As a maintainer, I want the codebase to have clear separation of concerns, so that I can extend it with consumables and subscriptions in the future.

#### Acceptance Criteria
1. The project SHALL have at least these classes: `HuaweiIAPPlugin`, `IAPClient`, `PurchaseManager`, `ProductCache`, `IAPError`
2. `HuaweiIAPPlugin` SHALL only handle Godot bridge logic (@UsedByGodot, signals, lifecycle) and delegate to `PurchaseManager`
3. `IAPClient` SHALL encapsulate all direct calls to `com.huawei.hms.iap.IapClient`
4. `PurchaseManager` SHALL orchestrate the purchase flow and activity result handling
5. `ProductCache` SHALL maintain an in-memory `Set<String>` of owned product IDs
6. `IAPError` SHALL be a sealed class mapping Huawei `IapApiException` status codes to user-friendly messages

### Requirement 7: Empaquetado como plugin Godot v2
**User Story:** As a game developer, I want to install the plugin via the Godot Asset Library or manual addons folder, so that I can use it in my project.

#### Acceptance Criteria
1. The plugin SHALL produce an AAR binary via Gradle `assemble` task
2. The plugin SHALL include an `export_scripts_template/export_plugin.gd` extending `EditorExportPlugin`
3. The plugin SHALL include a `plugin.cfg` with metadata (name, description, author, version)
4. The plugin SHALL declare the init class in AndroidManifest.xml as `org.godotengine.plugin.v2.HuaweiIAP`
5. The plugin SHALL list Huawei Maven repository in `_get_android_dependencies_maven_repos`
6. The plugin SHALL list `com.huawei.hms:iap` as a dependency in `_get_android_dependencies`

### Requirement 8: StoreIAP.gd wrapper unificado
**User Story:** As a game developer, I want a single GDScript API that works on Huawei, Google Play, and Apple App Store without changing my code.

#### Acceptance Criteria
1. StoreIAP.gd SHALL expose the API: `initialize()`, `purchase(productId)`, `restore()`, `isOwned(productId): bool`
2. StoreIAP.gd SHALL expose signals: `purchase_success(productId)`, `purchase_failed(productId, error)`, `purchase_restored(productIds)`
3. WHEN `initialize()` is called THEN StoreIAP SHALL auto-detect the available backend in this priority: HuaweiIAP → InAppStore → BillingClient
4. IF no backend is found THEN StoreIAP SHALL push an error and fail gracefully
5. StoreIAP.gd SHALL be located in `addons/godot-huawei-iap/StoreIAP.gd`

### Requirement 9: Backend Google Play
**User Story:** As a game developer using Google Play, I want StoreIAP to work with GodotGooglePlayBilling without additional configuration.

#### Acceptance Criteria
1. WHEN `ClassDB.class_exists("BillingClient")` is true THEN StoreIAP SHALL use Google Play billing
2. The plugin SHALL map `BillingClient` signals to StoreIAP unified signals
3. WHEN `purchase(productId)` is called on Google Play backend THEN it SHALL call `BillingClient.purchase()`
4. WHEN `restore()` is called THEN it SHALL call `BillingClient.query_purchases()`

### Requirement 10: Backend Apple App Store
**User Story:** As a game developer with an iOS game, I want StoreIAP to work with Apple StoreKit via godot_ios_plugin_iap.

#### Acceptance Criteria
1. WHEN `Engine.has_singleton("InAppStore")` is true THEN StoreIAP SHALL use Apple StoreKit
2. The plugin SHALL map `InAppStore` events to StoreIAP unified signals
3. WHEN `purchase(productId)` is called on Apple backend THEN it SHALL call `InAppStore.purchase()`
4. WHEN `restore()` is called THEN it SHALL call `InAppStore.restore_purchases()`

### Requirement 11: CI/CD automatizado
**User Story:** As a maintainer, I want automated pipelines that build, lint, and release the plugin, so that every push is validated and releases are consistent.

#### Acceptance Criteria
1. The repository SHALL have a `.github/workflows/build.yml` that compiles the AAR on every push
2. The build workflow SHALL cache Gradle dependencies
3. The repository SHALL have a `.github/workflows/release.yml` triggered by `v*` tags
4. The release workflow SHALL attach the AAR binary to the GitHub Release
5. The repository SHALL have a `.github/workflows/integration.yml` running weekly
6. All workflow files SHALL use `ubuntu-latest` and Java 17 via `temurin`
