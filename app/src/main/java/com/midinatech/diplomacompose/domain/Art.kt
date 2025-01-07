package com.midinatech.diplomacompose.domain

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

typealias Matrix = @RawValue List<List<Color>>

@Parcelize
data class Art(val id: Int = 0, val title: String, val timestamp: Long, val matrix: ByteArray): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Art

        if (title != other.title) return false
        if (!matrix.contentEquals(other.matrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + matrix.contentHashCode()
        return result
    }
}