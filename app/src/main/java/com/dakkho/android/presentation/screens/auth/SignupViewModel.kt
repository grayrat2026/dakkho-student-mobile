package com.dakkho.android.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakkho.android.domain.model.Institute
import com.dakkho.android.domain.model.Technology
import com.dakkho.android.domain.model.User
import com.dakkho.android.domain.repository.AuthRepository
import com.dakkho.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    sealed class SignupState {
        data object Idle : SignupState()
        data object Loading : SignupState()
        data class Success(val user: User) : SignupState()
        data class Error(val message: String) : SignupState()
        data class OtpSent(val email: String) : SignupState()
        data class OtpVerified(val user: User) : SignupState()
    }

    private val _signupState = MutableStateFlow<SignupState>(SignupState.Idle)
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()

    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // Step 1: Basic info
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    // Step 2: Institute
    private val _institutes = MutableStateFlow<List<Institute>>(emptyList())
    val institutes: StateFlow<List<Institute>> = _institutes.asStateFlow()

    private val _selectedInstituteId = MutableStateFlow<String?>(null)
    val selectedInstituteId: StateFlow<String?> = _selectedInstituteId.asStateFlow()

    private val _instituteSearchQuery = MutableStateFlow("")
    val instituteSearchQuery: StateFlow<String> = _instituteSearchQuery.asStateFlow()

    // Step 3: Technology
    private val _technologies = MutableStateFlow<List<Technology>>(emptyList())
    val technologies: StateFlow<List<Technology>> = _technologies.asStateFlow()

    private val _selectedTechnology = MutableStateFlow<String?>(null)
    val selectedTechnology: StateFlow<String?> = _selectedTechnology.asStateFlow()

    // Step 4: OTP
    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    private val _otpCooldown = MutableStateFlow(0)
    val otpCooldown: StateFlow<Int> = _otpCooldown.asStateFlow()

    // Validation
    private val _fullNameError = MutableStateFlow<String?>(null)
    val fullNameError: StateFlow<String?> = _fullNameError.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    // Loading states for dropdown data
    private val _isLoadingInstitutes = MutableStateFlow(false)
    val isLoadingInstitutes: StateFlow<Boolean> = _isLoadingInstitutes.asStateFlow()

    private val _isLoadingTechnologies = MutableStateFlow(false)
    val isLoadingTechnologies: StateFlow<Boolean> = _isLoadingTechnologies.asStateFlow()

    // Password strength — computed from password flow
    private val _passwordStrength = MutableStateFlow(PasswordStrength.WEAK)
    val passwordStrength: StateFlow<PasswordStrength> = _passwordStrength.asStateFlow()

    init {
        loadInstitutes()
        loadTechnologies()
        observePasswordStrength()
    }

    private fun observePasswordStrength() {
        viewModelScope.launch {
            _password.collect { pwd ->
                _passwordStrength.value = computePasswordStrength(pwd)
            }
        }
    }

    private fun computePasswordStrength(pwd: String): PasswordStrength {
        return when {
            pwd.isBlank() -> PasswordStrength.WEAK
            pwd.length < 6 -> PasswordStrength.WEAK
            pwd.length < 8 -> PasswordStrength.MEDIUM
            pwd.any { it.isDigit() } && pwd.any { it.isUpperCase() } && pwd.any { !it.isLetterOrDigit() } -> PasswordStrength.STRONG
            pwd.any { it.isDigit() } || pwd.any { it.isUpperCase() } -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }

    // Step 1 handlers
    fun onFullNameChanged(name: String) {
        _fullName.value = name
        _fullNameError.value = null
    }

    fun onEmailChanged(email: String) {
        _email.value = email
        _emailError.value = null
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
        _passwordError.value = null
    }

    fun onPasswordVisibilityToggle() {
        _passwordVisible.value = !_passwordVisible.value
    }

    // Step 2 handlers
    fun onInstituteSearchChanged(query: String) {
        _instituteSearchQuery.value = query
    }

    fun onInstituteSelected(instituteId: String) {
        _selectedInstituteId.value = instituteId
    }

    // Step 3 handlers
    fun onTechnologySelected(technology: String) {
        _selectedTechnology.value = technology
    }

    // Step 4 handlers
    fun onOtpChanged(otp: String) {
        if (otp.length <= 6) {
            _otp.value = otp
            _otpError.value = null
        }
    }

    fun verifyOtp() {
        if (_otp.value.length != 6) {
            _otpError.value = "Enter 6-digit OTP"
            return
        }

        _signupState.value = SignupState.Loading

        viewModelScope.launch {
            val result = authRepository.verifyOtp(
                email = _email.value.trim(),
                otp = _otp.value
            )

            result.onSuccess { authResponse ->
                _signupState.value = SignupState.OtpVerified(
                    user = User(
                        id = authResponse.user.id,
                        email = authResponse.user.email,
                        fullName = authResponse.user.fullName,
                        instituteId = authResponse.user.instituteId,
                        technology = authResponse.user.technology,
                        avatarUrl = authResponse.user.avatarUrl,
                        role = authResponse.user.role,
                        phone = authResponse.user.phone,
                        isVerified = authResponse.user.isVerified ?: false,
                        createdAt = authResponse.user.createdAt
                    )
                )
            }.onFailure { error ->
                _signupState.value = SignupState.Error(
                    message = error.localizedMessage ?: "OTP verification failed"
                )
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            authRepository.signup(
                email = _email.value.trim(),
                password = _password.value,
                fullName = _fullName.value.trim(),
                instituteId = _selectedInstituteId.value,
                technology = _selectedTechnology.value,
                phone = null
            )
            startOtpCooldown()
        }
    }

    // Navigation
    fun goToNextStep() {
        when (_currentStep.value) {
            1 -> {
                if (validateStep1()) {
                    _currentStep.value = 2
                }
            }
            2 -> {
                _currentStep.value = 3
            }
            3 -> {
                if (validateStep3()) {
                    performSignup()
                }
            }
        }
    }

    fun goToPreviousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    private fun performSignup() {
        _signupState.value = SignupState.Loading

        viewModelScope.launch {
            val result = authRepository.signup(
                email = _email.value.trim(),
                password = _password.value,
                fullName = _fullName.value.trim(),
                instituteId = _selectedInstituteId.value,
                technology = _selectedTechnology.value,
                phone = null
            )

            result.onSuccess { authResponse ->
                _signupState.value = SignupState.OtpSent(email = _email.value.trim())
                _currentStep.value = 4
                startOtpCooldown()
            }.onFailure { error ->
                _signupState.value = SignupState.Error(
                    message = error.localizedMessage ?: "Signup failed. Please try again."
                )
            }
        }
    }

    private fun startOtpCooldown() {
        _otpCooldown.value = 60
        viewModelScope.launch {
            for (i in 60 downTo 1) {
                _otpCooldown.value = i
                delay(1000)
            }
            _otpCooldown.value = 0
        }
    }

    fun resetState() {
        _signupState.value = SignupState.Idle
    }

    // Validation
    private fun validateStep1(): Boolean {
        var isValid = true

        if (_fullName.value.trim().isBlank()) {
            _fullNameError.value = "Full name is required"
            isValid = false
        } else if (_fullName.value.trim().length < 2) {
            _fullNameError.value = "Name must be at least 2 characters"
            isValid = false
        }

        val emailValue = _email.value.trim()
        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Enter a valid email address"
            isValid = false
        }

        if (_password.value.isBlank()) {
            _passwordError.value = "Password is required"
            isValid = false
        } else if (_password.value.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun validateStep3(): Boolean {
        return true // Technology is optional
    }

    private fun loadInstitutes() {
        _isLoadingInstitutes.value = true
        viewModelScope.launch {
            _institutes.value = listOf(
                Institute(id = "inst_1", name = "Bangladesh University of Engineering and Technology"),
                Institute(id = "inst_2", name = "University of Dhaka"),
                Institute(id = "inst_3", name = "North South University"),
                Institute(id = "inst_4", name = "BRAC University"),
                Institute(id = "inst_5", name = "Islamic University of Technology"),
                Institute(id = "inst_6", name = "American International University-Bangladesh"),
                Institute(id = "inst_7", name = "Independent University Bangladesh"),
                Institute(id = "inst_8", name = "East West University"),
                Institute(id = "inst_9", name = "University of Chittagong"),
                Institute(id = "inst_10", name = "Rajshahi University of Engineering and Technology")
            )
            _isLoadingInstitutes.value = false
        }
    }

    private fun loadTechnologies() {
        _isLoadingTechnologies.value = true
        viewModelScope.launch {
            _technologies.value = listOf(
                Technology(id = "tech_1", name = "CSE", courseCount = 24),
                Technology(id = "tech_2", name = "EEE", courseCount = 18),
                Technology(id = "tech_3", name = "Civil", courseCount = 12),
                Technology(id = "tech_4", name = "Mechanical", courseCount = 15),
                Technology(id = "tech_5", name = "Architecture", courseCount = 8),
                Technology(id = "tech_6", name = "ECE", courseCount = 20),
                Technology(id = "tech_7", name = "URP", courseCount = 6),
                Technology(id = "tech_8", name = "MME", courseCount = 5),
                Technology(id = "tech_9", name = "Chemical", courseCount = 10),
                Technology(id = "tech_10", name = "BME", courseCount = 7),
                Technology(id = "tech_11", name = "IPE", courseCount = 9),
                Technology(id = "tech_12", name = "Naval", courseCount = 4)
            )
            _isLoadingTechnologies.value = false
        }
    }
}

enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}
