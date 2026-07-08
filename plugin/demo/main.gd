extends Control

@onready var status_label = $StatusLabel
@onready var purchase_btn = $PurchaseButton
@onready var restore_btn = $RestoreButton

var store: StoreIAP


func _ready():
	store = StoreIAP.new()
	store.purchase_success.connect(_on_purchase_success)
	store.purchase_failed.connect(_on_purchase_failed)
	store.purchase_restored.connect(_on_purchase_restored)
	store.initialize()
	purchase_btn.pressed.connect(_on_purchase_pressed)
	restore_btn.pressed.connect(_on_restore_pressed)
	status_label.text = "StoreIAP initialized"


func _on_purchase_pressed():
	store.purchase("full_game")
	status_label.text = "Purchasing..."


func _on_restore_pressed():
	store.restore()
	status_label.text = "Restoring..."


func _on_purchase_success(product_id: String):
	status_label.text = "Purchased: " + product_id
	if store.is_owned("full_game"):
		status_label.text += " (full game unlocked!)"


func _on_purchase_failed(product_id: String, error: String):
	status_label.text = "Failed: " + error


func _on_purchase_restored(product_ids: PackedStringArray):
	status_label.text = "Restored: " + ", ".join(product_ids)
