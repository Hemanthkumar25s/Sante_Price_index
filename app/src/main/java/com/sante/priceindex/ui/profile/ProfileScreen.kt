package com.sante.priceindex.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.viewmodel.AppLanguage
import com.sante.priceindex.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    uiState: UiState,
    onUpdateProfile: (String, String, String, String) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onHomeClick: () -> Unit
) {
    var name by remember { mutableStateOf(uiState.userName) }
    var shopName by remember { mutableStateOf(uiState.stallName) }
    var phone by remember { mutableStateOf(uiState.userPhone) }
    var location by remember { mutableStateOf(uiState.userLocation) }
    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Home", tint = Color.White)
                    }
                },
                title = { Text("Profile & Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { 
                        if (isEditing) onUpdateProfile(name, shopName, phone, location)
                        isEditing = !isEditing 
                    }) {
                        Icon(
                            if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save" else "Edit",
                            tint = Color.White
                        )
                    }
                }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // User Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Account Information", fontWeight = FontWeight.Bold, color = Color.Gray)
                    
                    ProfileTextField(
                        label = "Full Name",
                        value = name,
                        onValueChange = { name = it },
                        enabled = isEditing,
                        icon = Icons.Default.Person
                    )
                    
                    ProfileTextField(
                        label = "Shop/Stall Name",
                        value = shopName,
                        onValueChange = { shopName = it },
                        enabled = isEditing,
                        icon = Icons.Default.Store
                    )
                    
                    ProfileTextField(
                        label = "Phone Number",
                        value = phone,
                        onValueChange = { phone = it },
                        enabled = isEditing,
                        icon = Icons.Default.Phone
                    )

                    ProfileTextField(
                        label = "Location",
                        value = location,
                        onValueChange = { location = it },
                        enabled = isEditing,
                        icon = Icons.Default.LocationOn
                    )
                }
            }

            // Language Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("App Language", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = uiState.activeLanguage == lang,
                                onClick = { onLanguageChange(lang) },
                                label = { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(lang.nativeName)
                                        Text(lang.label, fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Statistics Brief
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Role", uiState.activeRole.label, Icons.Default.Badge, Modifier.weight(1f))
                StatCard("Items", "${uiState.priceBoardItems.size} on Board", Icons.Default.Dashboard, Modifier.weight(1f))
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledBorderColor = Color(0xFFE0E0E0),
            disabledLabelColor = Color.Gray,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, null, tint = Color(0xFF1B5E20), modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        maxItemsInEachRow = maxItemsInEachRow,
        content = content
    )
}
