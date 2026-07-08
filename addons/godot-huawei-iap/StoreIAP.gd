class_name StoreIAP extends RefCounted
## Unified in-app purchase wrapper supporting Huawei AppGallery, Google Play, and Apple App Store.
##
## Auto-detects the available backend at runtime. No configuration needed.
##
## Usage:
## [codeblock]
## var store = StoreIAP.new()
## store.purchase_success.connect(_on_purchased)
## store.purchase("full_game")
## [/codeblock]

signal purchase_success(product_id: String)
signal purchase_failed(product_id: String, error: String)
signal purchase_restored(product_ids: PackedStringArray)

enum Backend {
	NONE,
	HUAWEI,
	GOOGLE,
	APPLE,
}

var _backend: Backend = Backend.NONE
var _huawei = null
var _google = null
var _apple = null
var _owned_cache: Dictionary = {}


func initialize():
	if Engine.has_singleton("HuaweiIAP"):
		_huawei = Engine.get_singleton("HuaweiIAP")
		_huawei.initialize()
		_huawei.purchase_success.connect(_on_huawei_success)
		_huawei.purchase_failed.connect(_on_huawei_failed)
		_huawei.purchase_restored.connect(_on_huawei_restored)
		_backend = Backend.HUAWEI
	elif Engine.has_singleton("InAppStore"):
		_apple = Engine.get_singleton("InAppStore")
		_apple.set_auto_finish_transaction(true)
		_apple.purchase_success.connect(_on_apple_success)
		_apple.purchase_failed.connect(_on_apple_failed)
		_apple.restore_purchases_success.connect(_on_apple_restored)
		_apple.restore_purchases_failed.connect(_on_apple_restore_failed)
		_backend = Backend.APPLE
	elif ClassDB.class_exists("BillingClient"):
		_google = ClassDB.instantiate("BillingClient")
		_google.connected.connect(_on_google_connected)
		_google.on_purchase_updated.connect(_on_google_purchase_updated)
		_google.query_purchases_response.connect(_on_google_query_purchases)
		_google.start()
		_backend = Backend.GOOGLE
	else:
		push_error("StoreIAP: No supported IAP backend found")


func purchase(product_id: String):
	match _backend:
		Backend.HUAWEI:
			_huawei.purchase(product_id)
		Backend.GOOGLE:
			_google.purchase({product_id = product_id})
		Backend.APPLE:
			_apple.purchase(product_id)


func restore():
	match _backend:
		Backend.HUAWEI:
			_huawei.restore()
		Backend.GOOGLE:
			_google.query_purchases(0)
		Backend.APPLE:
			_apple.restore_purchases()


func is_owned(product_id: String) -> bool:
	return _owned_cache.get(product_id, false)


func _on_apple_success(product_id: String):
	_owned_cache[product_id] = true
	purchase_success.emit(product_id)


func _on_apple_failed(product_id: String, error_code: int, error_message: String):
	purchase_failed.emit(product_id, error_message)


func _on_apple_restored(product_ids: PackedStringArray):
	for pid in product_ids:
		_owned_cache[pid] = true
	purchase_restored.emit(product_ids)


func _on_apple_restore_failed(error_code: int, error_message: String):
	purchase_failed.emit("", error_message)


func _on_huawei_success(product_id: String):
	_owned_cache[product_id] = true
	purchase_success.emit(product_id)


func _on_huawei_failed(product_id: String, error: String):
	purchase_failed.emit(product_id, error)


func _on_huawei_restored(product_ids: Array):
	for pid in product_ids:
		_owned_cache[pid] = true
	purchase_restored.emit(PackedStringArray(product_ids))


func _on_google_connected():
	_google.query_product_details({product_ids = []})


func _on_google_purchase_updated(response: Dictionary):
	if response.get("purchase_list"):
		for purchase in response.purchase_list:
			var pid = purchase.get("product_id", "")
			_owned_cache[pid] = true
			purchase_success.emit(pid)
	elif response.get("response_code") != 0:
		purchase_failed.emit("", str(response.get("response_code")))


func _on_google_query_purchases(response: Dictionary):
	var pids := PackedStringArray()
	if response.get("data"):
		for purchase in response.data:
			var pid = purchase.get("product_id", "")
			_owned_cache[pid] = true
			pids.append(pid)
	purchase_restored.emit(pids)
