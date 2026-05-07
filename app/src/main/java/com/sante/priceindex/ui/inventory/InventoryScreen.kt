package com.sante.priceindex.ui.inventory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.viewmodel.InventoryItem
import com.sante.priceindex.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    uiState: UiState,
    onAddInventory: (String, Double, Double, Double) -> Unit,
    onRemoveInventory: (String) -> Unit,
    onHomeClick: () -> Unit
) {
    val selected = uiState.prices.find { it.id == uiState.selectedCommodityId } ?: uiState.prices.firstOrNull()
    val selectedId = selected?.id
    var quantity by remember { mutableStateOf("50") }
    var buyPrice by remember(selected?.id) { mutableStateOf(selected?.pricePerKg?.let { String.format("%.1f", it) } ?: "") }
    var sellPrice by remember(uiState.profitResult?.rrpPerKg) {
        mutableStateOf(uiState.profitResult?.rrpPerKg?.let { String.format("%.1f", it) } ?: "")
    }

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
                        Text("Inventory", fontWeight = FontWeight.Bold)
                        Text("Stock, wastage, and profit tracking", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.82f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00695C),
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inventory2, null, tint = Color(0xFF00695C))
                            Text(" Add ${selected?.name ?: "stock"} purchase", fontWeight = FontWeight.Bold, color = Color(0xFF00695C))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            InventoryInput("Qty kg", quantity, { quantity = it }, Modifier.weight(1f))
                            InventoryInput("Buy Rs/kg", buyPrice, { buyPrice = it }, Modifier.weight(1f))
                        }
                        InventoryInput("Sell Rs/kg", sellPrice, { sellPrice = it }, Modifier.fillMaxWidth())
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedId != null,
                            onClick = {
                                val qty = quantity.toDoubleOrNull() ?: return@Button
                                val buy = buyPrice.toDoubleOrNull() ?: return@Button
                                val sell = sellPrice.toDoubleOrNull() ?: return@Button
                                selectedId?.let { onAddInventory(it, qty, buy, sell) }
                            }
                        ) {
                            Icon(Icons.Default.Add, null)
                            Text(" Add to inventory")
                        }
                    }
                }
            }

            if (uiState.inventoryItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            "No stock added yet. Add today's purchase to track stock and estimated profit.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            items(uiState.inventoryItems) { item ->
                InventoryCard(item = item, onRemove = { onRemoveInventory(item.id) })
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun InventoryInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun InventoryCard(item: InventoryItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.commodityName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${String.format("%.1f", item.stockLeftKg)} kg left from ${String.format("%.1f", item.quantityKg)} kg", color = Color.Gray, fontSize = 12.sp)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Remove")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InventoryMetric("Buy", "Rs ${String.format("%.1f", item.buyPricePerKg)}", Modifier.weight(1f))
                InventoryMetric("Sell", "Rs ${String.format("%.1f", item.sellingPricePerKg)}", Modifier.weight(1f))
                InventoryMetric("Profit", "Rs ${String.format("%.0f", item.estimatedProfit)}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InventoryMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFE0F2F1), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 11.sp)
        Text(value, color = Color(0xFF00695C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
