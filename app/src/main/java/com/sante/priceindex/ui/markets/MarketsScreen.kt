package com.sante.priceindex.ui.markets

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.viewmodel.UiState

private data class MarketOption(
    val name: String,
    val distanceKm: Int,
    val priceDelta: Double,
    val transportCost: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketsScreen(
    uiState: UiState,
    onHomeClick: () -> Unit
) {
    val selected = uiState.prices.find { it.id == uiState.selectedCommodityId } ?: uiState.prices.firstOrNull()
    val basePrice = selected?.pricePerKg ?: 0.0
    val markets = listOf(
        MarketOption("Nearest City Mandi", 4, 0.0, 90.0),
        MarketOption("Wholesale Yard", 12, -1.8, 220.0),
        MarketOption("Morning Auction Market", 19, -3.2, 360.0),
        MarketOption("Premium Fresh Market", 8, 2.4, 140.0)
    ).sortedBy { option -> ((basePrice + option.priceDelta) * 50.0) + option.transportCost }

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
                        Text("Best Market", fontWeight = FontWeight.Bold)
                        Text("Compare by landed cost", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.82f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Map, null, tint = Color(0xFF1565C0), modifier = Modifier.size(30.dp))
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(selected?.name ?: "Select a commodity", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            Text("Assuming 50 kg purchase quantity. Lowest landed cost is ranked first.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(markets) { market ->
                MarketCard(
                    market = market,
                    basePrice = basePrice,
                    isBest = market == markets.first()
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun MarketCard(market: MarketOption, basePrice: Double, isBest: Boolean) {
    val mandiPrice = (basePrice + market.priceDelta).coerceAtLeast(1.0)
    val landedTotal = (mandiPrice * 50.0) + market.transportCost
    val landedPerKg = landedTotal / 50.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isBest) Color(0xFFE8F5E9) else Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = if (isBest) Color(0xFF1B5E20) else Color(0xFF1565C0))
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(market.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${market.distanceKm} km away", color = Color.Gray, fontSize = 12.sp)
                }
                if (isBest) {
                    Text("Best", color = Color(0xFF1B5E20), fontWeight = FontWeight.ExtraBold)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniMetric("Mandi", "Rs ${String.format("%.1f", mandiPrice)}/kg", Modifier.weight(1f))
                MiniMetric("Transport", "Rs ${String.format("%.0f", market.transportCost)}", Modifier.weight(1f))
                MiniMetric("Landed", "Rs ${String.format("%.1f", landedPerKg)}/kg", Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Route, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(" Real market API can replace these sample routes later.", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MiniMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 11.sp)
        Text(value, color = Color(0xFF1A1A1A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
