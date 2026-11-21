package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.OrderEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.PolicySection
import com.example.data.model.Product
import com.example.data.model.SouthAfricanProvinces
import com.example.data.model.formatZar
import com.example.data.repository.CartLineItem
import com.example.ui.StoreUiState
import com.example.ui.components.PaymentMethodsStrip
import com.example.ui.theme.GridEmerald500
import com.example.ui.theme.VoltAmber500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    uiState: StoreUiState,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onBrowseCatalog: () -> Unit,
    onPlaceOrder: (
        name: String,
        phone: String,
        address: String,
        province: String,
        paymentMethod: PaymentMethod
    ) -> Unit,
    onDismissOrderReceipt: () -> Unit,
    onOpenPolicy: (PolicySection) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCheckoutSheet by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen")
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Your Cart",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (uiState.cartItems.isEmpty()) {
                    "No items added yet"
                } else {
                    "${uiState.cartBadgeCount} item(s) • Incl. 15% SA VAT"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your D&T Corp cart is empty",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Explore inverters, LiFePO4 batteries, mini-UPS units, and consumer electronics.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onBrowseCatalog,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.testTag("empty_cart_browse_button")
                ) {
                    Text("Browse Electronics & Power")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Courier threshold / Freight notice
                item {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalShipping,
                                    contentDescription = null,
                                    tint = if (uiState.cartShippingZar == 0.0) GridEmerald500 else MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = when {
                                        uiState.hasHeavyFreight -> "Insured Palletized Heavy Freight applies (R 350)"
                                        uiState.freeCourierRemainingZar == 0.0 -> "You unlocked FREE nationwide SA courier!"
                                        else -> "Add ${formatZar(uiState.freeCourierRemainingZar)} more for FREE SA courier"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (!uiState.hasHeavyFreight) {
                                val progress = (uiState.cartSubtotalZar / 2500.0).toFloat().coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = GridEmerald500,
                                    trackColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }

                items(uiState.cartItems, key = { it.product.id }) { lineItem ->
                    CartLineItemCard(
                        lineItem = lineItem,
                        onProductClick = { onProductClick(lineItem.product) },
                        onDecrease = { onUpdateQuantity(lineItem.product.id, lineItem.quantity - 1) },
                        onIncrease = { onUpdateQuantity(lineItem.product.id, lineItem.quantity + 1) },
                        onRemove = { onRemoveItem(lineItem.product.id) }
                    )
                }

                // Totals & Checkout Card
                item {
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
                            Text(
                                text = "Order Summary",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            SummaryRow("Subtotal (incl. 15% VAT)", formatZar(uiState.cartSubtotalZar, includeCents = true))
                            SummaryRow(
                                label = if (uiState.hasHeavyFreight) "Heavy-Freight Courier" else "SA Nationwide Courier",
                                value = if (uiState.cartShippingZar == 0.0) "FREE" else formatZar(uiState.cartShippingZar, includeCents = true)
                            )
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total (ZAR)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = formatZar(uiState.cartTotalZar, includeCents = true),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "or 4x ${formatZar(uiState.cartTotalZar / 4.0, includeCents = true)} interest-free with Payflex",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = { showCheckoutSheet = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("proceed_to_checkout_button"),
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Checkout • ${formatZar(uiState.cartTotalZar, includeCents = true)}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            PaymentMethodsStrip(modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }
    }

    if (showCheckoutSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCheckoutSheet = false },
            sheetState = sheetState
        ) {
            CheckoutSheetContent(
                totalZar = uiState.cartTotalZar,
                onOpenPolicy = onOpenPolicy,
                onConfirmOrder = { name, phone, address, province, method ->
                    onPlaceOrder(name, phone, address, province, method)
                    showCheckoutSheet = false
                }
            )
        }
    }

    uiState.lastPlacedOrder?.let { order ->
        OrderConfirmationDialog(
            order = order,
            onDismiss = onDismissOrderReceipt
        )
    }
}

@Composable
private fun CartLineItemCard(
    lineItem: CartLineItem,
    onProductClick: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onProductClick)
            .testTag("cart_item_${lineItem.product.id}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = lineItem.product.imageRes),
                contentDescription = lineItem.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = lineItem.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatZar(lineItem.product.priceZar) + " each",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDecrease,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("cart_minus_${lineItem.product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Decrease quantity",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${lineItem.quantity}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = onIncrease,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("cart_plus_${lineItem.product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Increase quantity",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatZar(lineItem.lineTotalZar),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.testTag("cart_remove_${lineItem.product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Remove item",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun CheckoutSheetContent(
    totalZar: Double,
    onOpenPolicy: (PolicySection) -> Unit,
    onConfirmOrder: (String, String, String, String, PaymentMethod) -> Unit
) {
    var customerName by rememberSaveable { mutableStateOf("") }
    var customerPhone by rememberSaveable { mutableStateOf("") }
    var deliveryAddress by rememberSaveable { mutableStateOf("") }
    var selectedProvince by rememberSaveable { mutableStateOf(SouthAfricanProvinces.first()) }
    var provinceDropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedPayment by rememberSaveable { mutableStateOf(PaymentMethod.PAYFAST) }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "South Africa Checkout",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Dispatched from D&T Corp (Pty) Ltd, Johannesburg",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = customerName,
            onValueChange = {
                customerName = it
                validationError = null
            },
            label = { Text("Full Name / Company") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_name_input")
        )

        OutlinedTextField(
            value = customerPhone,
            onValueChange = {
                customerPhone = it
                validationError = null
            },
            label = { Text("SA Mobile Number (e.g. 082 000 0000)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_phone_input")
        )

        OutlinedTextField(
            value = deliveryAddress,
            onValueChange = {
                deliveryAddress = it
                validationError = null
            },
            label = { Text("Street Address & Suburb") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_address_input")
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { provinceDropdownExpanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_province_button"),
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Province")
                    Text(selectedProvince, fontWeight = FontWeight.SemiBold)
                }
            }
            DropdownMenu(
                expanded = provinceDropdownExpanded,
                onDismissRequest = { provinceDropdownExpanded = false }
            ) {
                SouthAfricanProvinces.forEach { province ->
                    DropdownMenuItem(
                        text = { Text(province) },
                        onClick = {
                            selectedProvince = province
                            provinceDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Payment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Select Accepted Payment Method",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        PaymentMethod.entries.forEach { method ->
            val isSelected = selectedPayment == method
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) VoltAmber500 else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { selectedPayment = method }
                    .testTag("payment_option_${method.name}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = method.badgeCode,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Column {
                            Text(
                                text = method.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (method == PaymentMethod.PAYFLEX) {
                                    "4x ${formatZar(totalZar / 4.0, includeCents = true)} interest-free"
                                } else {
                                    method.subtitle
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) VoltAmber500 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (validationError != null) {
            Text(
                text = validationError!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = { onOpenPolicy(PolicySection.POPIA) }) {
                Text("POPIA Privacy")
            }
            TextButton(onClick = { onOpenPolicy(PolicySection.TERMS) }) {
                Text("Terms & Conditions")
            }
        }

        Button(
            onClick = {
                when {
                    customerName.isBlank() -> validationError = "Please enter your recipient name."
                    customerPhone.isBlank() -> validationError = "Please enter a South African contact number."
                    deliveryAddress.isBlank() -> validationError = "Please enter your delivery street address."
                    else -> onConfirmOrder(
                        customerName,
                        customerPhone,
                        deliveryAddress,
                        selectedProvince,
                        selectedPayment
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("confirm_place_order_button"),
            shape = MaterialTheme.shapes.small
        ) {
            val payLabel = if (selectedPayment == PaymentMethod.PAYFLEX) {
                "Pay 1st Instalment • ${formatZar(totalZar / 4.0, includeCents = true)}"
            } else {
                "Confirm Order • ${formatZar(totalZar, includeCents = true)}"
            }
            Text(text = payLabel, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun OrderConfirmationDialog(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = GridEmerald500,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Order Confirmed!",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Reference: ${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Payment Method: ${order.paymentMethod}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total: ${formatZar(order.totalZar, includeCents = true)} (Incl. VAT)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Delivering to: ${order.deliveryAddress}, ${order.province}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Items: ${order.itemSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("order_receipt_done_button")
            ) {
                Text("Done")
            }
        }
    )
}
