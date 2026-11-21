package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DTCorpDatabase
import com.example.data.local.OrderEntity
import com.example.data.local.WarrantyClaimEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.model.ProductCategory
import com.example.data.model.SortOption
import com.example.data.repository.CartLineItem
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoreUiState(
    val allProducts: List<Product> = ProductCatalog.products,
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.FEATURED,
    val cartItems: List<CartLineItem> = emptyList(),
    val wishlistIds: Set<String> = emptySet(),
    val orders: List<OrderEntity> = emptyList(),
    val warrantyClaims: List<WarrantyClaimEntity> = emptyList(),
    val selectedApplianceIds: Set<String> = setOf("router", "laptop"),
    val backupHours: Int = 4,
    val lastPlacedOrder: OrderEntity? = null,
    val feedbackMessage: String? = null
) {
    val filteredProducts: List<Product>
        get() {
            val base = allProducts.filter { product ->
                val matchesCategory = selectedCategory == ProductCategory.ALL ||
                    product.category == selectedCategory
                val query = searchQuery.trim()
                val matchesQuery = query.isEmpty() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.shortTagline.contains(query, ignoreCase = true) ||
                    product.sku.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
            return when (sortOption) {
                SortOption.FEATURED -> base.sortedByDescending { it.isFeatured }
                SortOption.PRICE_LOW_HIGH -> base.sortedBy { it.priceZar }
                SortOption.PRICE_HIGH_LOW -> base.sortedByDescending { it.priceZar }
                SortOption.CAPACITY_HIGH -> base.sortedByDescending { it.capacityWh }
            }
        }

    val featuredProducts: List<Product>
        get() = allProducts.filter { it.isFeatured }

    val trendingTechProducts: List<Product>
        get() = allProducts.filter { it.isTrendingTech && !it.category.isBackupPower }

    val cartBadgeCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartSubtotalZar: Double
        get() = cartItems.sumOf { it.lineTotalZar }

    val hasHeavyFreight: Boolean
        get() = cartItems.any { it.product.isHeavyFreight }

    val cartShippingZar: Double
        get() = when {
            cartItems.isEmpty() -> 0.0
            hasHeavyFreight -> 350.0
            cartSubtotalZar >= 2500.0 -> 0.0
            else -> 149.0
        }

    val cartTotalZar: Double
        get() = cartSubtotalZar + cartShippingZar

    val freeCourierRemainingZar: Double
        get() = if (hasHeavyFreight) 0.0 else (2500.0 - cartSubtotalZar).coerceAtLeast(0.0)

    val totalLoadWatts: Int
        get() = ProductCatalog.appliances
            .filter { it.id in selectedApplianceIds }
            .sumOf { it.watts }

    val requiredCapacityWh: Int
        get() = ((totalLoadWatts * backupHours) * 1.2).toInt()

    val recommendedBackupProducts: List<Product>
        get() {
            val neededWh = requiredCapacityWh
            val neededW = totalLoadWatts
            if (neededW == 0) return emptyList()
            val backupCatalog = allProducts.filter { it.category.isBackupPower }
            return backupCatalog
                .filter { it.capacityWh >= neededWh && it.wattageRating >= neededW }
                .sortedBy { it.priceZar }
                .ifEmpty {
                    listOfNotNull(backupCatalog.maxByOrNull { it.capacityWh })
                }
        }
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StoreRepository = StoreRepository(
        DTCorpDatabase.getInstance(application).storeDao()
    )

    private val localFilterState = MutableStateFlow(LocalFilterState())

    val uiState: StateFlow<StoreUiState> = combine(
        localFilterState,
        repository.cartItems,
        repository.wishlistIds,
        repository.orders,
        repository.warrantyClaims
    ) { filter, cart, wishlist, orders, claims ->
        StoreUiState(
            selectedCategory = filter.selectedCategory,
            searchQuery = filter.searchQuery,
            sortOption = filter.sortOption,
            cartItems = cart,
            wishlistIds = wishlist,
            orders = orders,
            warrantyClaims = claims,
            selectedApplianceIds = filter.selectedApplianceIds,
            backupHours = filter.backupHours,
            lastPlacedOrder = filter.lastPlacedOrder,
            feedbackMessage = filter.feedbackMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StoreUiState()
    )

    fun selectCategory(category: ProductCategory) {
        localFilterState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        localFilterState.update { it.copy(searchQuery = query) }
    }

    fun selectSortOption(option: SortOption) {
        localFilterState.update { it.copy(sortOption = option) }
    }

    fun toggleAppliance(applianceId: String) {
        localFilterState.update { current ->
            val updated = if (applianceId in current.selectedApplianceIds) {
                current.selectedApplianceIds - applianceId
            } else {
                current.selectedApplianceIds + applianceId
            }
            current.copy(selectedApplianceIds = updated)
        }
    }

    fun setBackupHours(hours: Int) {
        localFilterState.update { it.copy(backupHours = hours.coerceIn(2, 10)) }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product.id, quantity)
            showFeedback("${product.name} added to cart")
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val wasSaved = product.id in uiState.value.wishlistIds
            repository.toggleWishlist(product.id)
            showFeedback(
                if (wasSaved) "Removed from saved items" else "Saved ${product.name}"
            )
        }
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        province: String,
        paymentMethod: PaymentMethod
    ) {
        val currentCart = uiState.value.cartItems
        if (currentCart.isEmpty()) return
        viewModelScope.launch {
            val order = repository.placeOrder(
                items = currentCart,
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                province = province,
                paymentMethod = paymentMethod
            )
            localFilterState.update {
                it.copy(
                    lastPlacedOrder = order,
                    feedbackMessage = "Order ${order.orderId} confirmed via ${paymentMethod.displayName}"
                )
            }
        }
    }

    fun dismissLastOrderReceipt() {
        localFilterState.update { it.copy(lastPlacedOrder = null) }
    }

    fun submitWarrantyClaim(
        orderRef: String,
        productName: String,
        serialNumber: String,
        issueDescription: String
    ) {
        viewModelScope.launch {
            val claim = repository.submitWarrantyClaim(
                orderRef = orderRef,
                productName = productName,
                serialNumber = serialNumber,
                issueDescription = issueDescription
            )
            showFeedback("CPA Warranty Claim ${claim.claimId} logged")
        }
    }

    fun clearFeedback() {
        localFilterState.update { it.copy(feedbackMessage = null) }
    }

    private fun showFeedback(message: String) {
        localFilterState.update { it.copy(feedbackMessage = message) }
    }

    private data class LocalFilterState(
        val selectedCategory: ProductCategory = ProductCategory.ALL,
        val searchQuery: String = "",
        val sortOption: SortOption = SortOption.FEATURED,
        val selectedApplianceIds: Set<String> = setOf("router", "laptop"),
        val backupHours: Int = 4,
        val lastPlacedOrder: OrderEntity? = null,
        val feedbackMessage: String? = null
    )
}
