package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.PolicySection
import com.example.data.model.Product
import com.example.data.model.ProductCatalog
import com.example.data.model.ProductCategory
import com.example.data.model.formatZar
import com.example.ui.StoreUiState
import com.example.ui.components.CompanyFooterCard
import com.example.ui.components.ProductCard
import com.example.ui.components.categoryIcon
import com.example.ui.theme.GridEmerald500
import com.example.ui.theme.Obsidian950
import com.example.ui.theme.VoltAmber500
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: StoreUiState,
    onNavigateToCatalog: (ProductCategory) -> Unit,
    onOpenSearch: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onToggleAppliance: (String) -> Unit,
    onBackupHoursChange: (Int) -> Unit,
    onOpenPolicy: (PolicySection) -> Unit,
    modifier: Modifier = Modifier
) {
    var calculatorExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Branded Header featuring official D&T Corp Logo
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_dtcorp_logo),
                            contentDescription = "D&T Corp logo",
                            modifier = Modifier
                                .size(width = 78.dp, height = 46.dp)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Column {
                        Text(
                            text = "D&T Corp (Pty) Ltd",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = GridEmerald500,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Johannesburg • Mon–Fri 08:00–17:00 SAST",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    IconButton(
                        onClick = onOpenSearch,
                        modifier = Modifier.testTag("home_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search catalog",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clickable { onNavigateToCatalog(ProductCategory.LAPTOPS_COMPUTING) }
                    .testTag("home_hero_banner")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_backup_power),
                    contentDescription = "D&T Corp consumer electronics and backup power studio",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Obsidian950.copy(alpha = 0.94f),
                                    Obsidian950.copy(alpha = 0.72f),
                                    Obsidian950.copy(alpha = 0.25f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = VoltAmber500,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "SA TECH & BACKUP POWER • IN STOCK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Obsidian950,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Next-Gen Tech &\nBackup Power.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            text = "OLED laptops, 4K monitors, 5G smartphones, gaming consoles, 4K CCTV & LiFePO4 backup systems.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f),
                            maxLines = 2
                        )
                    }

                    Button(
                        onClick = { onNavigateToCatalog(ProductCategory.ALL) },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VoltAmber500,
                            contentColor = Obsidian950
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("hero_shop_cta")
                    ) {
                        Text(
                            text = "Shop All ${uiState.allProducts.size} Products",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Quick Category Shortcuts
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore Categories",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(
                        onClick = { onNavigateToCatalog(ProductCategory.ALL) },
                        modifier = Modifier.testTag("view_all_categories_btn")
                    ) {
                        Text("View All")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductCategory.entries.filter { it != ProductCategory.ALL }.forEach { category ->
                        FilterChip(
                            selected = false,
                            onClick = { onNavigateToCatalog(category) },
                            label = {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = categoryIcon(category),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("home_category_${category.name}")
                        )
                    }
                }
            }
        }

        // In-Demand Consumer Tech Showcase (non-load-shedding tech)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Top-Selling Consumer Tech",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${uiState.allProducts.count { !it.category.isBackupPower }} in-demand devices • Laptops, monitors, 5G, gaming, CCTV & NAS",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { onNavigateToCatalog(ProductCategory.LAPTOPS_COMPUTING) }) {
                        Text("More")
                    }
                }

                val trendingShowcase = uiState.trendingTechProducts.take(12)
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val columns = when {
                        maxWidth >= 840.dp -> 3
                        maxWidth >= 520.dp -> 2
                        else -> 1
                    }
                    val rows = trendingShowcase.chunked(columns)
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        rows.forEach { rowProducts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                rowProducts.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        isWishlisted = product.id in uiState.wishlistIds,
                                        onProductClick = { onProductClick(product) },
                                        onAddToCart = { onAddToCart(product) },
                                        onToggleWishlist = { onToggleWishlist(product) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(columns - rowProducts.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { onNavigateToCatalog(ProductCategory.ALL) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("browse_all_tech_button"),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Browse All ${uiState.allProducts.size} Top-Selling Products",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Featured Backup Power Solutions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Backup Power",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Dispatching daily from Sandton, Johannesburg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { onNavigateToCatalog(ProductCategory.INVERTERS_SOLAR) }) {
                        Text("Power")
                    }
                }

                val powerShowcase = uiState.allProducts.filter { it.category.isBackupPower && it.isFeatured }
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val columns = when {
                        maxWidth >= 840.dp -> 3
                        maxWidth >= 520.dp -> 2
                        else -> 1
                    }
                    val rows = powerShowcase.chunked(columns)
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        rows.forEach { rowProducts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                rowProducts.forEach { product ->
                                    ProductCard(
                                        product = product,
                                        isWishlisted = product.id in uiState.wishlistIds,
                                        onProductClick = { onProductClick(product) },
                                        onAddToCart = { onAddToCart(product) },
                                        onToggleWishlist = { onToggleWishlist(product) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(columns - rowProducts.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive Backup Power Sizing Calculator (collapsible)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("backup_calculator_card"),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { calculatorExpanded = !calculatorExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.BatteryChargingFull,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Backup Power Sizing Tool",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (calculatorExpanded) {
                                        "Calculate inverter & Wh needed for load-shedding"
                                    } else {
                                        "Tap to size an inverter or power station for your loads"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Toggle calculator",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    AnimatedVisibility(visible = calculatorExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ProductCatalog.appliances.forEach { appliance ->
                                    val selected = appliance.id in uiState.selectedApplianceIds
                                    FilterChip(
                                        selected = selected,
                                        onClick = { onToggleAppliance(appliance.id) },
                                        label = {
                                            Text(
                                                text = "${appliance.name} (${appliance.watts}W)",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        leadingIcon = if (selected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        modifier = Modifier.testTag("calc_appliance_${appliance.id}")
                                    )
                                }
                            }

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Outage Duration",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${uiState.backupHours} hours",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Slider(
                                    value = uiState.backupHours.toFloat(),
                                    onValueChange = { onBackupHoursChange(it.roundToInt()) },
                                    valueRange = 2f..10f,
                                    steps = 7,
                                    colors = SliderDefaults.colors(
                                        thumbColor = VoltAmber500,
                                        activeTrackColor = VoltAmber500
                                    ),
                                    modifier = Modifier.testTag("calc_hours_slider")
                                )
                            }

                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceContainer
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Estimated Load",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${uiState.totalLoadWatts} W",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Recommended Storage (+20% buffer)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${uiState.requiredCapacityWh} Wh",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = GridEmerald500
                                        )
                                    }
                                }
                            }

                            val match = uiState.recommendedBackupProducts.firstOrNull()
                            if (match != null) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, VoltAmber500.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { onProductClick(match) }
                                        .testTag("calc_recommended_product")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "BEST MATCH FOR YOUR LOAD",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = match.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${formatZar(match.priceZar)} • ${match.capacityWh}Wh",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { onAddToCart(match) },
                                            shape = MaterialTheme.shapes.small,
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                            modifier = Modifier.testTag("calc_add_recommended_btn")
                                        ) {
                                            Text("Add", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Official Company Footer
        item {
            CompanyFooterCard(onOpenPolicy = onOpenPolicy)
        }
    }
}
