package com.midinatech.diplomacompose.domain

import kotlinx.coroutines.flow.Flow

interface SpotifyRepository {
    val trackFlow: Flow<Track>

    fun connect()
    fun disconnect()
}