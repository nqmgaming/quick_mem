package com.pwhs.quickmem.presentation.app.study_set.detail.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pwhs.quickmem.R
import com.pwhs.quickmem.core.data.enums.LearnMode
import com.pwhs.quickmem.domain.model.color.ColorModel
import com.pwhs.quickmem.domain.model.flashcard.StudySetFlashCardResponseModel
import com.pwhs.quickmem.presentation.app.study_set.detail.component.ItemMenuBottomSheet
import com.pwhs.quickmem.presentation.component.QuickMemAlertDialog
import com.pwhs.quickmem.ui.theme.QuickMemTheme
import com.pwhs.quickmem.util.toColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialTabScreen(
    modifier: Modifier = Modifier,
    flashCards: List<StudySetFlashCardResponseModel> = emptyList(),
    isOwner: Boolean,
    onFlashcardClick: (String) -> Unit = {},
    onDeleteFlashCardClick: () -> Unit = {},
    onEditFlashCardClick: () -> Unit = {},
    onToggleStarClick: (String, Boolean) -> Unit = { _, _ -> },
    onAddFlashCardClick: () -> Unit = {},
    onMakeCopyClick: () -> Unit = {},
    studySetColor: Color = ColorModel.defaultColors.first().hexValue.toColor(),
    learningPercentFlipped: Int = 0,
    learningPercentQuiz: Int = 0,
    learningPercentTrueFalse: Int = 0,
    learningPercentWrite: Int = 0,
    onNavigateToLearn: (LearnMode, Boolean) -> Unit = { _, _ -> },
) {
    val menuBottomSheetState = rememberModalBottomSheetState()
    var showMenu by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var flashCardSelectedId by remember { mutableStateOf("") }
    val hintBottomSheet = rememberModalBottomSheetState()
    var showHint by remember { mutableStateOf(false) }
    val explanationBottomSheet = rememberModalBottomSheetState()
    var showExplanation by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var showGetAllDialog by remember { mutableStateOf(false) }
    var learningMode by remember { mutableStateOf(LearnMode.NONE) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Center
    ) {
        when {
            flashCards.isEmpty() -> {
                Column(
                    horizontalAlignment = CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.txt_add_your_material_to_get_started),
                        style = typography.titleLarge.copy(
                            fontWeight = Bold,
                            color = colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.txt_this_study_set_can_contain_flashcards_notes_and_files_on_certain_topic),
                        textAlign = TextAlign.Center,
                        style = typography.bodyMedium.copy(
                            color = colorScheme.onSurface,
                        ),
                    )
                    if (isOwner) {
                        Button(
                            onClick = onAddFlashCardClick,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.txt_add),
                                    tint = colorScheme.background,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = stringResource(R.string.txt_add_material),
                                    style = typography.titleMedium.copy(
                                        color = colorScheme.background
                                    )
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    if (!isOwner) {
                        item {
                            Column(
                                horizontalAlignment = CenterHorizontally,
                            ) {
                                Button(
                                    onClick = onMakeCopyClick,
                                    modifier = Modifier.padding(16.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = studySetColor
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.txt_make_a_copy),
                                        style = typography.titleMedium.copy(
                                            color = colorScheme.background
                                        )
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.txt_you_can_not_edit_this_study_set_just_create_a_copy_of_it),
                                    style = typography.bodyMedium.copy(
                                        color = colorScheme.onSurface
                                    ),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    if (isOwner) {
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                items(items = flashCards, key = { it.id }) { flashCard ->
                                    StudySetFlipCard(
                                        frontText = flashCard.term,
                                        backText = flashCard.definition,
                                        backgroundColor = colorScheme.background,
                                        backImage = flashCard.definitionImageURL,
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = stringResource(R.string.txt_choose_your_way_to_study),
                                style = typography.titleMedium.copy(
                                    color = colorScheme.onSurface,
                                    fontWeight = Bold
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        item {
                            LearnModeCard(
                                title = stringResource(R.string.txt_flip_flashcards),
                                icon = R.drawable.ic_flipcard,
                                onClick = {
                                    showGetAllDialog = true
                                    learningMode = LearnMode.FLIP
                                },
                                color = studySetColor,
                                learningPercentage = learningPercentFlipped
                            )
                        }
                        item {
                            LearnModeCard(
                                title = stringResource(R.string.txt_quiz),
                                icon = R.drawable.ic_quiz,
                                onClick = {
                                    showGetAllDialog = true
                                    learningMode = LearnMode.QUIZ
                                },
                                color = studySetColor,
                                learningPercentage = learningPercentQuiz
                            )
                        }
                        item {
                            LearnModeCard(
                                title = stringResource(R.string.txt_true_false),
                                icon = R.drawable.ic_tf,
                                onClick = {
                                    showGetAllDialog = true
                                    learningMode = LearnMode.TRUE_FALSE
                                },
                                color = studySetColor,
                                learningPercentage = learningPercentTrueFalse
                            )
                        }
                        item {
                            LearnModeCard(
                                title = stringResource(R.string.txt_write),
                                icon = R.drawable.ic_write,
                                onClick = {
                                    showGetAllDialog = true
                                    learningMode = LearnMode.WRITE
                                },
                                color = studySetColor,
                                learningPercentage = learningPercentWrite
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = CenterVertically,
                            horizontalArrangement = SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.txt_terms),
                                style = typography.titleMedium.copy(
                                    color = colorScheme.onSurface,
                                    fontWeight = Bold
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    items(items = flashCards, key = { it.id }) { flashCard ->
                        CardDetail(
                            isOwner = isOwner,
                            color = studySetColor,
                            front = flashCard.term,
                            back = flashCard.definition,
                            isStarred = flashCard.isStarred,
                            imageURL = flashCard.definitionImageURL,
                            isAIGenerated = flashCard.isAIGenerated,
                            onToggleStarClick = { isStarred ->
                                onToggleStarClick(flashCard.id, isStarred)
                            },
                            onMenuClick = {
                                (flashCard.hint
                                    ?: context.getString(R.string.txt_there_is_no_hint_for_this_flashcard)).also {
                                    hint = it
                                }
                                (flashCard.explanation
                                    ?: context.getString(R.string.txt_there_is_no_explanation_for_this_flashcard)).also {
                                    explanation = it
                                }
                                showMenu = true
                                flashCardSelectedId = flashCard.id
                                onFlashcardClick(flashCard.id)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.padding(60.dp))
                    }
                }
            }
        }
    }

    if (showMenu) {
        ModalBottomSheet(
            sheetState = menuBottomSheetState,
            onDismissRequest = {
                showMenu = false
            }
        ) {
            Column {
                ItemMenuBottomSheet(
                    onClick = {
                        showHint = true
                        showMenu = false
                    },
                    icon = Filled.HelpOutline,
                    title = stringResource(R.string.txt_hint)
                )
                ItemMenuBottomSheet(
                    onClick = {
                        showExplanation = true
                        showMenu = false
                    },
                    icon = Filled.HelpOutline,
                    title = stringResource(R.string.txt_explanation)
                )
                if (isOwner) {
                    ItemMenuBottomSheet(
                        onClick = {
                            onEditFlashCardClick()
                            showMenu = false
                        },
                        icon = Outlined.Edit,
                        title = stringResource(R.string.txt_edit)
                    )
                    ItemMenuBottomSheet(
                        onClick = {
                            showAlertDialog = true
                            showMenu = false
                        },
                        icon = Default.DeleteOutline,
                        title = stringResource(R.string.txt_delete),
                        color = Color.Red,
                    )
                }
            }
        }
    }

    if (showHint) {
        ModalBottomSheet(
            sheetState = hintBottomSheet,
            onDismissRequest = {
                hint = ""
                showHint = false
            }
        ) {
            Column {
                Text(
                    text = stringResource(R.string.txt_hint),
                    style = typography.titleMedium.copy(
                        color = colorScheme.onSurface,
                        fontWeight = Bold
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = hint.takeIf { it.isNotEmpty() } ?: stringResource(R.string.txt_no_hint),
                    style = typography.bodyMedium.copy(
                        color = colorScheme.onSurface,
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showExplanation) {
        ModalBottomSheet(
            sheetState = explanationBottomSheet,
            onDismissRequest = {
                hint = ""
                showExplanation = false
            }
        ) {
            Column {
                Text(
                    text = stringResource(R.string.txt_explanation),
                    style = typography.titleMedium.copy(
                        color = colorScheme.onSurface,
                        fontWeight = Bold
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = explanation.takeIf { it.isNotEmpty() }
                        ?: stringResource(R.string.txt_no_explanation),
                    style = typography.bodyMedium.copy(
                        color = colorScheme.onSurface,
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showAlertDialog) {
        QuickMemAlertDialog(
            onDismissRequest = {
                showAlertDialog = false
            },
            onConfirm = {
                showAlertDialog = false
                onDeleteFlashCardClick()
            },
            title = stringResource(R.string.txt_delete_flashcard),
            text = stringResource(R.string.txt_are_you_sure_you_want_to_delete_this_flashcard),
            confirmButtonTitle = stringResource(R.string.txt_delete),
            dismissButtonTitle = stringResource(R.string.txt_cancel),
            buttonColor = colorScheme.error,
        )
    }
    if (showGetAllDialog && flashCards.size > 10) {
        QuickMemAlertDialog(
            onDismissRequest = {
                showGetAllDialog = false
                onNavigateToLearn(learningMode, true)
                learningMode = LearnMode.NONE
            },
            onConfirm = {
                showGetAllDialog = false
                onNavigateToLearn(learningMode, false)
                learningMode = LearnMode.NONE
            },
            title = stringResource(R.string.txt_get_all),
            text = stringResource(R.string.txt_are_you_sure_you_want_to_get_all_flashcards),
            confirmButtonTitle = stringResource(R.string.txt_ok),
            dismissButtonTitle = stringResource(R.string.txt_no_thanks),
        )
    } else {
        onNavigateToLearn(learningMode, true)
        learningMode = LearnMode.NONE
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MaterialTabScreenPreview() {
    QuickMemTheme {
        MaterialTabScreen(
            isOwner = false,
            flashCards = listOf(
                StudySetFlashCardResponseModel(
                    id = "1",
                    term = "Term 1",
                    definition = "Definition 1",
                    definitionImageURL = "https://www.example.com/image1.jpg"
                ),
                StudySetFlashCardResponseModel(
                    id = "2",
                    term = "Term 2",
                    definition = "Definition 2",
                    definitionImageURL = "https://www.example.com/image2.jpg"
                ),
                StudySetFlashCardResponseModel(
                    id = "3",
                    term = "Term 3",
                    definition = "Definition 3",
                    definitionImageURL = "https://www.example.com/image3.jpg"
                ),
                StudySetFlashCardResponseModel(
                    id = "4",
                    term = "Term 4",
                    definition = "Definition 4",
                    definitionImageURL = "https://www.example.com/image4.jpg"
                ),
                StudySetFlashCardResponseModel(
                    id = "5",
                    term = "Term 5",
                    definition = "Definition 5",
                    definitionImageURL = "https://www.example.com/image5.jpg"
                ),
            )
        )
    }
}