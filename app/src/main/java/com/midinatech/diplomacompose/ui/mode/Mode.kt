package com.midinatech.diplomacompose.ui.mode

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment

import com.midinatech.diplomacompose.R
import com.midinatech.diplomacompose.ui.view.ColorPicker
import com.midinatech.diplomacompose.ui.view.ImagePicker

sealed class Mode<T> {
    @get:DrawableRes
    abstract val icon: Int

    @Composable
    abstract fun SettingView(consumer: (T) -> Unit, titleChanged: (String) -> Unit, title: String)

    class PaintMode : Mode<PaintMode.Type>() {

        sealed class Type {
            abstract val color: Color

            class Pen(override val color: Color) : Type()
            class Fill(override val color: Color) : Type()
        }

        @DrawableRes
        override val icon: Int = R.drawable.ic_paint_mode

        @Composable
        override fun SettingView(
            consumer: (Type) -> Unit,
            titleChanged: (String) -> Unit,
            title: String,
        ) {
            var color by remember { mutableStateOf(Color.White) }
            var type: Type by remember { mutableStateOf(Type.Pen(color)) }

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pen),
                        contentDescription = "Pen",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .border(
                                width = 1.dp,
                                color = if (type is Type.Pen) Color.Blue else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { type = Type.Pen(color) }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_fill),
                        contentDescription = "Filling",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .border(
                                width = 1.dp,
                                color = if (type is Type.Fill) Color.Blue else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { type = Type.Fill(color) }
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .background(color)
                    )
                }

                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = title,
                        onValueChange = {
                            titleChanged(it)
                        },
                        label = { Text("Enter Title") },
                        modifier = Modifier
                            .height(70.dp)
                            .width(240.dp)
                            .padding(8.dp)
                    )

                    ColorPicker(
                        modifier = Modifier
                            .padding(8.dp),
                        onColorSelected = {
                            color = it
                            type = if (type is Type.Fill) {
                                Type.Fill(color)
                            } else {
                                Type.Pen(color)
                            }
                        }
                    )
                }

                consumer(type)
            }
        }

    }


    class ImageMode : Mode<Uri>() {
        @DrawableRes
        override val icon: Int = R.drawable.ic_image_mode


        @Composable
        override fun SettingView(
            consumer: (Uri) -> Unit,
            titleChanged: (String) -> Unit,
            title: String,
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = title,
                    onValueChange = {
                        titleChanged(it)
                    },
                    label = { Text("Enter Title") },
                    modifier = Modifier
                        .height(70.dp)
                        .width(240.dp)
                        .padding(8.dp)
                )

                ImagePicker(consumer)
            }
        }
    }

    class SpotifyMode : Mode<Unit>() {
        @DrawableRes
        override val icon: Int = R.drawable.ic_spotify_mode

        @Composable
        override fun SettingView(
            consumer: (Unit) -> Unit,
            titleChanged: (String) -> Unit,
            title: String,
        ) {
            Column {
                TextField(
                    value = title,
                    onValueChange = {
                        titleChanged(it)
                    },
                    label = { Text("Enter Title") },
                    modifier = Modifier
                        .height(70.dp)
                        .width(240.dp)
                        .padding(8.dp)
                )

                Button(onClick = {
                    consumer(Unit)
                }) {
                    Text("Get song's poster")
                }
            }
        }
    }
}
