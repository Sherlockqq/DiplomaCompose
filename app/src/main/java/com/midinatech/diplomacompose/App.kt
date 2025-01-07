package com.midinatech.diplomacompose

import android.app.Application
import com.midinatech.diplomacompose.data.ArtDatabase

class App : Application() {
    val artDatabase by lazy {
        ArtDatabase.getDatabase(this)
    }
}