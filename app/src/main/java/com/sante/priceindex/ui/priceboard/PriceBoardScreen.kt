package com.sante.priceindex.ui.priceboard

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.viewmodel.AppLanguage
import com.sante.priceindex.viewmodel.AppRole
import com.sante.priceindex.viewmodel.PriceBoardItem
import com.sante.priceindex.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceBoardScreen(
    uiState: UiState,
    onRemoveItem: (String) -> Unit,
    onNavigateToCalc: () -> Unit,
    onHomeClick: () -> Unit
) {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    var showControls by remember { mutableStateOf(false) }
    val isReadOnly = uiState.activeRole == AppRole.STAFF

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.ArrowBack, "Home")
                    }
                },
                title = {
                    Column {
                        Text(uiState.stallName, fontWeight = FontWeight.Bold)
                        Text("Live Digital Price List", style = MaterialTheme.typography.labelSmall)
                    }
                },
                actions = {
                    if (!isReadOnly) {
                        IconButton(onClick = { showControls = !showControls }) {
                            Icon(if (showControls) Icons.Default.Close else Icons.Default.Add, "Manage")
                        }
                    } else {
                        Icon(Icons.Default.Lock, "Staff Mode", modifier = Modifier.padding(16.dp), tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F8E9)) // Light mint background
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { if (!isReadOnly) showControls = !showControls }
                    )
                }
        ) {
            if (uiState.priceBoardItems.isEmpty()) {
                EmptyBoardMessage(onNavigateToCalc, isReadOnly)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.priceBoardItems) { item ->
                        PriceTile(
                            item = item, 
                            showRemove = showControls && !isReadOnly, 
                            onRemove = onRemoveItem,
                            language = uiState.activeLanguage
                        )
                    }
                }
            }

            if (!showControls && uiState.priceBoardItems.isNotEmpty() && !isReadOnly) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        "Long press items to manage",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PriceTile(
    item: PriceBoardItem, 
    showRemove: Boolean, 
    onRemove: (String) -> Unit,
    language: AppLanguage
) {
    val displayName = when(language) {
        AppLanguage.HINDI -> item.nameHi
        AppLanguage.KANNADA -> if (item.nameKn.isNotEmpty()) item.nameKn else item.name
        AppLanguage.TAMIL -> if (item.nameTa.isNotEmpty()) item.nameTa else item.name
        AppLanguage.TELUGU -> if (item.nameTe.isNotEmpty()) item.nameTe else item.name
        else -> item.name
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Area
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 38.sp)
                }
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    displayName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                if (language != AppLanguage.ENGLISH) {
                    Text(
                        item.name,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFE0E0E0))

                // Price Area
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "₹",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        String.format("%.0f", item.price),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "/kg",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
                    )
                }
                
                if (item.isFresh) {
                    Surface(
                        color = Color(0xFF1B5E20),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            "FRESH ARRIVAL",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (showRemove) {
                IconButton(
                    onClick = { onRemove(item.name) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(Color(0xFFB71C1C), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Close, "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyBoardMessage(onNavigateToCalc: () -> Unit, isReadOnly: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🥬", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Board is Empty",
            color = Color(0xFF1B5E20),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (isReadOnly) "No prices published yet." else "Update prices in the Calculator\nto show them here.",
            color = Color.Gray,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        if (!isReadOnly) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onNavigateToCalc,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Open Calculator")
            }
        }
    }
}
