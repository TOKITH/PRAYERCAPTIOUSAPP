package com.example.prayercaptious.android

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SensorEventListener
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException

class MyUtils()
{
    private lateinit var context:Context
    private lateinit var applicationContext:Context
    private lateinit var bowInitialized: MediaPlayer
    private lateinit var bowVerified: MediaPlayer
    private lateinit var rukuperformed: MediaPlayer
    private lateinit var being_prayer_monitor: MediaPlayer
    private lateinit var prostrationInitialized: MediaPlayer
    private lateinit var rukuMissed: MediaPlayer
    var mp:MediaPlayer = MediaPlayer()
    fun init(context: Context, applicationContext:Context,
             resID1: Int,
             resID2: Int,
             resID3: Int,
             resID4: Int,
             resID5: Int,
             resID6: Int,) {
        this.context = context
        this.applicationContext = applicationContext

        bowInitialized = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID1}")
        )

        bowVerified = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID2}")
        )

        rukuperformed = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID3}")
        )

        being_prayer_monitor = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID4}")
        )

        prostrationInitialized = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID5}")
        )

        rukuMissed = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID6}")
        )

    }

    fun rukuMissed(){
        try {
            rukuMissed?.seekTo(0)
            rukuMissed?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prostrationInitializedAudio(){
        try {
            prostrationInitialized?.seekTo(0)
            prostrationInitialized?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun bowInitializedAudio(){
        try {
            bowInitialized?.seekTo(0)
            bowInitialized?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun bowVerifiedAudio(){
        try {
            bowVerified?.seekTo(0)
            bowVerified?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun rukuPerformedAudio(){
        try {
            rukuperformed?.seekTo(0)
            rukuperformed?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun beginPrayerAudio(){
        try {
            being_prayer_monitor.seekTo(0)
            being_prayer_monitor.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun mpPause(){
        pauseMediaPlayer(bowInitialized)
        pauseMediaPlayer(bowVerified)
        pauseMediaPlayer(rukuperformed)
    }

    private fun pauseMediaPlayer(mediaPlayer: MediaPlayer){
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }
    }

    fun mpRelease(){
        bowInitialized?.release()
        bowVerified?.release()
        rukuperformed?.release()
        prostrationInitialized?.release()
        rukuMissed?.release()
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun myDB(): SQLliteDB {
        return SQLliteDB(context)
    }

}

