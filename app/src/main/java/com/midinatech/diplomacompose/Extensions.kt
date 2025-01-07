package com.midinatech.diplomacompose

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.io.ByteArrayOutputStream

fun List<List<Color>>.toByteArray(): ByteArray {
    // Flatten the 2D list into a 1D list of Ints (ARGB values)
    val argbValues = this.flatten().map { it.toArgb() }

    // Create a ByteArray to store ARGB values (4 bytes per Int)
    val byteArray = ByteArray(argbValues.size * 4)

    // Write each ARGB value into the ByteArray
    argbValues.forEachIndexed { index, argb ->
        val baseIndex = index * 4
        byteArray[baseIndex] = (argb shr 24 and 0xFF).toByte() // Alpha
        byteArray[baseIndex + 1] = (argb shr 16 and 0xFF).toByte() // Red
        byteArray[baseIndex + 2] = (argb shr 8 and 0xFF).toByte() // Green
        byteArray[baseIndex + 3] = (argb and 0xFF).toByte() // Blue
    }

    return byteArray
}


fun Bitmap.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(format, quality, outputStream)  // Compress and write to output stream
    return outputStream.toByteArray()
}


fun ByteArray.toBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Each pixel is represented by 4 bytes (ARGB)
    require(size == width * height * 4) { "ByteArray size does not match dimensions" }

    var index = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            val a = this[index].toInt() and 0xFF // Alpha
            val r = this[index + 1].toInt() and 0xFF // Red
            val g = this[index + 2].toInt() and 0xFF // Green
            val b = this[index + 3].toInt() and 0xFF // Blue

            val color = (a shl 24) or (r shl 16) or (g shl 8) or b
            bitmap.setPixel(x, y, color)

            index += 4
        }
    }

    return bitmap
}

fun Bitmap.toColorMatrix(): List<List<Color>> {
    val width = this.width
    val height = this.height

    // Create a matrix of colors
    return List(height) { y ->
        List(width) { x ->
            Color(this.getPixel(x, y)) // Convert each pixel's ARGB int to a Color object
        }
    }
}

fun Array<SnapshotStateList<Color>>.toBitmap(): Bitmap {
    val height = this.size
    val width = this.firstOrNull()?.size ?: 0

    // Create a Bitmap with the specified dimensions
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Set each pixel in the Bitmap based on the colors in the SnapshotStateList
    for (y in 0 until height) {
        for (x in 0 until width) {
            val color = this[y][x]
            val colorInt = color.toArgb() // Convert the Color to ARGB integer
            bitmap.setPixel(x, y, colorInt) // Set pixel in the bitmap
        }
    }

    return bitmap
}


fun List<List<Color>>.toBitmap(): Bitmap {
    val height = this.size
    val width = this.firstOrNull()?.size ?: 0

    // Create a Bitmap with the same dimensions
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Set each pixel in the Bitmap
    for (y in 0 until height) {
        for (x in 0 until width) {
            val colorInt = this[y][x].toArgb() // Convert Color to ARGB integer
            bitmap.setPixel(x, y, colorInt)   // Set pixel color in Bitmap
        }
    }
    return bitmap
}


fun Array<SnapshotStateList<Color>>.toListOfLists(): List<List<Color>> {
    return this.map { it.toList() }  // Convert each SnapshotStateList<Color> to a List<Color>
}
