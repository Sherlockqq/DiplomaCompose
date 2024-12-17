package com.midinatech.diplomacompose.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.midinatech.diplomacompose.data.BluetoothRepositoryImpl

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = BluetoothRepositoryImpl(context)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository) as T
    }
}