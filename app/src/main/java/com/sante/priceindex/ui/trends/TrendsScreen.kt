package com.sante.priceindex.ui.trends

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.Trend
import com.sante.priceindex.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendsScreen(
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
                        Text("Price Trends", fontWeight = FontWeight.Bold)
                        Text(
                            "7-day Mandi price movement",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4527A0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📈", fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Trend Intelligence",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4527A0)
                                )
                                Text(
                                    "Tap any commodity to see its price chart",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                items(uiState.prices) { commodity ->
                    TrendCard(commodity = commodity)
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TrendCard(commodity: CommodityPrice) {
    var expanded by remember { mutableStateOf(false) }
    val trend = commodity.getTrend()

    val (trendColor, trendIcon, trendBg, advisory) = when (trend) {
        Trend.RISING -> Tuple4(
            Color(0xFFB71C1C),
            Icons.Default.TrendingUp,
            Color(0xFFFFEBEE),
            "Prices rising — consider buying in smaller batches."
        )
        Trend.FALLING -> Tuple4(
            Color(0xFF1B5E20),
            Icons.Default.TrendingDown,
            Color(0xFFE8F5E9),
            "Prices falling — good time to buy in bulk."
        )
        Trend.STABLE -> Tuple4(
            Color(0xFFF57F17),
            Icons.Default.TrendingFlat,
            Color(0xFFFFFDE7),
            "Prices stable — normal buying recommended."
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(trendBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(commodity.emoji, fontSize = 24.sp)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(commodity.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(trendIcon, null, tint = trendColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            commodity.getTrendLabel(),
                            color = trendColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "₹${String.format("%.1f", commodity.pricePerKg)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("today", fontSize = 11.sp, color = Color.Gray)
                }

                Spacer(Modifier.width(8.dp))
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = Color.Gray
                )
            }

            // Expanded chart + advisory
            if (expanded) {
                Divider(color = Color(0xFFF5F5F5))
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "7-Day Price History (₹/kg)",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Mini sparkline chart
                    SparklineChart(
                        data = commodity.history7d,
                        lineColor = trendColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )

                    // Day labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Today").forEach { day ->
                            Text(day, fontSize = 9.sp, color = Color.LightGray)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Advisory
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(trendBg)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(advisory, fontSize = 13.sp, color = trendColor, fontWeight = FontWeight.Medium)
                    }

                    // Min / Max / Avg row
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip("Min", "₹${String.format("%.1f", commodity.history7d.minOrNull() ?: 0.0)}", Color(0xFF1B5E20))
                        StatChip("Avg", "₹${String.format("%.1f", commodity.history7d.average())}", Color(0xFF1565C0))
                        StatChip("Max", "₹${String.format("%.1f", commodity.history7d.maxOrNull() ?: 0.0)}", Color(0xFFB71C1C))
                    }
                }
            }
        }
    }
}

@Composable
fun SparklineChart(data: List<Double>, lineColor: Color, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return
    val minVal = data.min()
    val maxVal = data.max()
    val range = if (maxVal - minVal == 0.0) 1.0 else maxVal - minVal

    Canvas(modifier = modifier.background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))) {
        val w = size.width
        val h = size.height
        val padH = 12f
        val padV = 12f
        val stepX = (w - padH * 2) / (data.size - 1).coerceAtLeast(1)

        val points = data.mapIndexed { i, v ->
            Offset(
                x = padH + i * stepX,
                y = padV + ((maxVal - v) / range * (h - padV * 2)).toFloat()
            )
        }

        // Fill area under line
        val fillPath = Path().apply {
            moveTo(points.first().x, h)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, h)
            close()
        }
        drawPath(fillPath, lineColor.copy(alpha = 0.08f))

        // Line
        val linePath = Path().apply {
            points.forEachIndexed { i, pt ->
                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }
        drawPath(linePath, lineColor, style = Stroke(width = 3f))

        // Dots
        points.forEach { pt ->
            drawCircle(lineColor, radius = 5f, center = pt)
            drawCircle(Color.White, radius = 2.5f, center = pt)
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// Helper data class for destructuring 4 values
data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
