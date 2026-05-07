package com.sante.priceindex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sante.priceindex.data.repository.AiRepository
import com.sante.priceindex.ui.SanteTheme
import com.sante.priceindex.ui.ai.AiAgentScreen
import com.sante.priceindex.ui.alerts.AlertsScreen
import com.sante.priceindex.ui.calculator.ProfitCalculatorScreen
import com.sante.priceindex.ui.dashboard.HomeScreen
import com.sante.priceindex.ui.inventory.InventoryScreen
import com.sante.priceindex.ui.login.LoginScreen
import com.sante.priceindex.ui.markets.MarketsScreen
import com.sante.priceindex.ui.navigation.Screen
import com.sante.priceindex.ui.priceboard.PriceBoardScreen
import com.sante.priceindex.ui.pricewatch.PriceWatchScreen
import com.sante.priceindex.ui.profile.ProfileScreen
import com.sante.priceindex.ui.recommendations.RecommendationsScreen
import com.sante.priceindex.ui.splash.SplashScreen
import com.sante.priceindex.ui.trends.TrendsScreen
import com.sante.priceindex.ui.users.UsersScreen
import com.sante.priceindex.viewmodel.*

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val navItems = listOf(
    NavItem(Screen.Home,       "Home",    Icons.Default.Home),
    NavItem(Screen.PriceWatch, "Prices",  Icons.Default.Store),
    NavItem(Screen.AiAgent,    "AI Agent", Icons.Default.SmartToy),
    NavItem(Screen.Calculator, "Calc",    Icons.Default.Calculate),
    NavItem(Screen.PriceBoard, "Board",   Icons.Default.Dashboard)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanteTheme {
                SanteApp()
            }
        }
    }
}

@Composable
fun SanteApp() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                if (authState.isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            })
        }

        composable("login") {
            LoginScreen(
                authState = authState,
                onLogin = { email, pass ->
                    authViewModel.login(email, pass)
                },
                onGoogleLogin = { credential ->
                    authViewModel.loginWithGoogle(credential)
                },
                onClearError = authViewModel::clearError
            )
            if (authState.isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Home.route) {
            MainScaffold()
        }
    }
}

@Composable
fun MainScaffold() {
    val mainViewModel: MainViewModel = viewModel()
    val uiState by mainViewModel.uiState.collectAsState()
    val authViewModel: AuthViewModel = viewModel()
    
    val bottomNavController = rememberNavController()

    val aiRepository = remember { AiRepository(apiKey = "AIzaSyDrB-7RK2HgW9cRLryT2_6EhiZjWKGxDcA") }
    val aiViewModel: AiViewModel = viewModel(factory = AiViewModelFactory(aiRepository))

    LaunchedEffect(Unit) {
        mainViewModel.ensurePricesLoaded()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDest = navBackStackEntry?.destination

                navItems.forEach { item ->
                    val selected = currentDest?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(item.icon, item.label) },
                        label = {
                            Text(
                                item.label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(item.screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Logout, "Logout") },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        authViewModel.logout()
                    },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFFB71C1C),
                        unselectedTextColor = Color(0xFFB71C1C)
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                if (uiState.activeRole == AppRole.STAFF) {
                    LaunchedEffect(Unit) {
                        bottomNavController.navigate(Screen.PriceBoard.route)
                    }
                }
                HomeScreen(
                    uiState = uiState,
                    onNavigate = { route -> bottomNavController.navigate(route) },
                    onRefresh = mainViewModel::loadPrices
                )
            }

            composable(Screen.PriceWatch.route) {
                if (uiState.activeRole == AppRole.STAFF) {
                    LockedFeaturePlaceholder("Staff can only view the Price Board.")
                } else {
                    PriceWatchScreen(
                        uiState = uiState,
                        onRefresh = mainViewModel::loadPrices,
                        onHomeClick = { bottomNavController.navigate(Screen.Home.route) },
                        onCommodityClick = { id ->
                            mainViewModel.selectCommodity(id)
                            bottomNavController.navigate(Screen.Calculator.route)
                        }
                    )
                }
            }

            composable(Screen.AiAgent.route) {
                if (uiState.activeRole == AppRole.STAFF) {
                    LockedFeaturePlaceholder("AI Features are restricted to Vendor/Admin.")
                } else {
                    AiAgentScreen(
                        viewModel = aiViewModel,
                        uiState = uiState,
                        onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                    )
                }
            }

            composable(Screen.Calculator.route) {
                if (uiState.activeRole == AppRole.STAFF) {
                    LockedFeaturePlaceholder("Pricing tools are restricted.")
                } else {
                    ProfitCalculatorScreen(
                        uiState = uiState,
                        onCommoditySelect = mainViewModel::selectCommodity,
                        onQuantityChange = mainViewModel::updateQuantity,
                        onTransportChange = mainViewModel::updateTransport,
                        onWastageChange = mainViewModel::updateWastage,
                        onMarginChange = mainViewModel::updateMargin,
                        onAddMandiItem = mainViewModel::addMandiItem,
                        onPushToBoard = {
                            mainViewModel.pushToPriceBoard()
                            bottomNavController.navigate(Screen.PriceBoard.route)
                        },
                        onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                    )
                }
            }

            composable(Screen.PriceBoard.route) {
                PriceBoardScreen(
                    uiState = uiState,
                    onRemoveItem = mainViewModel::removePriceBoardItem,
                    onNavigateToCalc = { bottomNavController.navigate(Screen.Calculator.route) },
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Trends.route) {
                TrendsScreen(
                    uiState = uiState,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Alerts.route) {
                AlertsScreen(
                    uiState = uiState,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Recommend.route) {
                RecommendationsScreen(
                    uiState = uiState,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Markets.route) {
                MarketsScreen(
                    uiState = uiState,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Users.route) {
                UsersScreen(
                    uiState = uiState,
                    onRoleChange = mainViewModel::updateRole,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Inventory.route) {
                InventoryScreen(
                    uiState = uiState,
                    onAddInventory = mainViewModel::addInventoryItem,
                    onRemoveInventory = mainViewModel::removeInventoryItem,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    uiState = uiState,
                    onUpdateProfile = mainViewModel::updateUserInfo,
                    onLanguageChange = mainViewModel::updateLanguage,
                    onHomeClick = { bottomNavController.navigate(Screen.Home.route) }
                )
            }
        }
    }
}

@Composable
fun LockedFeaturePlaceholder(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Lock, null, Modifier.size(64.dp), tint = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Text(message, color = Color.Gray)
        }
    }
}
