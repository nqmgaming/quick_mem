package com.pwhs.quickmem.presentation.app.library

sealed class LibraryUiAction() {
    data object Refresh : LibraryUiAction()
}

