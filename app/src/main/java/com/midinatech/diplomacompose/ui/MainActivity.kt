package com.midinatech.diplomacompose.ui

import com.midinatech.diplomacompose.ui.viewmodel.ArtViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.canhub.cropper.CropImageContract
import com.midinatech.diplomacompose.ui.screen.ArtScreen
import com.midinatech.diplomacompose.ui.screen.MainScreen
import com.midinatech.diplomacompose.ui.viewmodel.MainViewModel
import com.midinatech.diplomacompose.ui.viewmodel.factory.ArtViewModelFactory
import com.midinatech.diplomacompose.ui.viewmodel.factory.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private companion object {
        private const val TAG = "MainActivity"
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val artViewModel: ArtViewModel by lazy {
        ViewModelProvider(this, ArtViewModelFactory(this))[ArtViewModel::class.java]
    }
    private val enableBtLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mainViewModel.connectToDevice()
            } else {
                Toast.makeText(this, "Bluetooth is required for this app", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            uri?.let {
                mainViewModel.cropImageToMatrix(this, uri)
            }
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

    override fun onDestroy() {
        mainViewModel.disconnectBluetooth()
        mainViewModel.disconnectFromSpotify()
        super.onDestroy()
    }

    @Composable
    fun DiplomaApp() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "screen_main"
        ) {
            composable("screen_main") {
                MainScreen(
                    this@MainActivity,
                    mainViewModel,
                    navController
                )
            }
            composable("screen_art") { ArtScreen(artViewModel, navController) }
        }
    }

//    @Composable
//    fun DiplomaApp() {
//        Scaffold(
//            topBar = { Toolbar() }
//        ) { _ ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                val trackImage by mainViewModel.trackImageFlow.collectAsState(initial = null)
//
//                DrawingMatrix()
//
//                when (val currentMode = mainViewModel.mode) {
//                    is Mode.PaintMode -> currentMode.SettingView { type ->
//                        Log.d(TAG, "DiplomaApp: type: ${type.color}")
//                        Log.d(TAG, "DiplomaApp: type: $type")
//                        mainViewModel.setASelectedType(type)
//                    }
//                    is Mode.ImageMode -> currentMode.SettingView { uri ->
//                        cropImageLauncher.launch(
//                            CropImageContractOptions(
//                                uri,
//                                CropImageOptions(
//                                    guidelines = CropImageView.Guidelines.ON,
//                                    cropShape = CropImageView.CropShape.RECTANGLE,
//                                    fixAspectRatio = true,
//                                )
//                            )
//                        )
//                    }
//                    is Mode.SpotifyMode -> currentMode.SettingView { _ ->
//                        mainViewModel.connectToSpotify()
//                    }
//                }
//
//                trackImage?.let { track ->
//                    LaunchedEffect(track.imageUri) {
//                        mainViewModel.cropImageToMatrix(context = this@MainActivity, track.imageUri)
//                    }
//                }
//            }
//        }
//    }
//
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun Toolbar() {
//        var showDialog by remember { mutableStateOf(false) }
//        var expanded by remember { mutableStateOf(false) }
//
//
//        TopAppBar(
//            title = { Text("Pixel Art") },
//            actions = {
//
//                IconButton(onClick = {
//                    mainViewModel.clearMatrix()
//                }) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(R.drawable.ic_clear),
//                        contentDescription = "Clear Button"
//                    )
//                }
//
//                IconButton(onClick = {
//                    if (mainViewModel.isConnected) {
//                        mainViewModel.disconnectBluetooth()
//                    } else {
//                        mainViewModel.connectToDevice()
//                        Log.d(TAG, "Toolbar: enableBluetoothIf needeed")
//                    }
//
//                }) {
//                    Icon(
//                        imageVector = if (mainViewModel.isConnected) ImageVector.vectorResource(id = R.drawable.ic_bluetooth_on) else ImageVector.vectorResource(
//                            id = R.drawable.ic_bluetooth_off
//                        ),
//                        contentDescription = "Bluetooth Status"
//                    )
//                }
//
//                IconButton(onClick = {
//                    showDialog = true
//                }) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_all_modes),
//                        contentDescription = "All modes"
//                    )
//                }
//
//                // IconButton for 3-dots icon
//                IconButton(onClick = { expanded = true }) {
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = "More Options"
//                    )
//                }
//
//                DropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    // Add items to the menu
//                    DropdownMenuItem(
//                        text = { Text("Item 1") },
//                        onClick = {
//                            // Handle Item 1 click
//                            expanded = false
//                        }
//                    )
//                    DropdownMenuItem(
//                        text = { Text("Item 2") },
//                        onClick = {
//                            // Handle Item 2 click
//                            expanded = false
//                        }
//                    )
//                    DropdownMenuItem(
//                        text = { Text("Item 3") },
//                        onClick = {
//                            // Handle Item 3 click
//                            expanded = false
//                        }
//                    )
//                }
//
//
//                if (showDialog) {
//                    ModeDialog(
//                        modes = listOf(Mode.PaintMode(), Mode.ImageMode(), Mode.SpotifyMode()),
//                        onIconSelected = {
//                            mainViewModel.onModeSelected(it)
//                            showDialog = false
//                        },
//                        onDismiss = {
//                            showDialog = false
//                        }
//                    )
//                }
//            }
//        )
//    }
//
//    @Composable
//    fun DrawingMatrix() {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            Canvas(
//                modifier = Modifier
//                    .aspectRatio(1f)
//                    .pointerInput(mainViewModel.selectedType.color) {
//                        detectTapGestures { offset ->
//                            val cellSize = size.width / 16
//                            val row = (offset.y / cellSize).toInt()
//                            val col = (offset.x / cellSize).toInt()
//
////                            if (viewModel.selectedType is Mode.PaintMode.Type.Fill) {
////                                fillArea(row, col, viewModel.selectedType.color)
////                            } else {
//                            if (row in 0 until 16 && col in 0 until 16) {
//                                mainViewModel.setMatrixColor(row, col, mainViewModel.selectedType.color)
//                                Log.d(
//                                    TAG,
//                                    "DrawingMatrix: setMatrixColor: ${mainViewModel.selectedType.color}"
//                                )
//                            }
//                            //}
//                        }
//                    }
//                    .pointerInput(mainViewModel.selectedType.color) {
//                        detectDragGestures { change, _ ->
//                            change.consume()
////                            if (viewModel.selectedType is Mode.PaintMode.Type.Fill) {
////                                return@detectDragGestures
////                            }
//                            Log.d(
//                                TAG,
//                                "DrawingMatrix: setMatrixColora : ${mainViewModel.selectedType.color}"
//                            )
//
//                            val cellSize = size.width / 16
//                            val row = (change.position.y / cellSize).toInt()
//                            val col = (change.position.x / cellSize).toInt()
//
//                            if (row in 0 until 16 && col in 0 until 16) {
//                                mainViewModel.setMatrixColor(row, col, mainViewModel.selectedType.color)
//                            }
//                        }
//                    }
//            ) {
//                val cellSize = size.width / 16
//                for (row in 0 until 16) {
//                    for (col in 0 until 16) {
//                        drawRect(
//                            color = mainViewModel.matrix[row][col],
//                            topLeft = Offset(x = col * cellSize, y = row * cellSize),
//                            size = Size(cellSize, cellSize)
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    private fun fillArea(row: Int, col: Int, newColor: Color) {
//        val currentColor = mainViewModel.matrix[row][col]
//        if (currentColor == newColor) return  // No need to fill if the color is the same
//
//        val visited = mutableSetOf<Pair<Int, Int>>()
//        val stack = mutableListOf(Pair(row, col))
//
//        while (stack.isNotEmpty()) {
//            val (r, c) = stack.removeAt(stack.size - 1)
//
//            if (r !in 0 until 16 || c !in 0 until 16 || visited.contains(Pair(r, c))) {
//                continue
//            }
//
//            visited.add(Pair(r, c))
//
//            // Change color of the current cell
//            mainViewModel.setMatrixColor(r, c, newColor)
//
//            // Add neighbors to the stack
//            if (r > 0 && mainViewModel.matrix[r - 1][c] == currentColor) stack.add(Pair(r - 1, c)) // Up
//            if (r < 15 && mainViewModel.matrix[r + 1][c] == currentColor) stack.add(Pair(r + 1, c)) // Down
//            if (c > 0 && mainViewModel.matrix[r][c - 1] == currentColor) stack.add(Pair(r, c - 1)) // Left
//            if (c < 15 && mainViewModel.matrix[r][c + 1] == currentColor) stack.add(Pair(r, c + 1)) // Right
//        }
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun AppPreview() {
//        MaterialTheme {
//            DiplomaApp()
//        }
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun ToolbarPreview() {
//        MaterialTheme {
//            Toolbar()
//        }
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun DrawingMatrixPreview() {
//        MaterialTheme {
//            DrawingMatrix()
//        }
//    }
}