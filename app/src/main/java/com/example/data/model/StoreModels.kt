package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.R
import java.text.NumberFormat
import java.util.Locale

enum class ProductCategory(val label: String, val isBackupPower: Boolean = false) {
    ALL("All"),
    LAPTOPS_COMPUTING("Laptops & PCs"),
    MONITORS_DISPLAYS("Monitors & Displays"),
    MOBILE_TABLETS("Phones & Tablets"),
    GAMING_CONSOLES("Gaming & VR"),
    SMART_HOME_CCTV("Smart Home & CCTV"),
    AUDIO_WEARABLES("Audio & Wearables"),
    NETWORKING_STORAGE("Networking & SSDs"),
    INVERTERS_SOLAR("Inverters & Solar", isBackupPower = true),
    POWER_STATIONS("Power Stations", isBackupPower = true),
    LIFEPO4_BATTERIES("LiFePO4 Batteries", isBackupPower = true),
    MINI_UPS("Mini-UPS & Surge", isBackupPower = true)
}

enum class SortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    CAPACITY_HIGH("Highest Capacity")
}

enum class PaymentMethod(
    val displayName: String,
    val subtitle: String,
    val badgeCode: String
) {
    VISA("VISA", "Credit & Debit Cards (3D Secure)", "VISA"),
    MASTERCARD("Mastercard", "Credit & Cheque Cards", "MC"),
    PAYFAST("Payfast", "Instant Card, SnapScan & Zapper", "PF"),
    OZOW_EFT("Ozow EFT", "Automated Instant Bank EFT", "OZOW"),
    CAPITEC_PAY("Capitec Pay", "Approve directly in Capitec App", "CAP"),
    PAYFLEX("Payflex", "4 interest-free fortnightly payments", "PFX")
}

enum class PolicySection(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
    val badge: String
) {
    SHIPPING(R.string.policy_shipping_title, R.string.policy_shipping_body, "SA Courier"),
    RETURNS_CPA(R.string.policy_cpa_title, R.string.policy_cpa_body, "CPA Protected"),
    POPIA(R.string.policy_popia_title, R.string.policy_popia_body, "Act 4 of 2013"),
    TERMS(R.string.policy_terms_title, R.string.policy_terms_body, "Legal")
}

data class Product(
    val id: String,
    val sku: String,
    val name: String,
    val brand: String,
    val category: ProductCategory,
    val priceZar: Double,
    val originalPriceZar: Double? = null,
    val wattageRating: Int,
    val capacityWh: Int,
    val highlightSpec: String,
    val warrantyMonths: Int,
    val stockCount: Int,
    val isFeatured: Boolean = false,
    val isTrendingTech: Boolean = false,
    val isHeavyFreight: Boolean = false,
    @DrawableRes val imageRes: Int,
    val shortTagline: String,
    val description: String,
    val specs: List<Pair<String, String>>
) {
    val inStock: Boolean get() = stockCount > 0
    val payflexInstalment: Double get() = priceZar / 4.0
    val hasDiscount: Boolean get() = originalPriceZar != null && originalPriceZar > priceZar
}

data class ApplianceLoad(
    val id: String,
    val name: String,
    val watts: Int,
    val subtitle: String
)

val SouthAfricanProvinces = listOf(
    "Gauteng",
    "Western Cape",
    "KwaZulu-Natal",
    "Eastern Cape",
    "Free State",
    "Limpopo",
    "Mpumalanga",
    "North West",
    "Northern Cape"
)

fun formatZar(amount: Double, includeCents: Boolean = false): String {
    val locale = Locale("en", "ZA")
    val formatter = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = if (includeCents) 2 else 0
        maximumFractionDigits = if (includeCents) 2 else 0
    }
    return "R ${formatter.format(amount)}"
}
