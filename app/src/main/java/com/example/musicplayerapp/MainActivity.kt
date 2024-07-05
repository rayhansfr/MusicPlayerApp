package com.example.musicplayerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var playStopButton :ImageView
    var audioLink = "https://drive.google.com/uc?export=download&id=1iokpmife9mAqWhR8Leck49R1NWKn93LP"
    var musicPlaying : Boolean =  false
    lateinit var serviceIntent : Intent

    lateinit var sharedPreferences: SharedPreferences
    val buttonReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.getStringExtra("update")
            if (action=="changePlay"){
                playStopButton.setImageResource(R.drawable.play_button)
                musicPlaying = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(buttonReceiver, IntentFilter("button.update"), RECEIVER_EXPORTED)
        }

        musicPlaying = true


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playStopButton = findViewById(R.id.PlayStopButton)
        playStopButton.setImageResource(R.drawable.stop_button)
        serviceIntent = Intent(this, PlayService::class.java)

        playAudio()
        playStopButton.setOnClickListener {
            musicPlaying = if (!musicPlaying) {
                playAudio()
                playStopButton.setImageResource(R.drawable.stop_button)
                true
            } else{
                stopPlayService()
                playStopButton.setImageResource(R.drawable.play_button)
                false
            }
        }
    }

    fun playAudio() {
        serviceIntent.putExtra("audiolink", audioLink)

        try {
            startService(serviceIntent)
        }
        catch (e : SecurityException) {
            Toast.makeText(this, "Erroor : " + e.message,Toast.LENGTH_SHORT).show()
        }
    }

    fun stopPlayService() {
        try {
            stopService(serviceIntent)
        }
        catch (e : SecurityException){
            Toast.makeText(this,"Error : " + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}