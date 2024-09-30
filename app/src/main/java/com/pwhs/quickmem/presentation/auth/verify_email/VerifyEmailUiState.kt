package com.pwhs.quickmem.presentation.auth.verify_email

data class VerifyEmailUiState(
    val otp: String = "",
    val email: String = "",
    val isOtpValid: Boolean = false,
    val countdown: Int = 0,
)