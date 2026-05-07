package com.sante.priceindex.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.sante.priceindex.R
import com.sante.priceindex.viewmodel.AuthState
import kotlinx.coroutines.launch

private val LoginInk = Color(0xFF123422)
private val LoginGreen = Color(0xFF1B5E20)
private val LoginMint = Color(0xFFE8F5E9)
private val LoginGold = Color(0xFFF9A825)
private val LoginRed = Color(0xFFB71C1C)

@Composable
fun LoginScreen(
    authState: AuthState,
    onLogin: (String, String) -> Unit,
    onGoogleLogin: (com.google.firebase.auth.AuthCredential) -> Unit,
    onClearError: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val webClientId = stringResource(id = R.string.default_web_client_id)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val canSubmit = email.isNotBlank() && password.isNotBlank() && !authState.isLoading

    val handleGoogleSignIn = {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
                onGoogleLogin(credential)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FBF4))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F3D26),
                            LoginGreen,
                            Color(0xFF3B8A4A)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(28.dp))

            BrandHeader()

            Spacer(Modifier.height(26.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Welcome back",
                            color = LoginInk,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Sign in to monitor mandi prices, margins, and board updates.",
                            color = Color(0xFF627266),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            onClearError()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email address") },
                        placeholder = { Text("vendor@sante.com") },
                        leadingIcon = {
                            Icon(Icons.Default.AlternateEmail, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(14.dp),
                        colors = loginTextFieldColors()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onClearError()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        placeholder = { Text("Enter password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (showPassword) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    }
                                )
                            }
                        },
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (canSubmit) {
                                    focusManager.clearFocus()
                                    onLogin(email, password)
                                }
                            }
                        ),
                        shape = RoundedCornerShape(14.dp),
                        colors = loginTextFieldColors()
                    )

                    AnimatedVisibility(visible = authState.error != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = authState.error.orEmpty(),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                color = LoginRed,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onLogin(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = canSubmit,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginGreen,
                            disabledContainerColor = Color(0xFFC9D7CB),
                            disabledContentColor = Color(0xFF657568)
                        )
                    ) {
                        if (authState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sign in",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFD8E4DA)
                        )
                        Text(
                            text = " OR ",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFD8E4DA)
                        )
                    }

                    androidx.compose.material3.OutlinedButton(
                        onClick = { handleGoogleSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = LoginInk
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD8E4DA))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GoogleIcon()
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Sign in with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            AccessPanel()

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun GoogleIcon() {
    // A simple custom Google G logo using Canvas
    androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            val width = size.width
            val height = size.height
            moveTo(width * 0.98f, height * 0.45f)
            lineTo(width * 0.52f, height * 0.45f)
            lineTo(width * 0.52f, height * 0.59f)
            lineTo(width * 0.84f, height * 0.59f)
            cubicTo(width * 0.82f, height * 0.73f, width * 0.73f, height * 0.84f, width * 0.52f, height * 0.84f)
            cubicTo(width * 0.33f, height * 0.84f, width * 0.17f, height * 0.69f, width * 0.17f, height * 0.5f)
            cubicTo(width * 0.17f, height * 0.31f, width * 0.33f, height * 0.16f, width * 0.52f, height * 0.16f)
            cubicTo(width * 0.61f, height * 0.16f, width * 0.69f, height * 0.19f, width * 0.75f, height * 0.25f)
            lineTo(width * 0.86f, height * 0.14f)
            cubicTo(width * 0.77f, height * 0.05f, width * 0.66f, 0f, width * 0.52f, 0f)
            cubicTo(width * 0.23f, 0f, 0f, height * 0.22f, 0f, height * 0.5f)
            cubicTo(0f, height * 0.78f, width * 0.23f, height, width * 0.52f, height)
            cubicTo(width * 0.81f, height, width, height * 0.78f, width, height * 0.5f)
            cubicTo(width, height * 0.48f, width, height * 0.47f, width * 0.98f, height * 0.45f)
            close()
        }
        
        // Use standard Google colors for the sectors
        // For simplicity in a single path, we just use Google Blue here.
        // In a real app, you'd use a vector drawable with 4 colors.
        drawPath(path, color = Color(0xFF4285F4))
    }
}

@Composable
private fun BrandHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(LoginGold),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = LoginGreen,
                modifier = Modifier.size(34.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Sante Price Index",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 30.sp
            )
            Text(
                text = "Vendor intelligence for fresh-market decisions",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AccessPanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.92f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Secure access",
                color = LoginInk,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TrustChip(
                    icon = Icons.Default.Security,
                    label = "Firebase Auth",
                    modifier = Modifier.weight(1f)
                )
                TrustChip(
                    icon = Icons.Default.TrendingUp,
                    label = "Live price data",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Use an account created in Firebase Authentication. No admin password is stored in the app.",
                color = Color(0xFF627266),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun TrustChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(LoginMint)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LoginGreen,
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = label,
            color = LoginInk,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LoginGreen,
    unfocusedBorderColor = Color(0xFFD8E4DA),
    focusedLabelColor = LoginGreen,
    cursorColor = LoginGreen,
    focusedLeadingIconColor = LoginGreen,
    unfocusedLeadingIconColor = Color(0xFF7B8A7E),
    focusedTrailingIconColor = LoginGreen,
    unfocusedTrailingIconColor = Color(0xFF7B8A7E)
)
