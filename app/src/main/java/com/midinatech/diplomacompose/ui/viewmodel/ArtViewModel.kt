package com.midinatech.diplomacompose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midinatech.diplomacompose.domain.Art
import com.midinatech.diplomacompose.domain.ArtRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtViewModel(
    private val artRepository: ArtRepository,
) : ViewModel() {

    private val _artsStateFlow = MutableStateFlow<List<Art>>(emptyList())
    val artsStateFlow: StateFlow<List<Art>> = _artsStateFlow

    private val _errorMessageFlow = MutableStateFlow<String>("")
    val errorMessageFlow: StateFlow<String> = _errorMessageFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                artRepository.loadArts().collect { artList ->
                    _artsStateFlow.value = artList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessageFlow.value = "Failed to load arts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onRemoveClicked(art: Art) {
        viewModelScope.launch(Dispatchers.IO) {
            artRepository.removeArt(art)
        }
    }
}
