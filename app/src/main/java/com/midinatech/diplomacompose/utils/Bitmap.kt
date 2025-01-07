package com.midinatech.diplomacompose.utils

import android.graphics.Bitmap
import android.graphics.Color

fun List<List<Color>>.toBitmap(): Bitmap {
    val height = this.size
    val width = this.firstOrNull()?.size ?: 0

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Set each pixel color from the matrix
    for (y in 0 until height) {
        for (x in 0 until width) {
            val colorInt = this[y][x].toArgb()
            bitmap.setPixel(x, y, colorInt)
        }
    }
    return bitmap
}