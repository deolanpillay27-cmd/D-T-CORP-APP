package com.example.data.repository

import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.StoreDao
import com.example.data.local.WarrantyClaimEntity
import com.example.data.local.WishlistEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

data class CartLineItem(
    val product: Product,
    val quantity: Int
) {
    val lineTotalZar: Double get() = product.priceZar * quantity
}

class StoreRepository(private val dao: StoreDao) {
    val allProducts: List<Product> = ProductCatalog.products

    val cartItems: Flow<List<CartLineItem>> = dao.observeCartItems().map { entities ->
        entities.mapNotNull { entity ->
            ProductCatalog.getById(entity.productId)?.let { product ->
                CartLineItem(product = product, quantity = entity.quantity)
            }
        }
    }

    val wishlistIds: Flow<Set<String>> = dao.observeWishlist().map { list ->
        list.map { it.productId }.toSet()
    }

    val orders: Flow<List<OrderEntity>> = dao.observeOrders()

    val warrantyClaims: Flow<List<WarrantyClaimEntity>> = dao.observeWarrantyClaims()

    suspend fun addToCart(productId: String, amount: Int = 1) {
        val product = ProductCatalog.getById(productId) ?: return
        val existing = dao.getCartItem(productId)
        val nextQty = ((existing?.quantity ?: 0) + amount).coerceIn(1, product.stockCount)
        dao.upsertCartItem(CartItemEntity(productId = productId, quantity = nextQty))
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            dao.deleteCartItem(productId)
        } else {
            val maxStock = ProductCatalog.getById(productId)?.stockCount ?: 99
            dao.upsertCartItem(
                CartItemEntity(
                    productId = productId,
                    quantity = quantity.coerceAtMost(maxStock)
                )
            )
        }
    }

    suspend fun removeFromCart(productId: String) {
        dao.deleteCartItem(productId)
    }

    suspend fun toggleWishlist(productId: String) {
        if (dao.isWishlisted(productId)) {
            dao.removeWishlist(productId)
        } else {
            dao.addWishlist(WishlistEntity(productId = productId))
        }
    }

    suspend fun placeOrder(
        items: List<CartLineItem>,
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        province: String,
        paymentMethod: PaymentMethod
    ): OrderEntity {
        val subtotal = items.sumOf { it.lineTotalZar }
        val hasHeavy = items.any { it.product.isHeavyFreight }
        val shipping = when {
            items.isEmpty() -> 0.0
            hasHeavy -> 350.0
            subtotal >= 2500.0 -> 0.0
            else -> 149.0
        }
        val total = subtotal + shipping
        val summary = items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
        val count = items.sumOf { it.quantity }
        val orderId = "DT-2026-${Random.nextInt(1000, 9999)}"
        val order = OrderEntity(
            orderId = orderId,
            createdAt = System.currentTimeMillis(),
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            deliveryAddress = deliveryAddress.trim(),
            province = province,
            paymentMethod = paymentMethod.displayName,
            subtotalZar = subtotal,
            shippingZar = shipping,
            totalZar = total,
            itemSummary = summary,
            itemCount = count,
            status = if (paymentMethod == PaymentMethod.PAYFLEX) {
                "Confirmed • Payflex 1/4 Paid"
            } else {
                "Confirmed • Dispatching JHB Hub"
            }
        )
        dao.insertOrder(order)
        dao.clearCart()
        return order
    }

    suspend fun submitWarrantyClaim(
        orderRef: String,
        productName: String,
        serialNumber: String,
        issueDescription: String
    ): WarrantyClaimEntity {
        val claimId = "CPA-${Random.nextInt(10000, 99999)}"
        val claim = WarrantyClaimEntity(
            claimId = claimId,
            orderRef = orderRef.trim().ifBlank { "Direct Serial Verification" },
            productName = productName.trim(),
            serialNumber = serialNumber.trim(),
            issueDescription = issueDescription.trim(),
            submittedAt = System.currentTimeMillis(),
            status = "Logged • JHB Technical Desk Reviewing"
        )
        dao.insertWarrantyClaim(claim)
        return claim
    }
}
