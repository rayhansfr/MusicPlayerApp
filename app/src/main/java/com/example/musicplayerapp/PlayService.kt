package com.example.musicplayerapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore.Audio.Media
import android.widget.Toast

class PlayService : Service(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnInfoListener {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var link: String

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnErrorListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)
        mediaPlayer.setOnInfoListener(this)
        mediaPlayer.setOnBufferingUpdateListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("audiolink")) {
            link = intent.getStringExtra("audiolink").toString()
            mediaPlayer.reset()

            try {
                mediaPlayer.setDataSource(link)
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        val broadcastIntent = Intent("button.update")
        broadcastIntent.putExtra("update","changePlay")
        sendBroadcast(broadcastIntent)
        if (mp?.isPlaying!!) {
            mp.stop()

        }
        stopSelf()

    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        // Implement as needed
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ->
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK", Toast.LENGTH_SHORT).show()

            MediaPlayer.MEDIA_ERROR_SERVER_DIED ->
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show()

            MediaPlayer.MEDIA_ERROR_UNKNOWN ->
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        // Implement as needed
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        // Implement as needed
        return false
    }
}