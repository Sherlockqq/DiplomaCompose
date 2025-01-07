package com.midinatech.diplomacompose.data

import android.content.Context
import android.util.Log
import com.midinatech.diplomacompose.domain.SpotifyRepository
import com.midinatech.diplomacompose.domain.Track
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class SpotifyRepositoryImpl(private val context: Context) : SpotifyRepository {

    private companion object {
        private const val TAG = "SpotifyRepositoryImpl"

        private const val REDIRECT_URI = "com.midinatech.diplomacompose://callback"
        private const val CLIENT_ID = "817c51056fc7498eb73570d3018acd76"
        private const val CDN_URL = "https://i.scdn.co/image/"
    }

    private var spotifyAppRemote: SpotifyAppRemote? = null

    private val _trackFlow = MutableSharedFlow<Track>(
        replay = 1, // Replay the most recent value for new collectors
        extraBufferCapacity = 1 // Allow one uncollected emission
    )
    override val trackFlow: Flow<Track> get() = _trackFlow

    override fun connect() {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d(TAG, "Connected! Yay!")
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e(TAG, throwable.message, throwable)
            }
        })
    }

    override fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    private fun connected() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            Log.d(
                TAG,
                "Received: ${it.track.name} by ${it.track.artist} image; ${it.track.imageUri}"
            )

            val uri = it.track.imageUri.raw
            if (uri?.startsWith("spotify:image:") == true) {
                val imageId = uri.substringAfter("spotify:image:")
                _trackFlow.tryEmit(
                    Track(
                        title = it.track.name,
                        imageUri = "$CDN_URL$imageId"
                    )
                )
            }
        }
    }
}

//817c51056fc7498eb73570d3018acd76