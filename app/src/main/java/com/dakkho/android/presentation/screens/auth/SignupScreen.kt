package com.dakkho.android.presentation.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
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
import com.dakkho.android.presentation.components.GlassCard
import com.dakkho.android.presentation.components.GradientButton
import com.dakkho.android.presentation.theme.DeepBlue
import com.dakkho.android.presentation.theme.DesignToken
import com.dakkho.android.presentation.theme.Error
import com.dakkho.android.presentation.theme.Green
import com.dakkho.android.presentation.theme.Neutral400
import com.dakkho.android.presentation.theme.Neutral500
import com.dakkho.android.presentation.theme.Neutral700
import com.dakkho.android.presentation.theme.SkyBlue
import com.dakkho.android.presentation.theme.SurfaceLight
import com.dakkho.android.presentation.theme.Warning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignupScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val signupState by viewModel.signupState.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val fullNameError by viewModel.fullNameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val passwordStrength by viewModel.passwordStrength.collectAsState()
    val institutes by viewModel.institutes.collectAsState()
    val selectedInstituteId by viewModel.selectedInstituteId.collectAsState()
    val instituteSearchQuery by viewModel.instituteSearchQuery.collectAsState()
    val technologies by viewModel.technologies.collectAsState()
    val selectedTechnology by viewModel.selectedTechnology.collectAsState()
    val otp by viewModel.otp.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val otpCooldown by viewModel.otpCooldown.collectAsState()
    val isLoadingInstitutes by viewModel.isLoadingInstitutes.collectAsState()
    val isLoadingTechnologies by viewModel.isLoadingTechnologies.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(signupState) {
        when (val state = signupState) {
            is SignupViewModel.SignupState.OtpVerified -> {
                onNavigateToHome()
            }
            is SignupViewModel.SignupState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
                viewModel.resetState()
            }
            else -> { /* Idle, Loading, OtpSent, Success */ }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp48))

            // Branding
            Text(
                text = "DAKKHO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = SurfaceLight
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))

            // Step indicator
            StepIndicator(
                currentStep = currentStep,
                totalSteps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // Step content card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    },
                    label = "signup_steps"
                ) { step ->
                    when (step) {
                        1 -> Step1BasicInfo(
                            fullName = fullName,
                            email = email,
                            password = password,
                            passwordVisible = passwordVisible,
                            fullNameError = fullNameError,
                            emailError = emailError,
                            passwordError = passwordError,
                            passwordStrength = passwordStrength,
                            onFullNameChanged = viewModel::onFullNameChanged,
                            onEmailChanged = viewModel::onEmailChanged,
                            onPasswordChanged = viewModel::onPasswordChanged,
                            onPasswordVisibilityToggle = viewModel::onPasswordVisibilityToggle,
                            onNext = viewModel::goToNextStep
                        )
                        2 -> Step2Institute(
                            institutes = institutes,
                            selectedInstituteId = selectedInstituteId,
                            searchQuery = instituteSearchQuery,
                            isLoading = isLoadingInstitutes,
                            onSearchQueryChanged = viewModel::onInstituteSearchChanged,
                            onInstituteSelected = viewModel::onInstituteSelected,
                            onBack = viewModel::goToPreviousStep,
                            onNext = viewModel::goToNextStep
                        )
                        3 -> Step3Technology(
                            technologies = technologies,
                            selectedTechnology = selectedTechnology,
                            isLoading = isLoadingTechnologies,
                            onTechnologySelected = viewModel::onTechnologySelected,
                            onBack = viewModel::goToPreviousStep,
                            onNext = viewModel::goToNextStep,
                            isLoadingSignup = signupState is SignupViewModel.SignupState.Loading
                        )
                        4 -> Step4OtpVerification(
                            email = email,
                            otp = otp,
                            otpError = otpError,
                            cooldown = otpCooldown,
                            isLoading = signupState is SignupViewModel.SignupState.Loading,
                            onOtpChanged = viewModel::onOtpChanged,
                            onVerify = viewModel::verifyOtp,
                            onResend = viewModel::resendOtp,
                            onBack = viewModel::goToPreviousStep
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SurfaceLight.copy(alpha = 0.8f)
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = SurfaceLight,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.Space.dp32))
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val stepNum = index + 1
            val isActive = stepNum == currentStep
            val isCompleted = stepNum < currentStep

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Green
                            isActive -> SkyBlue
                            else -> Neutral400.copy(alpha = 0.4f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = SurfaceLight
                    )
                } else {
                    Text(
                        text = stepNum.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isActive) SurfaceLight else SurfaceLight.copy(alpha = 0.6f)
                    )
                }
            }

            if (index < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (stepNum < currentStep) SkyBlue
                            else SurfaceLight.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

@Composable
private fun Step1BasicInfo(
    fullName: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    fullNameError: String?,
    emailError: String?,
    passwordError: String?,
    passwordStrength: PasswordStrength,
    onFullNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onNext: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
        Text(
            text = "Step 1 of 4 — Basic Information",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChanged,
            label = { Text("Full Name") },
            singleLine = true,
            isError = fullNameError != null,
            supportingText = fullNameError?.let { { Text(it, color = Error) } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                    contentDescription = null,
                    tint = if (fullNameError != null) Error else Neutral400,
                    modifier = Modifier.size(DesignToken.IconSize.medium)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkyBlue,
                unfocusedBorderColor = Neutral400,
                focusedLabelColor = SkyBlue,
                errorBorderColor = Error
            ),
            shape = RoundedCornerShape(DesignToken.Space.dp12),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
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
                errorBorderColor = Error
            ),
            shape = RoundedCornerShape(DesignToken.Space.dp12),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
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
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                onNext()
            }),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_lock_lock),
                    contentDescription = null,
                    tint = if (passwordError != null) Error else Neutral400,
                    modifier = Modifier.size(DesignToken.IconSize.medium)
                )
            },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) android.R.drawable.ic_menu_view
                            else android.R.drawable.ic_menu_close_clear_cancel
                        ),
                        contentDescription = null,
                        tint = Neutral400,
                        modifier = Modifier.size(DesignToken.IconSize.medium)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkyBlue,
                unfocusedBorderColor = Neutral400,
                focusedLabelColor = SkyBlue,
                errorBorderColor = Error
            ),
            shape = RoundedCornerShape(DesignToken.Space.dp12),
            modifier = Modifier.fillMaxWidth()
        )

        // Password strength indicator
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            PasswordStrengthIndicator(strength = passwordStrength)
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

        GradientButton(
            text = "Continue",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp4)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            when {
                                strength == PasswordStrength.WEAK && index == 0 -> Error
                                strength == PasswordStrength.MEDIUM && index < 2 -> Warning
                                strength == PasswordStrength.STRONG -> Green
                                else -> Neutral400.copy(alpha = 0.2f)
                            }
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
        Text(
            text = when (strength) {
                PasswordStrength.WEAK -> "Weak password"
                PasswordStrength.MEDIUM -> "Medium password"
                PasswordStrength.STRONG -> "Strong password"
            },
            style = MaterialTheme.typography.labelSmall,
            color = when (strength) {
                PasswordStrength.WEAK -> Error
                PasswordStrength.MEDIUM -> Warning
                PasswordStrength.STRONG -> Green
            }
        )
    }
}

@Composable
private fun Step2Institute(
    institutes: List<com.dakkho.android.domain.model.Institute>,
    selectedInstituteId: String?,
    searchQuery: String,
    isLoading: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onInstituteSelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(
            text = "Select Institute",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
        Text(
            text = "Step 2 of 4 — Where do you study?",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Search institute...") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = null,
                    tint = Neutral400,
                    modifier = Modifier.size(DesignToken.IconSize.medium)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SkyBlue,
                unfocusedBorderColor = Neutral400,
                focusedLabelColor = SkyBlue
            ),
            shape = RoundedCornerShape(DesignToken.Space.dp12),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        // Institute list
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else {
            val filteredInstitutes = institutes.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp4)
            ) {
                items(filteredInstitutes, key = { it.id }) { institute ->
                    val isSelected = selectedInstituteId == institute.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(DesignToken.Space.dp8))
                            .background(
                                if (isSelected) SkyBlue.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SkyBlue else Neutral400.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(DesignToken.Space.dp8)
                            )
                            .clickable { onInstituteSelected(institute.id) }
                            .padding(DesignToken.Space.dp12),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = institute.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) SkyBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Skip option
            Text(
                text = "Skip this step",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Neutral500,
                modifier = Modifier
                    .clickable { onInstituteSelected("") }
                    .padding(vertical = DesignToken.Space.dp8)
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp16))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            GradientButton(
                text = "Back",
                onClick = onBack,
                gradient = Brush.linearGradient(
                    colors = listOf(Neutral400, Neutral700)
                ),
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = "Continue",
                onClick = onNext,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step3Technology(
    technologies: List<com.dakkho.android.domain.model.Technology>,
    selectedTechnology: String?,
    isLoading: Boolean,
    onTechnologySelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isLoadingSignup: Boolean
) {
    Column {
        Text(
            text = "Select Technology",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
        Text(
            text = "Step 3 of 4 — What do you study?",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8),
                verticalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8)
            ) {
                technologies.forEach { tech ->
                    val isSelected = selectedTechnology == tech.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(DesignToken.Space.dp8))
                            .background(
                                if (isSelected) SkyBlue.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SkyBlue else Neutral400.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(DesignToken.Space.dp8)
                            )
                            .clickable { onTechnologySelected(tech.name) }
                            .padding(horizontal = DesignToken.Space.dp16, vertical = DesignToken.Space.dp10)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tech.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (isSelected) SkyBlue else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(DesignToken.Space.dp4))
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Green
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp20))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp12)
        ) {
            GradientButton(
                text = "Back",
                onClick = onBack,
                gradient = Brush.linearGradient(
                    colors = listOf(Neutral400, Neutral700)
                ),
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = "Create Account",
                onClick = onNext,
                isLoading = isLoadingSignup,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Step4OtpVerification(
    email: String,
    otp: String,
    otpError: String?,
    cooldown: Int,
    isLoading: Boolean,
    onOtpChanged: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Verify Your Email",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(DesignToken.Space.dp4))
        Text(
            text = "Step 4 of 4 — We sent a code to $email",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))

        // OTP input - 6 individual boxes
        OtpInputField(
            otp = otp,
            onOtpChanged = onOtpChanged
        )

        if (otpError != null) {
            Spacer(modifier = Modifier.height(DesignToken.Space.dp8))
            Text(
                text = otpError,
                style = MaterialTheme.typography.bodySmall,
                color = Error
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp24))

        // Resend
        if (cooldown > 0) {
            Text(
                text = "Resend code in ${cooldown}s",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral500
            )
        } else {
            Text(
                text = "Resend Code",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = SkyBlue,
                modifier = Modifier.clickable { onResend() }
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.Space.dp32))

        GradientButton(
            text = "Verify & Continue",
            onClick = onVerify,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(DesignToken.Space.dp12))

        GradientButton(
            text = "Back",
            onClick = onBack,
            gradient = Brush.linearGradient(
                colors = listOf(Neutral400, Neutral700)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OtpInputField(
    otp: String,
    onOtpChanged: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(DesignToken.Space.dp8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            val digit = if (index < otp.length) otp[index].toString() else ""
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(DesignToken.Space.dp8))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.5.dp,
                        color = if (index < otp.length) SkyBlue else Neutral400.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(DesignToken.Space.dp8)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (digit.isNotEmpty()) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Hidden text field to capture keyboard input
    androidx.compose.foundation.text.BasicTextField(
        value = otp,
        onValueChange = { newValue ->
            val filtered = newValue.filter { it.isDigit() }
            if (filtered.length <= 6) {
                onOtpChanged(filtered)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(0.dp)
    )
}


