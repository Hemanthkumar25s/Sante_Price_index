package com.sante.priceindex.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sante.priceindex.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(800) // Reduced from 2000ms for faster startup
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sante",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Price Index",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}
