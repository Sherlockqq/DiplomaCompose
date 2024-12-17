package com.midinatech.diplomacompose.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.midinatech.diplomacompose.R

class MainActivity : ComponentActivity() {

    private companion object {
        private const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory(this))[MainViewModel::class.java]
    }

    private val enableBtLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.connectToDevice()
            } else {
                Toast.makeText(this, "Bluetooth is required for this app", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _ ->
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

        setContent {
            DiplomaApp()
        }
    }

    @Composable
    fun DiplomaApp() {
        Scaffold(
            topBar = { Toolbar() }
        ) { _ ->
            var selectedColor by remember { mutableStateOf(Color.Blue) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DrawingMatrix(selectedColor)


                when (viewModel.mode) {
                    is Mode.Paint -> ColorPicker(onColorSelected = {
                        selectedColor = it
                        Log.d(TAG, "DiplomaApp: onColorSelected : $selectedColor")
                    })

                    is Mode.Image -> {
                        Log.d(TAG, "DiplomaApp: Image")
                        PhotoSelectorView()
                    }

                    is Mode.Spotify -> {
                        Log.d(TAG, "DiplomaApp: Spotify")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Toolbar() {
        var showDialog by remember { mutableStateOf(false) }

        TopAppBar(
            title = { Text("Pixel Art") },
            actions = {

                IconButton(onClick = {
                    viewModel.clearMatrix()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_clear),
                        contentDescription = "Clear Button"
                    )
                }

                IconButton(onClick = {
                    if (viewModel.isConnected) {
                        viewModel.disconnect()
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

                if (showDialog) {
                    ModeDialog(onIconSelected = {
                        viewModel.onModeSelected(it)
                        showDialog = false
                    }, onDismiss = {
                        showDialog = false
                    })
                }
            }
        )
    }

    @Composable
    fun DrawingMatrix(selectedColor: Color) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .aspectRatio(1f)
                    .pointerInput(selectedColor) {
                        detectTapGestures { offset ->
                            val cellSize = size.width / 16
                            val row = (offset.y / cellSize).toInt()
                            val col = (offset.x / cellSize).toInt()

                            if (row in 0 until 16 && col in 0 until 16) {
                                viewModel.setMatrixColor(row, col, selectedColor)
                            }
                        }
                    }
                    .pointerInput(selectedColor) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val cellSize = size.width / 16
                            val row = (change.position.y / cellSize).toInt()
                            val col = (change.position.x / cellSize).toInt()

                            if (row in 0 until 16 && col in 0 until 16) {
                                Log.d(TAG, "DrawingMatrix: $selectedColor")
                                viewModel.setMatrixColor(row, col, selectedColor)
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


    @Composable
    fun PhotoSelectorView(maxSelectionCount: Int = 1) {
        var selectedImages by remember {
            mutableStateOf<List<Uri?>>(emptyList())
        }

        val buttonText = if (maxSelectionCount > 1) {
            "Select up to $maxSelectionCount photos"
        } else {
            "Select a photo"
        }

        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> selectedImages = listOf(uri) }
        )

        // I will start this off by saying that I am still learning Android development:
        // We are tricking the multiple photos picker here which is probably not the best way,
        // if you know of a better way to implement this feature drop a comment and let me know
        // how to improve this design
        val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = if (maxSelectionCount > 1) {
                maxSelectionCount
            } else {
                2
            }),
            onResult = { uris -> selectedImages = uris }
        )

        fun launchPhotoPicker() {
            if (maxSelectionCount > 1) {
                multiplePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                launchPhotoPicker()
            }) {
                Text(buttonText)
            }

            ImageLayoutView(selectedImages = selectedImages)
        }
    }

    @Composable
    fun ImageLayoutView(selectedImages: List<Uri?>) {
        LazyRow {
            items(selectedImages) { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AppPreview() {
        MaterialTheme {
            DiplomaApp()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ToolbarPreview() {
        MaterialTheme {
            Toolbar()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DrawingMatrixPreview() {
        MaterialTheme {
            DrawingMatrix(Color.Blue)
        }
    }
}