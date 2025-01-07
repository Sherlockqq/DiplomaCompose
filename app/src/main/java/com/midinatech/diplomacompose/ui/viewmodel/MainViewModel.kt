package com.midinatech.diplomacompose.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.midinatech.diplomacompose.domain.Art
import com.midinatech.diplomacompose.domain.ArtRepository
import com.midinatech.diplomacompose.domain.BluetoothRepository
import com.midinatech.diplomacompose.domain.MatrixCell
import com.midinatech.diplomacompose.domain.SpotifyRepository
import com.midinatech.diplomacompose.domain.Track
import com.midinatech.diplomacompose.toByteArray
import com.midinatech.diplomacompose.toListOfLists
import com.midinatech.diplomacompose.ui.mode.Mode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val spotifyRepository: SpotifyRepository,
    private val artRepository: ArtRepository,
) : ViewModel() {

    private val _matrix =
        mutableStateOf(Array(16) { mutableStateListOf(*Array(16) { Color.Black }) })
    val matrix = _matrix.value

    val trackFlow: StateFlow<Track?> = spotifyRepository.trackFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    var isConnected by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf("Disconnected")
        private set


    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title


    var selectedType: Mode.PaintMode.Type by mutableStateOf(Mode.PaintMode.Type.Pen(Color.White))
        private set

    var mode: Mode<*> by mutableStateOf(Mode.PaintMode())
        private set

    fun setMatrixColor(row: Int, col: Int, color: Color) {
        _matrix.value[row][col] = color
        updateMatrix(row, col, color)
    }

    fun clearMatrix() {
        for (row in 0 until 16) {
            for (col in 0 until 16) {
                setMatrixColor(row, col, Color.Black)
            }
        }
    }

    fun onTypeSelected(type: Mode.PaintMode.Type) {
        selectedType = type
    }

    fun onModeSelected(mode: Mode<*>) {
        this.mode = mode
    }

    fun onTitleChanged(title: String) {
        _title.value = title
    }

    fun connectToDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = bluetoothRepository.connectToDevice()
            isConnected = result
            statusMessage = if (result) "Connected" else "Connection Failed"
        }
    }

    fun disconnectBluetooth() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = bluetoothRepository.disconnect()
            isConnected = !result
            statusMessage = if (result) "Disconnected" else "Disconnection Failed"
        }
    }


    fun connectToSpotify() {
        spotifyRepository.connect()
    }

    fun disconnectFromSpotify() {
        spotifyRepository.disconnect()
    }

    fun saveArt() {
        viewModelScope.launch(Dispatchers.IO) {
            artRepository.saveArt(
                Art(
                    title = title.value,
                    timestamp = System.currentTimeMillis(),
                    matrix = matrix.toListOfLists().toByteArray()
                )
            )
        }
    }


    fun cropImageToMatrix(context: Context, imageUri: Uri) {
        // Step 1: Load Bitmap from URI
        val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: return

        // Step 2: Resize Bitmap to 16x16
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 16, 16, true)

        // Step 3: Populate Matrix with Colors
        for (x in 0 until 16) {
            for (y in 0 until 16) {
                val pixelColor = scaledBitmap.getPixel(x, y) // Android Color
                val composeColor = Color(pixelColor) // Convert to Compose Color
                _matrix.value[x][y] = composeColor
                updateMatrix(x, y, composeColor)
            }
        }
    }

    fun onArtLoaded(art: Art) {
        setByteArrayToMatrix(art.matrix)
        selectedType = Mode.PaintMode.Type.Pen(Color.Red)
        _title.value = art.title
        mode = Mode.PaintMode()
    }

    private fun setByteArrayToMatrix(byteArray: ByteArray) {
        val height = 16
        val width = 16

        if (byteArray.size != width * height * 4) {
            throw IllegalArgumentException("ByteArray size does not match expected size")
        }

        var index = 0
        for (x in 0 until height) {
            for (y in 0 until width) {
                val a = byteArray[index].toInt() and 0xFF
                val r = byteArray[index + 1].toInt() and 0xFF
                val g = byteArray[index + 2].toInt() and 0xFF
                val b = byteArray[index + 3].toInt() and 0xFF

                val color = Color(r, g, b, a)

                _matrix.value[x][y] = color
                updateMatrix(x, y, color)

                index += 4
            }
        }
    }


    suspend fun cropImageToMatrix(context: Context, imageUriString: String) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUriString)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable
            val bitmap = (result as? BitmapDrawable)?.bitmap ?: return

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 16, 16, true)

            for (x in 0 until 16) {
                for (y in 0 until 16) {
                    val pixelColor = scaledBitmap.getPixel(x, y)
                    val composeColor = Color(pixelColor)
                    _matrix.value[x][y] = composeColor
                    updateMatrix(x, y, composeColor)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle errors
        }
    }

    private fun updateMatrix(row: Int, col: Int, color: Color) {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothRepository.sendData(MatrixCell(row, col, color))
        }
    }
}