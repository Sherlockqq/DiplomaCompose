package com.midinatech.diplomacompose.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.midinatech.diplomacompose.data.dto.ArtEntity
import com.midinatech.diplomacompose.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ArtEntity::class], version = 1)
abstract class ArtDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: ArtDatabase? = null

        fun getDatabase(context: Context): ArtDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ArtDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ArtDatabase::class.java,
                "art_database"
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val database = getDatabase(context)
                    val defaultArts = listOf(
                        ArtEntity(
                            title = "a",
                            timestamp = 1735737600,
                            matrix = List(16) { List(16) { Color.White } }.toByteArray()
                        ),
                        ArtEntity(
                            title = "b",
                            timestamp = 1735737600,
                            matrix = List(16) { List(16) { Color.Blue } }.toByteArray()
                        ),
                        ArtEntity(
                            title = "c",
                            timestamp = 1735737600,
                            matrix = createSmileyFaceMatrix().toByteArray()
                        )
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        database.artDao().insertArts(defaultArts)
                    }
                }
            }).build()
        }
    }

    abstract fun artDao(): ArtDao
}

fun createSmileyFaceMatrix(): List<List<Color>> {
    // Initialize a 16x16 matrix with yellow color
    val matrix = List(16) { List(16) { Color.Yellow } }.toMutableList()

    // Draw the smiley face by modifying the matrix
    // Coordinates for the face outline
    for (y in 4..11) {
        for (x in 4..11) {
            // Draw a circular face (rough approximation using square pixels)
            val dx = x - 8
            val dy = y - 8
            if (dx * dx + dy * dy <= 16) {
                matrix[y] = matrix[y].toMutableList().apply {
                    set(x, Color.Black) // Set the face outline to black
                }
            }
        }
    }

    // Draw the eyes (two black pixels)
    matrix[6] = matrix[6].toMutableList().apply { set(6, Color.Black) }
    matrix[6] = matrix[6].toMutableList().apply { set(9, Color.Black) }

    // Draw the smile (rough approximation of a smile using black pixels)
    for (x in 6..9) {
        matrix[10] = matrix[10].toMutableList().apply { set(x, Color.Black) }
    }

    return matrix
}


