package com.sante.priceindex.ui.recommendations

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.Trend
import com.sante.priceindex.viewmodel.UiState

private data class BuyAdvice(
    val label: String,
    val reason: String,
    val color: Color,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    uiState: UiState,
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
                        Text("Buy Recommendations", fontWeight = FontWeight.Bold)
                        Text("Good buy, wait, or high risk", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.82f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF57F17),
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFF57F17), modifier = Modifier.size(30.dp))
                        Column(Modifier.padding(start = 12.dp)) {
                            Text("Decision first", fontWeight = FontWeight.Bold, color = Color(0xFF6D4C00))
                            Text("Recommendations use trend, volatility, and current price position.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(uiState.prices) { commodity ->
                AdviceCard(commodity = commodity, advice = commodity.toAdvice())
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun AdviceCard(commodity: CommodityPrice, advice: BuyAdvice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(commodity.emoji, fontSize = 28.sp)
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(commodity.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Rs ${String.format("%.1f", commodity.pricePerKg)} per kg", color = Color.Gray, fontSize = 12.sp)
                }
                Icon(advice.icon, null, tint = advice.color, modifier = Modifier.size(28.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(advice.color.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(advice.label, color = advice.color, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text("  -  ${advice.reason}", color = Color(0xFF4F5F52), fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

private fun CommodityPrice.toAdvice(): BuyAdvice {
    val min = history7d.minOrNull() ?: pricePerKg
    val max = history7d.maxOrNull() ?: pricePerKg
    val range = (max - min).coerceAtLeast(1.0)
    val pricePosition = (pricePerKg - min) / range

    return when {
        getTrend() == Trend.FALLING && pricePosition <= 0.45 -> BuyAdvice(
            "Good Buy",
            "Price is near the weekly low and trend is favorable.",
            Color(0xFF1B5E20),
            Icons.Default.CheckCircle
        )
        getTrend() == Trend.RISING && pricePosition >= 0.65 -> BuyAdvice(
            "High Risk",
            "Price is already high and still rising.",
            Color(0xFFB71C1C),
            Icons.Default.Warning
        )
        getTrend() == Trend.RISING -> BuyAdvice(
            "Wait",
            "Buy smaller quantity until the rise cools down.",
            Color(0xFFF57F17),
            Icons.Default.HourglassTop
        )
        else -> BuyAdvice(
            "Normal Buy",
            "Stable enough for regular stock planning.",
            Color(0xFF1565C0),
            Icons.Default.CheckCircle
        )
    }
}
