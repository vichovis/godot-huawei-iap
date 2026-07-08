# Changelog

## [1.0.0] - 2026-07-08
### Added
- Non-consumable purchases via `HuaweiIAP.purchase(productId)`
- Purchase restore via `HuaweiIAP.restore()`
- Ownership query via `HuaweiIAP.isOwned(productId)`
- Signals: `purchase_success`, `purchase_failed`, `purchase_restored`
- `StoreIAP.gd` unified wrapper with auto-detection (Huawei, Google, Apple)
- CI/CD with GitHub Actions (build, release, integration)
