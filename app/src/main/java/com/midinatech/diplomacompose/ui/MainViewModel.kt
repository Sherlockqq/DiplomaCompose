package com.midinatech.diplomacompose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midinatech.diplomacompose.domain.BluetoothRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: BluetoothRepository) : ViewModel() {
    private val _matrix =
        mutableStateOf(Array(16) { mutableStateListOf(*Array(16) { Color.White }) })
    val matrix = _matrix.value

    var isConnected by mutableStateOf(false)
        private set

    var statusMessage by mutableStateOf("Disconnected")
        private set

    var mode: Mode by mutableStateOf(Mode.Paint())
        private set

    fun connectToDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.connectToDevice()
            isConnected = result
            statusMessage = if (result) "Connected" else "Connection Failed"
        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.disconnect()
            isConnected = !result
            statusMessage = if (result) "Disconnected" else "Disconnection Failed"
        }
    }

    fun sendData(data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.sendData(data)
            statusMessage = if (result) "Data Sent" else "Send Failed"
        }
    }

    fun setMatrixColor(row: Int, col: Int, color: Color) {
        _matrix.value[row][col] = color
    }

    fun clearMatrix() {
        for (row in 0 until 16) {
            for (col in 0 until 16) {
                setMatrixColor(row, col, Color.White)
            }
        }
    }

    fun onModeSelected(mode: Mode) {
        this.mode = mode
    }
}