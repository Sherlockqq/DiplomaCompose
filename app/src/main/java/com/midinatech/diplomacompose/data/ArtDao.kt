package com.midinatech.diplomacompose.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.midinatech.diplomacompose.data.dto.ArtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArt(artEntity: ArtEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArts(arts: List<ArtEntity>)

    @Delete
    suspend fun removeArt(artEntity: ArtEntity)

    @Query("SELECT * FROM art_table ORDER BY timestamp DESC")
    fun getArts(): Flow<List<ArtEntity>>
}
