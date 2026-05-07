package com.sante.priceindex.ui.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Home        : Screen("home")
    object PriceWatch  : Screen("price_watch")
    object Calculator  : Screen("calculator")
    object PriceBoard  : Screen("price_board")
    object Trends      : Screen("trends")
    object Alerts      : Screen("alerts")
    object Recommend   : Screen("recommend")
    object Markets     : Screen("markets")
    object Users       : Screen("users")
    object Inventory   : Screen("inventory")
    object AiAgent     : Screen("ai_agent")
    object Profile     : Screen("profile")
    object More        : Screen("more")
}
