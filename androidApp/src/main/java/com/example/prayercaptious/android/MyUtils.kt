package com.example.prayercaptious.android

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.widget.Toast
import java.io.IOException
import java.lang.IllegalStateException

object MyUtils {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun myDB(context:Context): SQLliteDB {
        return SQLliteDB(context)
    }

    class AudioUtils(val context: Context) {
        var mp:MediaPlayer = MediaPlayer()

        fun playRawAudio(resID:Int){
            val uri = Uri.parse("android.resource://${context.packageName}/${resID}")
            try {
                mp = MediaPlayer.create(context,uri)
                mp.start()
            } catch (e:IOException){
                e.stackTrace
            }
        }

        fun mpRelease(){
            mp.release()
        }

    }

}

