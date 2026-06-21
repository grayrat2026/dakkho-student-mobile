package com.dakkho.android.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dakkho.android.presentation.components.AnimatedPage
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.Neutral700
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.SurfaceLight

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginViewModel.LoginState.Success -> {
                onNavigateToHome()
            }
            is LoginViewModel.LoginState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
                viewModel.resetState()
            }
            else -> { /* Idle or Loading */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(DeepBlue, SkyBlue),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = DesignToken.Space.dp24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp64))

            // Logo / Branding
            Text(
                text = "DAKKHO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = SurfaceLight
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

            Text(
                text = "Learn Without Limits",
                style = MaterialTheme.typography.bodyLarge,
                color = SurfaceLight.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp48))

            // Login Card
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp4))

                Text(
                    text = "Sign in to continue learning",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Email") },
                    singleLine = true,
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it, color = Error) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_dialog_email),
                            contentDescription = null,
                            tint = if (emailError != null) Error else Neutral400,
                            modifier = Modifier.size(DesignToken.IconSize.medium)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyBlue,
                        unfocusedBorderColor = Neutral400,
                        focusedLabelColor = SkyBlue,
                        errorBorderColor = Error,
                        errorLabelColor = Error
                    ),
                    shape = RoundedCornerShape(DesignToken.Space.dp12),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = { Text("Password") },
                    singleLine = true,
                    isError = passwordError != null,
                    supportingText = passwordError?.let { { Text(it, color = Error) } },
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.login()
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_lock_lock),
                            contentDescription = null,
                            tint = if (passwordError != null) Error else Neutral400,
                            modifier = Modifier.size(DesignToken.IconSize.medium)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = viewModel::onPasswordVisibilityToggle) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) android.R.drawable.ic_menu_view
                                    else android.R.drawable.ic_menu_close_clear_cancel
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Neutral400,
                                modifier = Modifier.size(DesignToken.IconSize.medium)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyBlue,
                        unfocusedBorderColor = Neutral400,
                        focusedLabelColor = SkyBlue,
                        errorBorderColor = Error,
                        errorLabelColor = Error
                    ),
                    shape = RoundedCornerShape(DesignToken.Space.dp12),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp8))

                // Forgot Password link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = SkyBlue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToForgotPassword() }
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                // Login Button
                GradientButton(
                    text = "Sign In",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.login()
                    },
                    isLoading = loginState is LoginViewModel.LoginState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Neutral400.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "  OR  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Neutral400.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                // Google Sign-In placeholder
                OutlinedButtonWithIcon(
                    text = "Continue with Google",
                    onClick = { /* Google Sign-In placeholder */ },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

                // Sign Up link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SkyBlue,
                        modifier = Modifier.clickable { onNavigateToSignup() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun OutlinedButtonWithIcon(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(DesignToken.ComponentSize.buttonHeight),
        shape = RoundedCornerShape(DesignToken.Space.dp12),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = DesignToken.Elevation.level1)
    ) {
        // Google 'G' placeholder icon
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "G",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF4285F4)
            )
        }
        Spacer(modifier = Modifier.width(DesignToken.Space.dp8))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
