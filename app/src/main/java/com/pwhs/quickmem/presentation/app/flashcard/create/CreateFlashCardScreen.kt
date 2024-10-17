package com.pwhs.quickmem.presentation.app.flashcard.create

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.rememberImagePicker
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.pwhs.quickmem.R
import com.pwhs.quickmem.presentation.component.BottomSheetItem
import com.pwhs.quickmem.util.bitmapToUri
import com.pwhs.quickmem.util.loadingOverlay
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DrawFlashCardScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import timber.log.Timber

@Destination<RootGraph>(
    navArgs = CreateFlashCardArgs::class
)
@Composable
fun CreateFlashCardScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateFlashCardViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    canvasResultBack: ResultRecipient<DrawFlashCardScreenDestination, Bitmap>,
    resultNavigator: ResultBackNavigator<Boolean>,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    canvasResultBack.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> {
                Timber.d("Canceled")
            }

            is NavResult.Value -> {
                Timber.d("Value: ${result.value}")
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                CreateFlashCardUiEvent.FlashCardSaved -> {
                    Timber.d("Flashcard saved")
                    Toast.makeText(context, "Flashcard saved", Toast.LENGTH_SHORT).show()
                }

                CreateFlashCardUiEvent.FlashCardSaveError -> {
                    Timber.d("Flashcard save error")
                    Toast.makeText(context, "Flashcard save error", Toast.LENGTH_SHORT).show()
                }

                CreateFlashCardUiEvent.LoadImage -> {
                    Timber.d("Load image")
                }
            }
        }
    }
    CreateFlashCard(
        modifier = modifier,
        term = uiState.term,
        definition = uiState.definition,
        definitionImageUri = uiState.definitionImageUri,
        definitionImageURL = uiState.definitionImageURL ?: "",
        hint = uiState.hint ?: "",
        showHint = uiState.showHint,
        explanation = uiState.explanation ?: "",
        showExplanation = uiState.showExplanation,
        isLoaded = uiState.isLoading,
        onTermChanged = { viewModel.onEvent(CreateFlashCardUiAction.FlashCardTermChanged(it)) },
        onDefinitionChanged = {
            viewModel.onEvent(
                CreateFlashCardUiAction.FlashCardDefinitionChanged(
                    it
                )
            )
        },
        onDefinitionImageChanged = {
            viewModel.onEvent(
                CreateFlashCardUiAction.FlashCardDefinitionImageChanged(
                    it
                )
            )
        },
        onHintChanged = { viewModel.onEvent(CreateFlashCardUiAction.FlashCardHintChanged(it)) },
        onShowHintClicked = { viewModel.onEvent(CreateFlashCardUiAction.ShowHintClicked(it)) },
        onExplanationChanged = {
            viewModel.onEvent(
                CreateFlashCardUiAction.FlashCardExplanationChanged(
                    it
                )
            )
        },
        onShowExplanationClicked = {
            viewModel.onEvent(
                CreateFlashCardUiAction.ShowExplanationClicked(
                    it
                )
            )
        },
        onUploadImage = { viewModel.onEvent(CreateFlashCardUiAction.UploadImage(it)) },
        onNavigationBack = {
            resultNavigator.setResult(uiState.isCreated)
            navigator.navigateUp()
        },
        onSaveFlashCardClicked = {
            viewModel.onEvent(CreateFlashCardUiAction.SaveFlashCard)
        }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
)
@Composable
fun CreateFlashCard(
    modifier: Modifier = Modifier,
    term: String = "",
    definition: String = "",
    definitionImageUri: Uri? = null,
    definitionImageURL: String = "",
    isLoaded: Boolean = false,
    hint: String = "",
    showHint: Boolean = false,
    explanation: String = "",
    showExplanation: Boolean = false,
    onTermChanged: (String) -> Unit = {},
    onDefinitionChanged: (String) -> Unit = {},
    onDefinitionImageChanged: (Uri?) -> Unit = {},
    onHintChanged: (String) -> Unit = {},
    onShowHintClicked: (Boolean) -> Unit = {},
    onExplanationChanged: (String) -> Unit = {},
    onShowExplanationClicked: (Boolean) -> Unit = {},
    onUploadImage: (Uri) -> Unit = {},
    onNavigationBack: () -> Unit = {},
    onSaveFlashCardClicked: () -> Unit = {},
) {

    var bottomSheetSetting = rememberModalBottomSheetState()
    var showBottomSheetSetting by remember {
        mutableStateOf(false)
    }

    val imageCropper = rememberImageCropper()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imagePicker = rememberImagePicker(onImage = { uri ->
        scope.launch {
            val result = imageCropper.crop(uri, context)
            when (result) {
                CropResult.Cancelled -> { /* Handle cancellation */
                }

                is CropError -> { /* Handle error */
                }

                is CropResult.Success -> {
                    Timber.d("Cropped image: ${result.bitmap}")
                    onDefinitionImageChanged(context.bitmapToUri(result.bitmap))
                }
            }
        }
    })

    val cropState = imageCropper.cropState
    if (cropState != null) {
        ImageCropperDialog(
            state = cropState, style = CropperStyle(
                backgroundColor = Color.Black.copy(alpha = 0.8f),
                rectColor = Color.White,
                overlay = Color.Black.copy(alpha = 0.5f),
            )
        )
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Flashcard",
                        style = typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationBack) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showBottomSheetSetting = true
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(
                        onClick = onSaveFlashCardClicked,
                        enabled = term.isNotEmpty() && definition.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Save",
                            tint = if (term.isNotEmpty() && definition.isNotEmpty()) {
                                colorScheme.primary
                            } else {
                                colorScheme.onSurface.copy(alpha = 0.5f)
                            }
                        )
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxSize()
            .loadingOverlay(isLoaded)
    ) { innerPadding ->
        Box(contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 5.dp,
                            focusedElevation = 8.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable {
                                imagePicker.pick(
                                    mimetype = "image/*"
                                )
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (definitionImageUri != null) {
                                    AsyncImage(
                                        model = definitionImageUri,
                                        contentDescription = "Image for definition",
                                        modifier = Modifier.size(120.dp),
                                        contentScale = ContentScale.Crop,
                                        onSuccess = {
                                            onUploadImage(definitionImageUri)
                                        }
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_add_image),
                                        contentDescription = "Add Image to Definition",
                                        modifier = Modifier.size(120.dp),
                                        colorFilter = ColorFilter.tint(
                                            colorScheme.onSurface.copy(
                                                alpha = 0.5f
                                            )
                                        ),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Text(
                                    "Image for definition",
                                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 5.dp,
                            focusedElevation = 8.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            TextField(
                                value = term,
                                onValueChange = onTermChanged,
                                placeholder = {
                                    Text(
                                        "Term",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    errorContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                                ),
                                maxLines = 4
                            )

                            TextField(
                                value = definition,
                                onValueChange = onDefinitionChanged,
                                placeholder = {
                                    Text(
                                        "Definition",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    errorContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                                ),
                                maxLines = 4
                            )
                        }
                    }
                }

                item {
                    if (showHint) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 5.dp,
                                focusedElevation = 8.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surface
                            ),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    TextField(
                                        value = hint,
                                        onValueChange = onHintChanged,
                                        placeholder = {
                                            Text(
                                                "Hint",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Gray.copy(alpha = 0.5f)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            errorContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                                        ),
                                        maxLines = 4
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onShowHintClicked(false)
                                        onHintChanged("")
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Close",
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    if (showExplanation) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = 5.dp,
                                focusedElevation = 8.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.surface
                            ),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    TextField(
                                        value = explanation,
                                        onValueChange = onExplanationChanged,
                                        placeholder = {
                                            Text(
                                                "Explanation",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Gray.copy(alpha = 0.5f)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            errorContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                                        ),
                                        maxLines = 4
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onShowExplanationClicked(false)
                                        onExplanationChanged("")
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Close",
                                    )
                                }
                            }
                        }
                    }
                }

            }

        }

        if (showBottomSheetSetting) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheetSetting = false
                },
                sheetState = bottomSheetSetting,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetItem(
                        title = "Hint (to question)",
                        icon = R.drawable.ic_add,
                        onClick = {
                            onShowHintClicked(true)
                            showBottomSheetSetting = false
                        },
                    )
                    BottomSheetItem(
                        title = "Explanation (to answer)",
                        icon = R.drawable.ic_add,
                        onClick = {
                            onShowExplanationClicked(true)
                            showBottomSheetSetting = false
                        },
                    )
                }
            }
        }
    }
}


@PreviewLightDark()
@Composable
fun CreateFlashCardPreview() {
    CreateFlashCard()
}