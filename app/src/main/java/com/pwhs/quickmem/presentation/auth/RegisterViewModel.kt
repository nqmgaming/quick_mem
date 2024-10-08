package com.pwhs.quickmem.presentation.auth

import androidx.lifecycle.ViewModel
import com.pwhs.quickmem.core.data.UserRole
import com.pwhs.quickmem.core.utils.Resources
import com.pwhs.quickmem.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun register(
        email: String,
        userName: String,
        password: String,
        fullName: String,
        birthDay: Date,
        role: UserRole
    ) {
        Timber.d("Registering user with email: $email")
        return authRepository.register(email, userName, password, fullName, birthDay, role)
            .collect {
                when (it) {
                    is Resources.Success -> {
                        Timber.d("User registered")
                    }

                    is Resources.Error -> {
                        Timber.e(it.message)
                    }

                    is Resources.Loading -> {
                        Timber.d("Loading")
                    }
                }
            }

    }
}