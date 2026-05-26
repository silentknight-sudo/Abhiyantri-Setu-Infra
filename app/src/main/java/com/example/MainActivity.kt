package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserRole
import com.example.data.repository.MarketplaceRepository
import com.example.ui.admin.AdminDashboard
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.AuthViewModelFactory
import com.example.ui.homeowner.HomeownerDashboard
import com.example.ui.provider.ProviderDashboard
import com.example.ui.theme.AbhiyantriSetuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize local persistent database & repositories safely
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = MarketplaceRepository(database.dao())

        // 2. Setup ViewModel with custom factory conforming with DI guidance
        val authViewModel: AuthViewModel by viewModels {
            AuthViewModelFactory(repository)
        }

        setContent {
            AbhiyantriSetuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by authViewModel.authState.collectAsState()
                    val currentUser by authViewModel.currentUser.collectAsState()

                    if (currentUser == null) {
                        // Render Unified Auth Screen as entrypoint
                        AuthScreen(
                            viewModel = authState,
                            authVm = authViewModel,
                            onAuthSuccess = { user ->
                                // Trigger state synchronization checks
                            }
                        )
                    } else {
                        // Route to respective production portal based on profile role
                        val user = currentUser!!
                        when (user.role) {
                            UserRole.HOMEOWNER -> {
                                HomeownerDashboard(
                                    repository = repository,
                                    currentUser = user,
                                    onLogout = { authViewModel.logout() },
                                    coroutineScope = lifecycleScope
                                )
                            }
                            UserRole.PROVIDER -> {
                                ProviderDashboard(
                                    repository = repository,
                                    currentProviderId = user.id,
                                    onLogout = { authViewModel.logout() },
                                    coroutineScope = lifecycleScope
                                )
                            }
                            UserRole.ADMIN -> {
                                AdminDashboard(
                                    repository = repository,
                                    currentUser = user,
                                    onLogout = { authViewModel.logout() },
                                    coroutineScope = lifecycleScope
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
