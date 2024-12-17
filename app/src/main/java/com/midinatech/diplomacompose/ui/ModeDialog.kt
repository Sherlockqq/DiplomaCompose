package com.midinatech.diplomacompose.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.midinatech.diplomacompose.R


sealed class Mode {
    @get:DrawableRes
    abstract val icon: Int

    data class Paint(@DrawableRes override val icon: Int = R.drawable.ic_paint_mode) : Mode()
    data class Image(@DrawableRes override val icon: Int = R.drawable.ic_image_mode) : Mode()
    data class Spotify(@DrawableRes override val icon: Int = R.drawable.ic_spotify_mode) : Mode()
}

@Composable
fun ModeDialog(
    onIconSelected: (Mode) -> Unit,
    onDismiss: () -> Unit,
) {
    val modes = listOf(Mode.Paint(), Mode.Image(), Mode.Spotify())

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Choose a mode", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                ) {
                    items(modes) { mode ->
                        IconButton(onClick = {
                            onIconSelected(mode)
                        }) {
                            Icon(
                                painter = painterResource(id = mode.icon),
                                contentDescription = "Selectable Icon",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Preview
@Composable
fun ModeDialogPreview() {
    MaterialTheme {
        ModeDialog(onIconSelected = {}, onDismiss = {})
    }
}