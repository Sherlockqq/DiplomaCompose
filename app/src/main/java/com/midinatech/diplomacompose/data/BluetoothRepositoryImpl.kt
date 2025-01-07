package com.midinatech.diplomacompose.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.midinatech.diplomacompose.domain.BluetoothRepository
import com.midinatech.diplomacompose.domain.Matrix
import com.midinatech.diplomacompose.domain.MatrixCell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class BluetoothRepositoryImpl(private val context: Context): BluetoothRepository {

    private companion object {
        private const val TAG = "BluetoothRepositoryImpl"
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null



    @SuppressLint("MissingPermission")
    override suspend fun connectToDevice(): Boolean {
        val device = bluetoothAdapter.bondedDevices?.find { it.name == "HC-06" } ?: return false
        return withContext(Dispatchers.IO) {
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                outputStream = bluetoothSocket?.outputStream ?: return@withContext false
                bluetoothSocket?.connect()
                true
            } catch (e: IOException) {
                Log.e(TAG, "Failed to connect: ${e.message}")
                false
            }
        }
    }

    override suspend fun disconnect(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                bluetoothSocket?.close()
                bluetoothSocket = null
                true
            } catch (e: IOException) {
                Log.e(TAG, "Failed to disconnect: ${e.message}")
                false
            }
        }
    }

    override suspend fun sendData(data: MatrixCell): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                outputStream?.write("$data;".toByteArray())
                outputStream?.flush()
                true
            } catch (e: IOException) {
                Log.e(TAG, "Failed to send data: ${e.message}")
                false
            }
        }
    }
}