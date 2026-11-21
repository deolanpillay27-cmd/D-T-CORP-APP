package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val createdAt: Long,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val province: String,
    val paymentMethod: String,
    val subtotalZar: Double,
    val shippingZar: Double,
    val totalZar: Double,
    val itemSummary: String,
    val itemCount: Int,
    val status: String
)

@Entity(tableName = "warranty_claims")
data class WarrantyClaimEntity(
    @PrimaryKey val claimId: String,
    val orderRef: String,
    val productName: String,
    val serialNumber: String,
    val issueDescription: String,
    val submittedAt: Long,
    val status: String
)
