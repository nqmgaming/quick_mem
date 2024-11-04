package com.pwhs.quickmem.presentation.app.classes.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pwhs.quickmem.core.datastore.AppManager
import com.pwhs.quickmem.core.datastore.TokenManager
import com.pwhs.quickmem.core.utils.Resources
import com.pwhs.quickmem.domain.repository.ClassRepository
import com.pwhs.quickmem.presentation.app.folder.detail.FolderDetailUiEvent
import com.wajahatkarim3.easyvalidation.core.collection_ktx.allUperCaseList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val tokenManager: TokenManager,
    private val appManager: AppManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ClassDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ClassDetailUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val joinClassCode: String = savedStateHandle["code"] ?: ""
        val id: String = savedStateHandle["id"] ?: ""
        val title: String = savedStateHandle["title"] ?: ""
        val description: String = savedStateHandle["description"] ?: ""
        viewModelScope.launch {
            appManager.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    _uiState.update {
                        it.copy(
                            isLogin = true,
                            joinClassCode = joinClassCode,
                            id = id,
                            title = title,
                            description = description
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLogin = false) }
                    onEvent(ClassDetailUiAction.NavigateToWelcomeClicked)
                }
            }
        }

        getClassByOwnerID()
    }

    fun onEvent(event: ClassDetailUiAction) {
        when (event) {
            ClassDetailUiAction.JoinClassClicked -> {
                TODO()
            }

            ClassDetailUiAction.NavigateToWelcomeClicked -> {
                _uiEvent.trySend(ClassDetailUiEvent.NavigateToWelcome)
            }
        }
    }

    private fun getClassByOwnerID() {
        viewModelScope.launch {
            val token = tokenManager.accessToken.firstOrNull() ?: ""
            val userId = appManager.userId.firstOrNull() ?: ""
            classRepository.getClassByOwnerID(token, userId).collectLatest { resources ->
                when(resources){
                    is Resources.Error -> {

                    }
                    is Resources.Loading -> {
                        TODO()
                    }
                    is Resources.Success -> {
                        TODO()
                    }
                }
            }
        }
    }
}