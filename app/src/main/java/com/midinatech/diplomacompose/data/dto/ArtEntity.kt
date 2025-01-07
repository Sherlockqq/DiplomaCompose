package com.midinatech.diplomacompose.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.midinatech.diplomacompose.data.ColorConverter

@Entity(tableName = "art_table")
@TypeConverters(ColorConverter::class)
data class ArtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timestamp: Long,
    val matrix: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (timestamp != other.timestamp) return false
        if (!matrix.contentEquals(other.matrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + matrix.contentHashCode()
        return result
    }

}
