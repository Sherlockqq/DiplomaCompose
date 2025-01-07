package com.midinatech.diplomacompose.domain

import androidx.compose.ui.graphics.Color

data class MatrixCell(val row: Int, val col: Int, val color: Color) {
    override fun toString(): String {
        val alpha = (color.alpha * 255).toInt()
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()

        return "$row,$col,$alpha,$red,$green,$blue"
    }
}