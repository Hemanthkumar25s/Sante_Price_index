package com.sante.priceindex.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.ProfitResult
import com.sante.priceindex.viewmodel.AppLanguage
import com.sante.priceindex.viewmodel.UiState

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var hi by remember { mutableStateOf("") }
    var kn by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🥦") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Mandi Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name (English)") })
                OutlinedTextField(value = hi, onValueChange = { hi = it }, label = { Text("Name (Hindi)") })
                OutlinedTextField(value = kn, onValueChange = { kn = it }, label = { Text("Name (Kannada)") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Mandi Price (₹/kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val p = price.toDoubleOrNull() ?: 0.0
                onConfirm(name, hi, kn, p, emoji)
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitCalculatorScreen(
    uiState: UiState,
    onCommoditySelect: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onTransportChange: (String) -> Unit,
    onWastageChange: (String) -> Unit,
    onMarginChange: (String) -> Unit,
    onAddMandiItem: (String, String, String, Double, String) -> Unit,
    onPushToBoard: () -> Unit,
    onHomeClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val selectedCommodity = uiState.prices.find { it.id == uiState.selectedCommodityId }

    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, hi, kn, price, emoji ->
                onAddMandiItem(name, hi, kn, price, emoji)
                showAddDialog = false
            }
        )
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
                        Text("Profit Calculator", fontWeight = FontWeight.Bold)
                        Text(
                            "Find your best selling price",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Item", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Commodity Selector ──────────────────────────────────────
            SectionLabel("Select Commodity")
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCommodity?.let { commodity ->
                        val displayName = when(uiState.activeLanguage) {
                            AppLanguage.HINDI -> commodity.nameHi
                            AppLanguage.KANNADA -> if (commodity.nameKn.isNotEmpty()) commodity.nameKn else commodity.name
                            AppLanguage.TAMIL -> if (commodity.nameTa.isNotEmpty()) commodity.nameTa else commodity.name
                            AppLanguage.TELUGU -> if (commodity.nameTe.isNotEmpty()) commodity.nameTe else commodity.name
                            else -> commodity.name
                        }
                        "${commodity.emoji} $displayName — ₹${String.format("%.1f", commodity.pricePerKg)}/kg" 
                    } ?: "Select...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mandi Commodity") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.prices.forEach { commodity ->
                        val displayName = when(uiState.activeLanguage) {
                            AppLanguage.HINDI -> commodity.nameHi
                            AppLanguage.KANNADA -> if (commodity.nameKn.isNotEmpty()) commodity.nameKn else commodity.name
                            AppLanguage.TAMIL -> if (commodity.nameTa.isNotEmpty()) commodity.nameTa else commodity.name
                            AppLanguage.TELUGU -> if (commodity.nameTe.isNotEmpty()) commodity.nameTe else commodity.name
                            else -> commodity.name
                        }
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(commodity.emoji, fontSize = 20.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(displayName, fontWeight = FontWeight.Medium)
                                        Text(
                                            "₹${String.format("%.1f", commodity.pricePerKg)}/kg  ${commodity.getTrendLabel()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onCommoditySelect(commodity.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // ── Input Fields ────────────────────────────────────────────
            SectionLabel("Enter Your Costs")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InputField(
                    label = "Quantity (kg)",
                    value = uiState.quantityKg,
                    onValueChange = onQuantityChange,
                    prefix = "kg",
                    modifier = Modifier.weight(1f)
                )
                InputField(
                    label = "Transport (₹)",
                    value = uiState.transportCost,
                    onValueChange = onTransportChange,
                    prefix = "₹",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InputField(
                    label = "Wastage %",
                    value = uiState.wastagePercent,
                    onValueChange = onWastageChange,
                    prefix = "%",
                    modifier = Modifier.weight(1f)
                )
                InputField(
                    label = "Profit Margin %",
                    value = uiState.profitMargin,
                    onValueChange = onMarginChange,
                    prefix = "%",
                    modifier = Modifier.weight(1f)
                )
            }

            HintCard("Default values: 5% wastage, 20% profit margin. Adjust based on your experience.")

            // ── Results ─────────────────────────────────────────────────
            if (uiState.profitResult != null && selectedCommodity != null) {
                ResultsPanel(
                    result = uiState.profitResult,
                    commodity = selectedCommodity,
                    onPushToBoard = onPushToBoard
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        prefix = { Text(prefix, color = Color.Gray, fontSize = 13.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun HintCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFF8E1))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("💡", fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 12.sp, color = Color(0xFF5D4037))
    }
}

@Composable
fun ResultsPanel(
    result: ProfitResult,
    commodity: CommodityPrice,
    onPushToBoard: () -> Unit
) {
    // RRP highlight card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Recommended Selling Price",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "₹${String.format("%.2f", result.rrpPerKg)} / kg",
                color = Color(0xFFF9A825),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "${commodity.emoji} ${commodity.name}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }

    // Breakdown table
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Cost Breakdown",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ResultRow("Mandi Cost",   "₹${String.format("%.2f", result.mandiCostTotal)}", Color(0xFF424242))
            ResultRow("Transport",    "₹${String.format("%.2f", result.transportCost)}",  Color(0xFF424242))
            ResultRow("Wastage Buffer","₹${String.format("%.2f", result.wastageBuffer)}", Color(0xFF424242))
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ResultRow("Total Cost",   "₹${String.format("%.2f", result.totalCost)}",  Color(0xFF424242), bold = true)
            ResultRow("Cost / kg",    "₹${String.format("%.2f", result.costPerKg)}", Color(0xFF424242), bold = true)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ResultRow("Gross Sales",  "₹${String.format("%.2f", result.grossSales)}",  Color(0xFF1565C0), bold = true)
            ResultRow(
                "Net Profit",
                "₹${String.format("%.2f", result.netProfit)}",
                if (result.netProfit >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                bold = true
            )
        }
    }

    // Push to Price Board button
    Button(
        onClick = onPushToBoard,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825))
    ) {
        Icon(Icons.Default.Dashboard, null, tint = Color.Black)
        Spacer(Modifier.width(8.dp))
        Text(
            "Push to Price Board",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 15.sp
        )
    }
}

@Composable
fun ResultRow(label: String, value: String, valueColor: Color, bold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Medium else FontWeight.Normal
        )
        Text(
            value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
