package com.midinatech.diplomacompose.domain

import kotlinx.coroutines.flow.Flow

interface ArtRepository {
    suspend fun saveArt(art: Art)
    suspend fun loadArts(): Flow<List<Art>>
    suspend fun removeArt(art: Art)
}