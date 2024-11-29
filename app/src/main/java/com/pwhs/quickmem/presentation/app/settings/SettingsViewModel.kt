package com.pwhs.quickmem.presentation.app.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pwhs.quickmem.core.datastore.AppManager
import com.pwhs.quickmem.core.datastore.TokenManager
import com.pwhs.quickmem.core.utils.Resources
import com.pwhs.quickmem.domain.model.auth.VerifyPasswordRequestModel
import com.pwhs.quickmem.domain.repository.AuthRepository
import com.pwhs.quickmem.domain.repository.SearchQueryRepository
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val appManager: AppManager,
    private val authRepository: AuthRepository,
    private val searchQueryRepository: SearchQueryRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<SettingUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        initData()
    }

    fun onEvent(event: SettingUiAction) {
        when (event) {
            SettingUiAction.Logout -> logout()
            is SettingUiAction.OnChangePassword -> {
                _uiState.update {
                    it.copy(
                        password = event.password,
                        errorMessage = ""
                    )
                }
            }

            SettingUiAction.OnSubmitClick -> {
                verifyPassword()
            }

            is SettingUiAction.OnChangeCanChangeInfo -> {
                _uiState.update {
                    it.copy(canChangeInfo = event.canChangeInfo)
                }
            }

            is SettingUiAction.OnChangeType -> {
                _uiState.update {
                    it.copy(changeType = event.changeType)
                }
            }

            is SettingUiAction.OnChangePushNotifications -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isPushNotificationsEnabled = event.isPushNotificationsEnabled)
                    }
                    appManager.savePushNotifications(event.isPushNotificationsEnabled)
                }
            }

            is SettingUiAction.OnChangeAppPushNotifications -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isAppPushNotificationsEnabled = event.isAppPushNotificationsEnabled)
                    }
                    appManager.saveAppPushNotifications(event.isAppPushNotificationsEnabled)
                }
            }

            is SettingUiAction.Refresh -> {
                initData()
            }
        }
    }

    private fun initData() {
        viewModelScope.launch {
            try {
                val userId = appManager.userId.firstOrNull() ?: ""
                val fullName = appManager.userFullName.firstOrNull() ?: ""
                val username = appManager.username.firstOrNull() ?: ""
                val role = appManager.userRole.firstOrNull() ?: ""
                val email = appManager.userEmail.firstOrNull() ?: ""
                val isPushNotificationsEnabled = appManager.pushNotifications.firstOrNull() ?: false
                val isAppPushNotificationsEnabled =
                    appManager.appPushNotifications.firstOrNull() ?: false
                _uiState.update {
                    it.copy(
                        userId = userId,
                        fullName = fullName,
                        username = username,
                        email = email,
                        role = role,
                        isPushNotificationsEnabled = isPushNotificationsEnabled,
                        isAppPushNotificationsEnabled = isAppPushNotificationsEnabled,
                    )
                }
                getCustomerInfo()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun verifyPassword() {
        viewModelScope.launch {
            val token = tokenManager.accessToken.firstOrNull() ?: ""
            authRepository.verifyPassword(
                token, VerifyPasswordRequestModel(
                    userId = _uiState.value.userId,
                    password = _uiState.value.password
                )
            ).collect { resource ->
                when (resource) {
                    is Resources.Error -> {
                        _uiState.update {
                            it.copy(
                                canChangeInfo = false,
                                isLoading = false,
                                errorMessage = "Password is incorrect"
                            )
                        }
                    }

                    is Resources.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }

                    }

                    is Resources.Success -> {
                        _uiState.update {
                            it.copy(
                                canChangeInfo = resource.data?.success ?: false,
                                isLoading = false,
                                password = "",
                                errorMessage = ""
                            )
                        }
                        if (resource.data?.success == true) {
                            when (_uiState.value.changeType) {
                                SettingChangeValueEnum.FULL_NAME -> {
                                    _uiEvent.send(SettingUiEvent.NavigateToChangeFullName)
                                }

                                SettingChangeValueEnum.USERNAME -> {
                                    _uiEvent.send(SettingUiEvent.NavigateToChangeUsername)
                                }

                                SettingChangeValueEnum.EMAIL -> {
                                    _uiEvent.send(SettingUiEvent.NavigateToChangeEmail)
                                }

                                SettingChangeValueEnum.ROLE -> {
                                    _uiEvent.send(SettingUiEvent.NavigateToChangeRole)
                                }

                                SettingChangeValueEnum.NONE -> {
                                    // do nothing
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCustomerInfo() {
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                _uiState.update {
                    it.copy(
                        customerInfo = customerInfo
                    )
                }
            }

            override fun onError(error: PurchasesError) {
                // handle error
                Timber.e(error.message)
            }
        })
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                tokenManager.clearTokens()
                appManager.clearAllData()
                Purchases.sharedInstance.logOut()
                searchQueryRepository.clearSearchHistory()
                _uiEvent.send(SettingUiEvent.NavigateToLogin)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
