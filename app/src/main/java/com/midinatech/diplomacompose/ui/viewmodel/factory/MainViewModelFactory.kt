package com.midinatech.diplomacompose.ui.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.midinatech.diplomacompose.data.ArtDatabase
import com.midinatech.diplomacompose.data.ArtRepositoryImpl
import com.midinatech.diplomacompose.data.BluetoothRepositoryImpl
import com.midinatech.diplomacompose.data.SpotifyRepositoryImpl
import com.midinatech.diplomacompose.ui.viewmodel.MainViewModel

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val bluetoothRepository = BluetoothRepositoryImpl(context)
        val spotifyRepository = SpotifyRepositoryImpl(context)
        val artRepository = ArtRepositoryImpl(context)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(bluetoothRepository, spotifyRepository, artRepository) as T
    }
}