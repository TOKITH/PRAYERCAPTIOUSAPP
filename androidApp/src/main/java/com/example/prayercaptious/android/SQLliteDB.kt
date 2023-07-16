package com.example.prayercaptious.android

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//val databaseName:String = "/Users/tokih/AndroidStudioProjects/PrayerCaptious/androidApp/src/main/SQLiteDB/sensordata.db"

class SQLliteDB(
    context:Context,
    ): SQLiteOpenHelper(context, DATABASENAME,null, VERSION) {

    companion object{
        private val DATABASENAME:String = "/Users/tokih/AndroidStudioProjects/PrayerCaptious/androidApp/src/main/SQLiteDB/sensordata.db"
        private val VERSION:Int = 1
        private val TABLE_GYRO = "gyroscope_table"
        private val ID = "id"
        private val NAME = "name"
        private val X_GYRO = "x_gyro"
        private val Y_GYRO = "y_gyro"
        private val Z_GYRO = "z_gyro"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createSensorTable = ("CREATE TABLE "+ TABLE_GYRO+ "("
                +ID+"INTEGER PRIMARY KEY, "
                )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}