package com.midinatech.diplomacompose.ui.screen

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.midinatech.diplomacompose.R
import com.midinatech.diplomacompose.domain.Art
import com.midinatech.diplomacompose.ui.viewmodel.MainViewModel
import com.midinatech.diplomacompose.ui.view.ModeDialog
import com.midinatech.diplomacompose.ui.mode.Mode

private const val TAG = "MainScreen"

@Composable
fun MainScreen(context: Context, viewModel: MainViewModel, navController: NavController) {

    navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Art>("selectedArt") // Observe LiveData changes
        ?.observe(LocalLifecycleOwner.current) { art ->
            Log.d(TAG, "MainScreen: art: $art")
            viewModel.onArtLoaded(art)

        }

    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            uri?.let {
                viewModel.cropImageToMatrix(context, uri)
            }
        }
    }

    Scaffold(
        topBar = { Toolbar(viewModel, navController) }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val track by viewModel.trackFlow.collectAsState(initial = null)
            val title by viewModel.title.collectAsState()

            DrawingMatrix(viewModel)

            when (val currentMode = viewModel.mode) {
                is Mode.PaintMode -> currentMode.SettingView(
                    consumer = { type ->
                        viewModel.onTypeSelected(type)
                    },
                    titleChanged = { title ->
                        viewModel.onTitleChanged(title)
                    },
                    title = title
                )

                is Mode.ImageMode -> currentMode.SettingView(
                    consumer = { uri ->
                        cropImageLauncher.launch(
                            CropImageContractOptions(
                                uri,
                                CropImageOptions(
                                    guidelines = CropImageView.Guidelines.ON,
                                    cropShape = CropImageView.CropShape.RECTANGLE,
                                    fixAspectRatio = true,
                                )
                            )
                        )
                    },
                    titleChanged = { title ->
                        viewModel.onTitleChanged(title)
                    },
                    title = title
                )

                is Mode.SpotifyMode -> currentMode.SettingView(
                    consumer = { _ ->
                        viewModel.connectToSpotify()
                    },
                    titleChanged = { title ->
                        viewModel.onTitleChanged(title)
                    },
                    title = title
                )
            }

            track?.let {
                viewModel.onTitleChanged(it.title)
                Log.d(TAG, "MainScreen: ${it.title}")
                LaunchedEffect(it.imageUri) {
                    viewModel.cropImageToMatrix(context, it.imageUri)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(viewModel: MainViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }


    TopAppBar(
        title = { Text("Pixel Art") },
        actions = {
            IconButton(onClick = {
                if (viewModel.isConnected) {
                    viewModel.disconnectBluetooth()
                } else {
                    viewModel.connectToDevice()
                    Log.d(TAG, "Toolbar: enableBluetoothIf needeed")
                }

            }) {
                Icon(
                    imageVector = if (viewModel.isConnected) ImageVector.vectorResource(id = R.drawable.ic_bluetooth_on) else ImageVector.vectorResource(
                        id = R.drawable.ic_bluetooth_off
                    ),
                    contentDescription = "Bluetooth Status"
                )
            }

            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_modes),
                    contentDescription = "All modes"
                )
            }

            // IconButton for 3-dots icon
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Add items to the menu
                DropdownMenuItem(
                    text = { Text("Clear Art") },
                    onClick = {
                        viewModel.clearMatrix()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Save Art") },
                    onClick = {
                        expanded = false
                        viewModel.saveArt()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Load Art") },
                    onClick = {
                        // Handle Item 3 click
                        expanded = false
                        navController.navigate("screen_art")
                    }
                )
            }


            if (showDialog) {
                ModeDialog(
                    modes = listOf(
                        Mode.PaintMode(),
                        Mode.ImageMode(),
                        Mode.SpotifyMode()
                    ),
                    onIconSelected = {
                        viewModel.onModeSelected(it)
                        showDialog = false
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }
    )
}


@Composable
private fun DrawingMatrix(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .aspectRatio(1f)
                .pointerInput(viewModel.selectedType.color) {
                    detectTapGestures { offset ->
                        val cellSize = size.width / 16
                        val row = (offset.y / cellSize).toInt()
                        val col = (offset.x / cellSize).toInt()

                        if (viewModel.selectedType is Mode.PaintMode.Type.Fill) {
                            fillArea(viewModel, row, col, viewModel.selectedType.color)
                        } else {
                            if (row in 0 until 16 && col in 0 until 16) {
                                viewModel.setMatrixColor(row, col, viewModel.selectedType.color)
                            }
                        }
                    }
                }
                .pointerInput(viewModel.selectedType.color) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        if (viewModel.selectedType is Mode.PaintMode.Type.Fill) {
                            return@detectDragGestures
                        }
                        val cellSize = size.width / 16
                        val row = (change.position.y / cellSize).toInt()
                        val col = (change.position.x / cellSize).toInt()

                        if (row in 0 until 16 && col in 0 until 16) {
                            viewModel.setMatrixColor(row, col, viewModel.selectedType.color)
                        }
                    }
                }
        ) {
            val cellSize = size.width / 16
            for (row in 0 until 16) {
                for (col in 0 until 16) {
                    drawRect(
                        color = viewModel.matrix[row][col],
                        topLeft = Offset(x = col * cellSize, y = row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

private fun fillArea(viewModel: MainViewModel, row: Int, col: Int, newColor: Color) {
    val currentColor = viewModel.matrix[row][col]
    if (currentColor == newColor) return  // No need to fill if the color is the same

    val visited = mutableSetOf<Pair<Int, Int>>()
    val stack = mutableListOf(Pair(row, col))

    while (stack.isNotEmpty()) {
        val (r, c) = stack.removeAt(stack.size - 1)

        if (r !in 0 until 16 || c !in 0 until 16 || visited.contains(Pair(r, c))) {
            continue
        }

        visited.add(Pair(r, c))

        // Change color of the current cell
        viewModel.setMatrixColor(r, c, newColor)

        // Add neighbors to the stack
        if (r > 0 && viewModel.matrix[r - 1][c] == currentColor) stack.add(Pair(r - 1, c)) // Up
        if (r < 15 && viewModel.matrix[r + 1][c] == currentColor) stack.add(
            Pair(
                r + 1,
                c
            )
        ) // Down
        if (c > 0 && viewModel.matrix[r][c - 1] == currentColor) stack.add(
            Pair(
                r,
                c - 1
            )
        ) // Left
        if (c < 15 && viewModel.matrix[r][c + 1] == currentColor) stack.add(
            Pair(
                r,
                c + 1
            )
        ) // Right
    }
}