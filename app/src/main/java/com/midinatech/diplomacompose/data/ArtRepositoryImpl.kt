package com.midinatech.diplomacompose.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.midinatech.diplomacompose.data.dto.ArtEntity
import com.midinatech.diplomacompose.domain.Art
import com.midinatech.diplomacompose.domain.ArtRepository
import com.midinatech.diplomacompose.toByteArray
import com.midinatech.diplomacompose.utils.artDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtRepositoryImpl(private val context: Context) : ArtRepository {
    private val dao = context.artDatabase.artDao()

    override suspend fun saveArt(art: Art) {
        context.artDatabase.artDao().insertArt(art.toArtEntity())
    }

    override suspend fun loadArts(): Flow<List<Art>> {
        return context.artDatabase.artDao().getArts().map { entity ->
            entity.map {
                Art(it.id, it.title, it.timestamp, it.matrix)
            }
        }
    }

    override suspend fun removeArt(art: Art) {
        context.artDatabase.artDao().removeArt(
            ArtEntity(
                id = art.id,
                title = art.title,
                timestamp = art.timestamp,
                matrix = art.matrix,
            )
        )
    }

    private fun Art.toArtEntity(): ArtEntity {
        return ArtEntity(
            title = this.title,
            timestamp = this.timestamp,
            matrix = this.matrix,
        )
    }
}

