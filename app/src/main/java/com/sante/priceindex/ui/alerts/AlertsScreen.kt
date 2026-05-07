package com.sante.priceindex.ui.alerts

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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import kotlin.math.abs

private data class PriceAlert(
    val title: String,
    val message: String,
    val severity: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    uiState: UiState,
    onHomeClick: () -> Unit
) {
    val alerts = uiState.prices.map { it.toAlert() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.ArrowBack, "Home", tint = Color.White)
                    }
                },
                title = {
                    Column {
                        Text("Smart Alerts", fontWeight = FontWeight.Bold)
                        Text("Daily buy and margin signals", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.82f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB71C1C),
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(28.dp))
                        Column(Modifier.padding(start = 12.dp)) {
                            Text("${alerts.size} active signals", fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
                            Text("Generated from the latest loaded mandi prices.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(alerts) { alert ->
                AlertCard(alert)
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun AlertCard(alert: PriceAlert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(alert.icon, null, tint = alert.color, modifier = Modifier.size(30.dp))
            Column(Modifier.padding(start = 14.dp).weight(1f)) {
                Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(alert.message, fontSize = 12.sp, color = Color.Gray, lineHeight = 17.sp)
            }
            Text(alert.severity, color = alert.color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

private fun CommodityPrice.toAlert(): PriceAlert {
    val first = history7d.firstOrNull() ?: pricePerKg
    val change = if (first > 0) ((pricePerKg - first) / first) * 100 else 0.0
    return when (getTrend()) {
        Trend.RISING -> PriceAlert(
            title = "$name is getting costly",
            message = "Price moved ${abs(change).formatPct()} from the start of the week. Buy smaller lots or raise selling price.",
            severity = "High",
            icon = Icons.Default.TrendingUp,
            color = Color(0xFFB71C1C)
        )
        Trend.FALLING -> PriceAlert(
            title = "$name has a buying window",
            message = "Price is down ${abs(change).formatPct()}. Good time to stock if wastage risk is low.",
            severity = "Good",
            icon = Icons.Default.TrendingDown,
            color = Color(0xFF1B5E20)
        )
        Trend.STABLE -> PriceAlert(
            title = "$name is stable",
            message = "No sharp movement today. Normal quantity planning is recommended.",
            severity = "Normal",
            icon = Icons.Default.TrendingFlat,
            color = Color(0xFFF57F17)
        )
    }
}

private fun Double.formatPct(): String = "${String.format("%.1f", this)}%"
