# Godot Huawei IAP Plugin

## Propósito

Plugin open source para Godot 4.7+ que integra Huawei AppGallery IAP (In-App Purchases) mediante el SDK oficial de Huawei. Ofrece una API sencilla similar a OpenIAP pero específica para Huawei, permitiendo a desarrolladores Godot monetizar juegos en Huawei AppGallery.

## Alcance — MVP

- Solo compras **no consumibles** (desbloquear versión completa de un juego)
- No incluye: consumibles, suscripciones, promociones, cupones, sandbox helpers

## API deseada desde GDScript

```gdscript
# StoreIAP.gd unifica Huawei + Google + Apple
var store = StoreIAP.new()

store.purchase_success.connect(_on_unlocked)
store.purchase("full_game")

if store.is_owned("full_game"):
    print("Owned")
```

StoreIAP.gd auto-detecta el backend disponible:
- `HuaweiIAP` → Huawei AppGallery (Android)
- `BillingClient` → Google Play (Android)
- `InAppStore` → Apple App Store (iOS)

## Alcance extendido

- **Google Play (Android)**: Soporte via GodotGooglePlayBilling (plugin oficial Godot, MIT)
- **Apple App Store (iOS)**: Soporte via godot_ios_plugin_iap (plugin comunitario, MIT)
- **StoreIAP.gd**: Wrapper GDScript unificado que auto-detecta el backend disponible
- **CI/CD**: GitHub Actions con 3 workflows (build + lint, release, integration)

## Stack técnico

- **Lenguaje**: Kotlin moderno
- **Plataforma**: Godot 4.7+ (Android Plugin v2)
- **SDK**: Huawei IAP Kit (`com.huawei.hms:iap`)
- **Sistema**: Godot Android Plugin v2 (extiende `GodotPlugin`, anotaciones `@UsedByGodot`, señales vía `emitSignal`)
- **Licencia**: MIT
- **Repositorio**: GitHub

## Restricciones

- No usar librerías de terceros (solo Huawei SDK oficial)
- El plugin debe exponerse como singleton `HuaweiIAP`
- Compatible con Godot 4.7+
- Código Kotlin moderno, bien documentado, fácil de extender
- Preparado arquitectónicamente para agregar consumibles y suscripciones en el futuro

## Estructura del repositorio

```
godot-huawei-iap/
├── .github/workflows/
│   ├── build.yml
│   ├── release.yml
│   └── integration.yml
├── openspec/
├── .openspec/
├── plugin/
│   ├── build.gradle.kts
│   ├── export_scripts_template/
│   │   ├── export_plugin.gd
│   │   └── plugin.cfg
│   ├── demo/
│   │   ├── project.godot
│   │   └── main.gd
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/huawei/godot/iap/
│           ├── HuaweiIAPPlugin.kt
│           ├── IAPClient.kt
│           ├── PurchaseManager.kt
│           ├── ProductCache.kt
│           ├── HuaweiIAPCallback.kt
│           └── IAPError.kt
├── addons/godot-huawei-iap/
│   └── StoreIAP.gd               ← wrapper unificado multitienda
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── LICENSE
├── README.md
└── CHANGELOG.md
```
