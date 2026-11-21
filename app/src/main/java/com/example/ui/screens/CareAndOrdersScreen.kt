package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.OrderEntity
import com.example.data.local.WarrantyClaimEntity
import com.example.data.model.PolicySection
import com.example.data.model.Product
import com.example.data.model.formatZar
import com.example.ui.StoreUiState
import com.example.ui.components.PaymentMethodsStrip
import com.example.ui.theme.GridEmerald500
import com.example.ui.theme.VoltAmber500
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareAndOrdersScreen(
    uiState: StoreUiState,
    onProductClick: (Product) -> Unit,
    onOpenPolicy: (PolicySection) -> Unit,
    onSubmitWarrantyClaim: (
        orderRef: String,
        productName: String,
        serialNumber: String,
        issueDescription: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Orders & Saved", "Customer Care", "Contact & Legal")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("care_screen")
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Customer Care & Orders",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.company_legal_name) + " • " + stringResource(R.string.cipc_reg_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.testTag("care_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> OrdersAndSavedTab(
                uiState = uiState,
                onProductClick = onProductClick
            )
            1 -> CustomerCarePoliciesTab(
                warrantyClaims = uiState.warrantyClaims,
                onOpenPolicy = onOpenPolicy,
                onSubmitWarrantyClaim = onSubmitWarrantyClaim
            )
            2 -> ContactAndLegalTab(onOpenPolicy = onOpenPolicy)
        }
    }
}

@Composable
private fun OrdersAndSavedTab(
    uiState: StoreUiState,
    onProductClick: (Product) -> Unit
) {
    val savedProducts = uiState.allProducts.filter { it.id in uiState.wishlistIds }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Order History (${uiState.orders.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (uiState.orders.isEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "No orders placed yet",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Text(
                            text = "Completed checkouts will appear here with tracking and CPA warranty references.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(uiState.orders, key = { it.orderId }) { order ->
                OrderHistoryCard(order = order)
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Saved Hardware (${savedProducts.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (savedProducts.isEmpty()) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = VoltAmber500
                        )
                        Text(
                            text = "Tap the heart icon on any inverter, battery, or device to save it for later.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(savedProducts, key = { it.id }) { product ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onProductClick(product) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${formatZar(product.priceZar)} • ${product.shortTagline}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open product"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(order: OrderEntity) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(order.createdAt))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderId}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.orderId,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = formatZar(order.totalZar, includeCents = true),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = order.itemSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${order.paymentMethod} • ${order.province} • $dateStr",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                color = GridEmerald500.copy(alpha = 0.14f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = GridEmerald500,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomerCarePoliciesTab(
    warrantyClaims: List<WarrantyClaimEntity>,
    onOpenPolicy: (PolicySection) -> Unit,
    onSubmitWarrantyClaim: (String, String, String, String) -> Unit
) {
    var orderRef by rememberSaveable { mutableStateOf("") }
    var productName by rememberSaveable { mutableStateOf("") }
    var serialNumber by rememberSaveable { mutableStateOf("") }
    var issueDesc by rememberSaveable { mutableStateOf("") }
    var formError by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Customer Care Policies",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(PolicySection.entries) { policy ->
            val icon = when (policy) {
                PolicySection.SHIPPING -> Icons.Filled.LocalShipping
                PolicySection.RETURNS_CPA -> Icons.Filled.VerifiedUser
                PolicySection.POPIA -> Icons.Filled.PrivacyTip
                PolicySection.TERMS -> Icons.Filled.Gavel
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onOpenPolicy(policy) }
                    .testTag("policy_row_${policy.name}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(policy.titleRes),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = policy.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Read policy"
                    )
                }
            }
        }

        // Interactive CPA Warranty Claim Form
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "Log a Return or CPA Warranty Claim",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "Protected under the Consumer Protection Act 68 of 2008. Claims are reviewed by our Johannesburg technical desk.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = orderRef,
                        onValueChange = { orderRef = it },
                        label = { Text("Order Reference (optional, e.g. DT-2026-1042)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cpa_order_ref_input")
                    )

                    OutlinedTextField(
                        value = productName,
                        onValueChange = {
                            productName = it
                            formError = null
                        },
                        label = { Text("Product Name / Model") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cpa_product_input")
                    )

                    OutlinedTextField(
                        value = serialNumber,
                        onValueChange = {
                            serialNumber = it
                            formError = null
                        },
                        label = { Text("Hardware Serial Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cpa_serial_input")
                    )

                    OutlinedTextField(
                        value = issueDesc,
                        onValueChange = {
                            issueDesc = it
                            formError = null
                        },
                        label = { Text("Fault / Return Reason") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cpa_issue_input")
                    )

                    if (formError != null) {
                        Text(
                            text = formError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            when {
                                productName.isBlank() -> formError = "Please enter the product model."
                                serialNumber.isBlank() -> formError = "Please enter the serial number."
                                issueDesc.isBlank() -> formError = "Please describe the fault or return reason."
                                else -> {
                                    onSubmitWarrantyClaim(orderRef, productName, serialNumber, issueDesc)
                                    orderRef = ""
                                    productName = ""
                                    serialNumber = ""
                                    issueDesc = ""
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("cpa_submit_button"),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Submit CPA Claim", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        if (warrantyClaims.isNotEmpty()) {
            item {
                Text(
                    text = "Submitted CPA Claims (${warrantyClaims.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(warrantyClaims, key = { it.claimId }) { claim ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = claim.claimId,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = claim.orderRef,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${claim.productName} (SN: ${claim.serialNumber})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = claim.issueDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = claim.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = GridEmerald500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactAndLegalTab(
    onOpenPolicy: (PolicySection) -> Unit
) {
    val context = LocalContext.current
    val phone = stringResource(R.string.contact_phone)
    val email = stringResource(R.string.contact_email)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_dtcorp_logo),
                                contentDescription = stringResource(R.string.company_legal_name),
                                modifier = Modifier
                                    .size(width = 84.dp, height = 50.dp)
                                    .padding(2.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.company_legal_name),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = stringResource(R.string.cipc_reg_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Text(
                        text = "D&T Corp (Pty) Ltd is a registered South African e-commerce retailer specializing in consumer electronics and backup power solutions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    ContactInfoRow(
                        icon = Icons.Filled.LocationOn,
                        label = "Location",
                        value = stringResource(R.string.contact_address),
                        onClick = null
                    )

                    ContactInfoRow(
                        icon = Icons.Filled.Call,
                        label = "Telephone (Tap to Call)",
                        value = phone,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+27110000000"))
                            context.startActivity(intent)
                        }
                    )

                    ContactInfoRow(
                        icon = Icons.Filled.Email,
                        label = "Support Email (Tap to Email)",
                        value = email,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                            context.startActivity(intent)
                        }
                    )

                    ContactInfoRow(
                        icon = Icons.Filled.Schedule,
                        label = "Operating Hours",
                        value = stringResource(R.string.contact_hours),
                        onClick = null
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PaymentMethodsStrip()
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { onOpenPolicy(PolicySection.POPIA) }) {
                            Text("POPIA Policy")
                        }
                        TextButton(onClick = { onOpenPolicy(PolicySection.TERMS) }) {
                            Text("Terms & Conditions")
                        }
                    }
                    Text(
                        text = stringResource(R.string.copyright_notice),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)?
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PolicyReaderDialog(
    policy: PolicySection,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = policy.badge.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(policy.titleRes),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(policy.bodyRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("policy_dialog_close")
            ) {
                Text("Close")
            }
        }
    )
}
