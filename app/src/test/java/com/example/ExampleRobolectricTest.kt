package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ProductCatalog
import com.example.data.model.ProductCategory
import com.example.ui.StoreUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `company strings match DT Corp details`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("D&T Corp", context.getString(R.string.app_name))
        assertEquals("CIPC Reg No: 2026/000000/07", context.getString(R.string.cipc_reg_label))
        assertEquals("support@dtcorp.co.za", context.getString(R.string.contact_email))
    }

    @Test
    fun `backup power calculator recommends only backup hardware`() {
        val state = StoreUiState(
            selectedApplianceIds = setOf("router", "laptop", "tv"),
            backupHours = 4
        )
        // 18 + 95 + 130 = 243W; 243 * 4 * 1.2 = 1166 Wh
        assertEquals(243, state.totalLoadWatts)
        assertEquals(1166, state.requiredCapacityWh)
        assertTrue(state.recommendedBackupProducts.isNotEmpty())
        assertTrue(state.recommendedBackupProducts.all { it.category.isBackupPower })
        assertTrue(state.recommendedBackupProducts.first().capacityWh >= state.requiredCapacityWh)
    }

    @Test
    fun `catalog contains wide range of over 30 top-selling products`() {
        assertTrue(ProductCatalog.products.count { !it.category.isBackupPower } > 30)
        assertEquals(43, ProductCatalog.products.size)
        assertTrue(ProductCatalog.products.all { it.highlightSpec.isNotBlank() })

        val nonPowerCategories = ProductCategory.entries.filter {
            it != ProductCategory.ALL && !it.isBackupPower
        }
        assertTrue(
            nonPowerCategories.all { cat ->
                ProductCatalog.products.any { it.category == cat }
            }
        )

        val laptopSearch = StoreUiState(
            selectedCategory = ProductCategory.LAPTOPS_COMPUTING,
            searchQuery = "OLED"
        )
        assertEquals(1, laptopSearch.filteredProducts.size)
        assertEquals("dt-lap-aero14", laptopSearch.filteredProducts.first().id)
    }
}
