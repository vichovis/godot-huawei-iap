# Godot Huawei IAP Plugin

[![Build](https://github.com/vichovis/godot-huawei-iap/actions/workflows/build.yml/badge.svg)](https://github.com/vichovis/godot-huawei-iap/actions/workflows/build.yml)
[![Release](https://github.com/vichovis/godot-huawei-iap/actions/workflows/release.yml/badge.svg)](https://github.com/vichovis/godot-huawei-iap/actions/workflows/release.yml)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Godot 4.7+ plugin for Huawei AppGallery IAP (In-App Purchases). Includes a unified `StoreIAP.gd` wrapper that also supports Google Play and Apple App Store.

## Features

- Non-consumable purchases (unlock full game)
- Purchase restore
- Ownership query
- Signals-based API

## Multi-store support

`StoreIAP.gd` auto-detects the available backend:

| Store | Platform | Backend |
|---|---|---|
| Huawei AppGallery | Android | This plugin (Kotlin) |
| Google Play | Android | [GodotGooglePlayBilling](https://github.com/godot-sdk-integrations/godot-google-play-billing) |
| Apple App Store | iOS | [godot_ios_plugin_iap](https://github.com/hrk4649/godot_ios_plugin_iap) |

## Installation

1. Copy `addons/godot-huawei-iap/` to your project
2. Enable plugin in `Project Settings > Plugins`
3. Install Android Build Template (`Project > Install Android Build Template...`)
4. Enable Gradle Build in Android export preset

## Usage

```gdscript
var store = StoreIAP.new()
store.purchase_success.connect(_on_purchased)
store.purchase("full_game")

if store.is_owned("full_game"):
    print("Full game unlocked!")
```

## API

| Method | Description |
|---|---|
| `initialize()` | Initialize the store backend |
| `purchase(product_id)` | Purchase a non-consumable product |
| `restore()` | Restore previous purchases |
| `is_owned(product_id)` → bool | Check if product is owned |

| Signal | Params |
|---|---|
| `purchase_success` | `product_id: String` |
| `purchase_failed` | `product_id: String, error: String` |
| `purchase_restored` | `product_ids: PackedStringArray` |

## Build

```bash
./gradlew assemble
```

Output: `plugin/build/outputs/aar/plugin-debug.aar`

## License

MIT
