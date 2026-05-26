package com.example.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserEntity
import com.example.data.local.UserRole
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _phoneInput = MutableStateFlow("")
    val phoneInputState: StateFlow<String> = _phoneInput.asStateFlow()

    private val _otpInput = MutableStateFlow("")
    val otpInputState: StateFlow<String> = _otpInput.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    init {
        // Automatically check if there is an active session (defaulting to the Ramesh Homeowner profile for onboarding)
        viewModelScope.launch {
            val defaultUser = repository.getDirectUser("homeowner_demo")
            if (defaultUser != null) {
                // Initialize state
            }
        }
    }

    fun updatePhone(phone: String) {
        _phoneInput.value = phone
    }

    fun updateOtp(otp: String) {
        _otpInput.value = otp
    }

    fun sendOtp() {
        if (_phoneInput.value.trim().length < 10) {
            _authState.value = AuthState.Error("Please enter a valid 10-digit Indian mobile number")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            // Simulate MSG91 high-priority endpoint delivery
            kotlinx.coroutines.delay(1000)
            _otpSent.value = true
            _authState.value = AuthState.Idle
        }
    }

    fun verifyOtp() {
        if (_otpInput.value.trim() != "123456" && _otpInput.value.trim() != "1234") {
            _authState.value = AuthState.Error("Invalid OTP entered. Please use 1234 or 123456 for simulator.")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val user = repository.getDirectUser("homeowner_demo")
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success(user)
            } else {
                // Register standard customer
                val newUser = UserEntity(
                    id = "user_new_" + System.currentTimeMillis(),
                    phone = _phoneInput.value,
                    email = "new_user@abhiyantrisetu.com",
                    name = "Verified Guest Client",
                    role = UserRole.HOMEOWNER,
                    cityId = "g_noida",
                    cityName = "Greater Noida"
                )
                repository.registerOrUpdateUser(newUser)
                _currentUser.value = newUser
                _authState.value = AuthState.Success(newUser)
            }
        }
    }

    fun signInWithGoogle(context: Context, email: String, name: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // Real Google Auth credential storage simulation
                kotlinx.coroutines.delay(800)
                val cleanEmail = email.ifEmpty { "client@abhiyantrisetu.com" }
                val cleanName = name.ifEmpty { "Google Verified Client" }

                // Check if user already exists
                val localId = "google_" + cleanEmail.replace("@", "_").replace(".", "_")
                val existing = repository.getDirectUser(localId)

                val finalUser = existing ?: UserEntity(
                    id = localId,
                    phone = "+919999988888",
                    email = cleanEmail,
                    name = cleanName,
                    role = UserRole.HOMEOWNER,
                    cityId = "noida",
                    cityName = "Noida"
                )

                if (existing == null) {
                    repository.registerOrUpdateUser(finalUser)
                }

                _currentUser.value = finalUser
                _authState.value = AuthState.Success(finalUser)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Google OAuth failed: ${e.localizedMessage}")
            }
        }
    }

    fun selectRoleBypass(role: UserRole) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val targetId = when (role) {
                UserRole.HOMEOWNER -> "homeowner_demo"
                UserRole.PROVIDER -> "provider_1" // Amit Sharma
                UserRole.ADMIN -> "admin_demo"
            }
            val user = repository.getDirectUser(targetId)
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("User role $role not synchronized.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
        _otpSent.value = false
        _phoneInput.value = ""
        _otpInput.value = ""
    }
}

class AuthViewModelFactory(private val repository: MarketplaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
