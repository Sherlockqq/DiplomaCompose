package com.midinatech.diplomacompose.domain

interface BluetoothRepository {
    suspend fun connectToDevice(): Boolean
    suspend fun disconnect(): Boolean
    suspend fun sendData(data: String): Boolean

}