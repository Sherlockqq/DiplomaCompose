package com.midinatech.diplomacompose.utils

import android.content.Context
import com.midinatech.diplomacompose.App
import com.midinatech.diplomacompose.data.ArtDatabase

private val Context.application: App get() = applicationContext as App

val Context.artDatabase: ArtDatabase get() = application.artDatabase