package com.midinatech.diplomacompose.ui.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.midinatech.diplomacompose.data.ArtDatabase
import com.midinatech.diplomacompose.data.ArtRepositoryImpl
import com.midinatech.diplomacompose.ui.viewmodel.ArtViewModel

class ArtViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val artRepository = ArtRepositoryImpl(context)
        @Suppress("UNCHECKED_CAST")
        return ArtViewModel(artRepository) as T
    }
}