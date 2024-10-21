package com.pwhs.quickmem.presentation.app.study_set.study.learn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination<RootGraph>
@Composable
fun LearnFlashCardScreen(
    modifier: Modifier = Modifier,
    resultNavigator: ResultBackNavigator<Boolean>,
    navigator: DestinationsNavigator,
    viewModel: LearnFlashCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                else -> {}
            }

        }
    }
    LearnFlashCard(modifier = modifier)
}

@Composable
fun LearnFlashCard(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            Text(
                text = "LearnFlashCardScreen",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun LearnFlashCardScreenPreview() {
    MaterialTheme {
        LearnFlashCard()
    }
}