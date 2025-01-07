package com.midinatech.diplomacompose.ui.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import com.midinatech.diplomacompose.ui.viewmodel.ArtViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.midinatech.diplomacompose.domain.Art
import com.midinatech.diplomacompose.toBitmap
import kotlinx.coroutines.launch


private const val TAG = "ArtScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtScreen(viewModel: ArtViewModel, navController: NavController) {
    val artList by viewModel.artsStateFlow.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewModel.errorMessageFlow.collect { message ->
            errorMessage = message
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Art Gallery") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(artList.size) { index ->
                        val art = artList[index]
                        ArtItem(
                            art = art,
                            onClick = {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedArt", art)
                                navController.navigateUp()
                            },
                            onItemLongPress = {
                                Log.d(TAG, "ArtScreen: ")

                                snackbarHostState.currentSnackbarData?.dismiss()

                                coroutineScope.launch {
                                    val snackbarResult = snackbarHostState.showSnackbar(
                                        message = "Remove item?",
                                        actionLabel = "Remove",
                                        withDismissAction = true,
                                    )


                                    when (snackbarResult) {
                                        SnackbarResult.Dismissed -> Log.d("a", "Snackbar dismissed")
                                        SnackbarResult.ActionPerformed -> {
                                            viewModel.onRemoveClicked(art)
                                            Log.d(TAG, "ArtScreen: per")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            SnackbarHost(
                hostState = SnackbarHostState().apply {
                    coroutineScope.launch {
                        showSnackbar(message = errorMessage)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArtItem(
    art: Art, onClick: () -> Unit, onItemLongPress: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onItemLongPress() }
            )
    ) {
        Image(
            bitmap = art.matrix.toBitmap(16, 16).asImageBitmap(),
            contentDescription = art.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = art.title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun DeleteButton(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable { onDelete() }
    ) {
        Text(
            text = "Delete Item",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        )
    }
}

@Composable
fun SnackBar() {
    Snackbar(
        modifier = Modifier.padding(4.dp),
        dismissAction = {
            TextButton(onClick = {}) {
                Text(text = "Cancel")
            }
        },
        action = {
            TextButton(onClick = {}) {
                Text(text = "Remove")
            }
        }
    ) {
        Text(text = "Remove item?")
    }
}
