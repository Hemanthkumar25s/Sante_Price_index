package com.sante.priceindex.ui.pricewatch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.Trend
import com.sante.priceindex.viewmodel.AppLanguage
import com.sante.priceindex.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceWatchScreen(
    uiState: UiState,
    onRefresh: () -> Unit,
    onCommodityClick: (String) -> Unit,
    onHomeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.ArrowBack, "Home", tint = Color.White)
                    }
                },
                title = {
                    Column {
                        Text("Price Watch", fontWeight = FontWeight.Bold)
                        Text(
                            "Today's Mandi Prices",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, "Refresh", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading && uiState.prices.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Text("Fetching Mandi prices...", color = Color.Gray)
                    }
                }
                uiState.error != null && uiState.prices.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(uiState.error, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRefresh) { Text("Retry") }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.error != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFF8E1)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        uiState.error,
                                        modifier = Modifier.padding(14.dp),
                                        color = Color(0xFF6D4C00),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        item {
                            // Header info card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🏪", fontSize = 28.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "City Mandi Prices",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "Tap any item to view 7-day trend",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        items(uiState.prices) { commodity ->
                            CommodityCard(
                                commodity = commodity,
                                language = uiState.activeLanguage,
                                onClick = { onCommodityClick(commodity.id) }
                            )
                        }

                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun CommodityCard(
    commodity: CommodityPrice, 
    language: AppLanguage,
    onClick: () -> Unit
) {
    val trend = commodity.getTrend()
    val (trendColor, trendIcon, trendBg) = when (trend) {
        Trend.RISING  -> Triple(Color(0xFFB71C1C), Icons.Default.TrendingUp, Color(0xFFFFEBEE))
        Trend.FALLING -> Triple(Color(0xFF1B5E20), Icons.Default.TrendingDown, Color(0xFFE8F5E9))
        Trend.STABLE  -> Triple(Color(0xFFF57F17), Icons.Default.TrendingFlat, Color(0xFFFFFDE7))
    }

    val displayName = when(language) {
        AppLanguage.HINDI -> commodity.nameHi
        AppLanguage.KANNADA -> if (commodity.nameKn.isNotEmpty()) commodity.nameKn else commodity.name
        AppLanguage.TAMIL -> if (commodity.nameTa.isNotEmpty()) commodity.nameTa else commodity.name
        AppLanguage.TELUGU -> if (commodity.nameTe.isNotEmpty()) commodity.nameTe else commodity.name
        else -> commodity.name
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(commodity.emoji, fontSize = 28.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (language != AppLanguage.ENGLISH) {
                    Text(
                        commodity.name,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
                Text(
                    "Updated: ${commodity.updatedAt}",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%.1f", commodity.pricePerKg)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "per kg",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(trendBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(trendIcon, null, tint = trendColor, modifier = Modifier.size(14.dp))
                    Text(
                        commodity.getTrendLabel(),
                        color = trendColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
