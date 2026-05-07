package com.sante.priceindex.ui.users

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.sante.priceindex.viewmodel.AppRole
import com.sante.priceindex.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    uiState: UiState,
    onRoleChange: (AppRole) -> Unit,
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
                        Text("User Roles", fontWeight = FontWeight.Bold)
                        Text("Admin, vendor, and staff access", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.82f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5D4037),
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEBE9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupervisorAccount, null, tint = Color(0xFF5D4037))
                            Text(" Current role: ${uiState.activeRole.label}", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppRole.entries.forEach { role ->
                                FilterChip(
                                    selected = role == uiState.activeRole,
                                    onClick = { onRoleChange(role) },
                                    label = { Text(role.label) }
                                )
                            }
                        }
                    }
                }
            }

            item { RoleCard(AppRole.ADMIN, "Can manage prices, users, inventory, and Firebase data sync.") }
            item { RoleCard(AppRole.VENDOR, "Can view prices, calculate margins, create boards, and track stock.") }
            item { RoleCard(AppRole.STAFF, "Can view the price board and stock list without admin changes.") }

            item {
                Text(
                    "Firebase note: store each user's role under a users/{uid}/role path and enforce it with Realtime Database rules.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun RoleCard(role: AppRole, detail: String) {
    val icon = when (role) {
        AppRole.ADMIN -> Icons.Default.AdminPanelSettings
        AppRole.VENDOR -> Icons.Default.PointOfSale
        AppRole.STAFF -> Icons.Default.Badge
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF5D4037))
            Column(Modifier.padding(start = 12.dp)) {
                Text(role.label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(detail, color = Color.Gray, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}
