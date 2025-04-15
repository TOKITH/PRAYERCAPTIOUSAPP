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
    private lateinit var bowHalfComplete: MediaPlayer
    private lateinit var prostrationOneInitialized: MediaPlayer
    private lateinit var prostrationOnePerformed: MediaPlayer
    private lateinit var prostrationTwoInitialized: MediaPlayer
    private lateinit var prostrationTwoPerformed: MediaPlayer
    private lateinit var prostrationExcess: MediaPlayer
    private lateinit var prostrationCompletelyMissed: MediaPlayer
    private lateinit var prostrationMissedOne: MediaPlayer
    private lateinit var prayerComplete: MediaPlayer
    private lateinit var prayingExcess: MediaPlayer
    var mp:MediaPlayer = MediaPlayer()
    fun init(context: Context, applicationContext:Context,
             resID1: Int,
             resID2: Int,
             resID3: Int,
             resID4: Int,
             resID5: Int,
             resID6: Int,
             resID7: Int,
             resID8: Int,
             resID9: Int,
             resID10: Int,
             resID11: Int,
             resID12: Int,
             resID13: Int,
             resID14: Int,
             resID15: Int,
             resID16: Int,) {
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

        bowHalfComplete = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID7}")
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

        prostrationOneInitialized = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID8}")
        )

        prostrationOnePerformed = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID9}")
        )

        prostrationTwoInitialized = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID10}")
        )

        prostrationTwoPerformed = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID11}")
        )

        prostrationExcess = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID12}")
        )

        prostrationCompletelyMissed = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID13}")
        )

        prostrationMissedOne = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID14}")
        )

        prayerComplete = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID15}")
        )

        prayingExcess = MediaPlayer.create(
            applicationContext,
            Uri.parse("android.resource://${context.packageName}/${resID16}")
        )

    }
    fun prayingExcess(){
        try {
            prayingExcess?.seekTo(0)
            prayingExcess?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prayerComplete(){
        try {
            prayerComplete?.seekTo(0)
            prayerComplete?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun prostrationMissedOne(){
        try {
            prostrationMissedOne?.seekTo(0)
            prostrationMissedOne?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prostrationCompletelyMissed(){
        try {
            prostrationCompletelyMissed?.seekTo(0)
            prostrationCompletelyMissed?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prostrationExcess(){
        try {
            prostrationExcess?.seekTo(0)
            prostrationExcess?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun prostrationTwoPerformed(){
        try {
            prostrationTwoPerformed?.seekTo(0)
            prostrationTwoPerformed?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prostrationTwoInitialized(){
        try {
            prostrationTwoInitialized?.seekTo(0)
            prostrationTwoInitialized?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prostrationOnePerformed(){
        try {
            prostrationOnePerformed?.seekTo(0)
            prostrationOnePerformed?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun prostrationOneInitialized(){
        try {
            prostrationOneInitialized?.seekTo(0)
            prostrationOneInitialized?.start()
        } catch (e:IOException){
            e.printStackTrace()
        }
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

    fun bowHalfCompleteAudio(){
        try {
            bowHalfComplete?.seekTo(0)
            bowHalfComplete?.start()
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
        bowHalfComplete?.release()
        prostrationOneInitialized?.release()
        prostrationOnePerformed?.release()
        prostrationTwoInitialized?.release()
        prostrationTwoPerformed?.release()
        prostrationExcess?.release()
        prostrationCompletelyMissed?.release()
        prostrationMissedOne?.release()
        prayerComplete?.release()
        prayingExcess?.release()



    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun myDB(): SQLliteDB {
        return SQLliteDB(context)
    }

}

