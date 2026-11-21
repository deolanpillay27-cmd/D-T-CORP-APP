package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.PolicySection
import com.example.data.model.ProductCatalog
import com.example.ui.StoreViewModel
import com.example.ui.screens.CareAndOrdersScreen
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PolicyReaderDialog
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.theme.DTCorpTheme

object StoreRoutes {
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val CART = "cart"
    const val CARE = "care"
    const val PRODUCT_DETAIL = "product/{productId}"

    fun productDetail(productId: String) = "product/$productId"
}

private data class TopLevelTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topLevelTabs = listOf(
    TopLevelTab(
        route = StoreRoutes.HOME,
        labelRes = R.string.nav_shop,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_shop"
    ),
    TopLevelTab(
        route = StoreRoutes.CATALOG,
        labelRes = R.string.nav_catalog,
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView,
        testTag = "nav_tab_catalog"
    ),
    TopLevelTab(
        route = StoreRoutes.CART,
        labelRes = R.string.nav_cart,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        testTag = "nav_tab_cart"
    ),
    TopLevelTab(
        route = StoreRoutes.CARE,
        labelRes = R.string.nav_care,
        selectedIcon = Icons.Filled.SupportAgent,
        unselectedIcon = Icons.Outlined.SupportAgent,
        testTag = "nav_tab_care"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DTCorpTheme {
                DTCorpStoreApp()
            }
        }
    }
}

@Composable
fun DTCorpStoreApp(
    viewModel: StoreViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomOrRailNav = currentRoute in topLevelTabs.map { it.route }

    val snackbarHostState = remember { SnackbarHostState() }
    var activePolicy by remember { mutableStateOf<PolicySection?>(null) }

    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomOrRailNav && !useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        topLevelTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(tab.route) },
                                icon = {
                                    TabIconWithBadge(
                                        tab = tab,
                                        selected = selected,
                                        cartBadgeCount = uiState.cartBadgeCount
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(tab.labelRes),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showBottomOrRailNav && useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        topLevelTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(tab.route) },
                                icon = {
                                    TabIconWithBadge(
                                        tab = tab,
                                        selected = selected,
                                        cartBadgeCount = uiState.cartBadgeCount
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(tab.labelRes),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    NavHost(
                        navController = navController,
                        startDestination = StoreRoutes.HOME
                    ) {
                        composable(StoreRoutes.HOME) {
                            HomeScreen(
                                uiState = uiState,
                                onNavigateToCatalog = { category ->
                                    viewModel.selectCategory(category)
                                    navigateTopLevel(StoreRoutes.CATALOG)
                                },
                                onOpenSearch = {
                                    navigateTopLevel(StoreRoutes.CATALOG)
                                },
                                onProductClick = { product ->
                                    navController.navigate(StoreRoutes.productDetail(product.id))
                                },
                                onAddToCart = { product ->
                                    viewModel.addToCart(product)
                                },
                                onToggleWishlist = { product ->
                                    viewModel.toggleWishlist(product)
                                },
                                onToggleAppliance = { applianceId ->
                                    viewModel.toggleAppliance(applianceId)
                                },
                                onBackupHoursChange = { hours ->
                                    viewModel.setBackupHours(hours)
                                },
                                onOpenPolicy = { policy ->
                                    activePolicy = policy
                                }
                            )
                        }

                        composable(StoreRoutes.CATALOG) {
                            CatalogScreen(
                                uiState = uiState,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onSelectCategory = { viewModel.selectCategory(it) },
                                onSelectSortOption = { viewModel.selectSortOption(it) },
                                onProductClick = { product ->
                                    navController.navigate(StoreRoutes.productDetail(product.id))
                                },
                                onAddToCart = { product ->
                                    viewModel.addToCart(product)
                                },
                                onToggleWishlist = { product ->
                                    viewModel.toggleWishlist(product)
                                }
                            )
                        }

                        composable(StoreRoutes.CART) {
                            CartCheckoutScreen(
                                uiState = uiState,
                                onUpdateQuantity = { id, qty ->
                                    viewModel.updateCartQuantity(id, qty)
                                },
                                onRemoveItem = { id ->
                                    viewModel.removeFromCart(id)
                                },
                                onProductClick = { product ->
                                    navController.navigate(StoreRoutes.productDetail(product.id))
                                },
                                onBrowseCatalog = {
                                    navigateTopLevel(StoreRoutes.CATALOG)
                                },
                                onPlaceOrder = { name, phone, address, province, method ->
                                    viewModel.placeOrder(name, phone, address, province, method)
                                },
                                onDismissOrderReceipt = {
                                    viewModel.dismissLastOrderReceipt()
                                },
                                onOpenPolicy = { policy ->
                                    activePolicy = policy
                                }
                            )
                        }

                        composable(StoreRoutes.CARE) {
                            CareAndOrdersScreen(
                                uiState = uiState,
                                onProductClick = { product ->
                                    navController.navigate(StoreRoutes.productDetail(product.id))
                                },
                                onOpenPolicy = { policy ->
                                    activePolicy = policy
                                },
                                onSubmitWarrantyClaim = { orderRef, productName, serial, issue ->
                                    viewModel.submitWarrantyClaim(orderRef, productName, serial, issue)
                                }
                            )
                        }

                        composable(
                            route = StoreRoutes.PRODUCT_DETAIL,
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            val product = ProductCatalog.getById(productId)
                            if (product != null) {
                                ProductDetailScreen(
                                    product = product,
                                    isWishlisted = product.id in uiState.wishlistIds,
                                    onBack = { navController.popBackStack() },
                                    onToggleWishlist = { viewModel.toggleWishlist(product) },
                                    onAddToCart = { qty -> viewModel.addToCart(product, qty) }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    activePolicy?.let { policy ->
        PolicyReaderDialog(
            policy = policy,
            onDismiss = { activePolicy = null }
        )
    }
}

@Composable
private fun TabIconWithBadge(
    tab: TopLevelTab,
    selected: Boolean,
    cartBadgeCount: Int
) {
    val icon = if (selected) tab.selectedIcon else tab.unselectedIcon
    val label = stringResource(tab.labelRes)
    if (tab.route == StoreRoutes.CART && cartBadgeCount > 0) {
        BadgedBox(
            badge = {
                Badge {
                    Text(text = if (cartBadgeCount > 99) "99+" else "$cartBadgeCount")
                }
            }
        ) {
            Icon(imageVector = icon, contentDescription = label)
        }
    } else {
        Icon(imageVector = icon, contentDescription = label)
    }
}
