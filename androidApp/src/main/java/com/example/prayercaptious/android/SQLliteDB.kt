package com.example.prayercaptious.android

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import java.io.File


class SQLliteDB(
    context:Context,
    ): SQLiteOpenHelper(context, getDatabaseFilePath(context),null, VERSION) {

    companion object{
        private val DATABASENAME:String = "paDB"
        private val VERSION:Int = 1
        // Function to get the custom database file path
        private fun getDatabaseFilePath(context: Context): String {
            val documentsDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            return File(documentsDirectory, DATABASENAME).absolutePath
        }

        // Tables
        private const val TABLE_USER = "dim_user"
        private const val TABLE_GYRO = "gyroscope"
        private const val TABLE_LINACC = "linear_acceleration"
        private const val TABLE_FACE_DETECTION = "face_detection"

        // Universal columns for the project
        private const val COL_PRAYER_ID = "prayer_id"
        private const val COL_TIMESTAMP = "event_time"


        // user table columns
        private const val COL_USER_ID = "user_id"
        private const val COL_NAME = "name"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_HEIGHT = "height"

        // gyro/lin acc table columns
        private const val COL_X_GYRO = "x_gyro"
        private const val COL_Y_GYRO = "y_gyro"
        private const val COL_Z_GYRO = "z_gyro"
        private const val COL_X_LINACC = "x_linacc"
        private const val COL_Y_LINACC = "y_linacc"
        private const val COL_Z_LINACC = "z_linacc"
        private const val COL_MOTION = "motion"
        private const val COL_PLACEMENT = "placement"
        private const val COL_SIDE = "side"
        private const val COL_ELEVATION = "elevation"

        //Face detection table columns
        private const val COL_FACE_DISTANCE = "face_distance"
        private const val COL_FACE_AREA = "face_area"



    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE IF NOT EXISTS "+ TABLE_USER+ " ("
                + COL_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME+" VARCHAR(255) NOT NULL,"
                + COL_HEIGHT+" VARCHAR(255) NOT NULL,"
                + COL_EMAIL+" VARCHAR(255) NOT NULL,"
                + COL_PASSWORD+" VARCHAR(255) NOT NULL"
                +");"
                )
        val createGyroscopeTable = ("CREATE TABLE IF NOT EXISTS "+ TABLE_GYRO+" ("
                + COL_USER_ID+" INTEGER,"
                + COL_PRAYER_ID+" INTEGER,"
                + COL_TIMESTAMP+" TEXT,"
                + COL_X_GYRO+" DOUBLE,"
                + COL_Y_GYRO+" DOUBLE,"
                + COL_Z_GYRO+" DOUBLE,"
                + COL_MOTION+" VARCHAR(10),"
                + COL_PLACEMENT+" VARCHAR(25),"
                + COL_SIDE+" VARCHAR(10),"
                + COL_ELEVATION+" VARCHAR(25),"
                + " FOREIGN KEY ($COL_USER_ID)"
                + " REFERENCES $TABLE_USER ($COL_USER_ID) ON UPDATE CASCADE ON DELETE CASCADE"
                + ");"
                )

        val createLinAccTable = ("CREATE TABLE IF NOT EXISTS "+ TABLE_LINACC+" ("
                + COL_USER_ID+" INTEGER,"
                + COL_PRAYER_ID+" INTEGER,"
                + COL_TIMESTAMP+" TEXT,"
                + COL_X_LINACC+" DOUBLE,"
                + COL_Y_LINACC+" DOUBLE,"
                + COL_Z_LINACC+" DOUBLE,"
                + COL_MOTION+" VARCHAR(10),"
                + COL_PLACEMENT+" VARCHAR(25),"
                + COL_SIDE+" VARCHAR(10),"
                + COL_ELEVATION+" VARCHAR(25),"
                + " FOREIGN KEY ($COL_USER_ID)"
                + " REFERENCES $TABLE_USER ($COL_USER_ID) ON UPDATE CASCADE ON DELETE CASCADE"
                +");"
                )

        val createFaceDetectionTable = ("CREATE TABLE IF NOT EXISTS "+ TABLE_FACE_DETECTION+" ("
                + COL_USER_ID+" INTEGER,"
                + COL_PRAYER_ID+" INTEGER,"
                + COL_TIMESTAMP+" TEXT,"
                + COL_FACE_DISTANCE+" DOUBLE,"
                + COL_FACE_AREA+" DOUBLE,"
                + " FOREIGN KEY ($COL_USER_ID)"
                + " REFERENCES $TABLE_USER ($COL_USER_ID) ON UPDATE CASCADE ON DELETE CASCADE"
                +");"
                )

        //Creating user table :)
        db?.execSQL(createUserTable)
        db?.execSQL(createGyroscopeTable)
        db?.execSQL(createLinAccTable)
        db?.execSQL(createFaceDetectionTable)

        //Enabling foreign key support
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

    //Inserting data into DB
    fun insertRegistrationUserData(user:User):Boolean{
        val db = this.writableDatabase
//        val query = "INSERT INTO $TABLE_USER ($COL_NAME,$COL_EMAIL,$COL_PASSWORD) VALUES('${user.name}','${user.email}','${user.pass}');"
        val cv = ContentValues()
        cv.put(COL_NAME, user.name)
        cv.put(COL_EMAIL, user.email)
        cv.put(COL_HEIGHT,user.height)
        cv.put(COL_PASSWORD, user.pass)

        val result = db.insert(TABLE_USER,null, cv)
        db.close()
        // if result is -1 than some error has occured
        return result == (-1).toLong()
    }

    fun readRegistrationUserData(): MutableList<User>{

        var UserDataList:MutableList<User> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM "+ TABLE_USER
        val cursor:Cursor = db.rawQuery(query,null)

        if (cursor.moveToFirst()){

            do{
                val user: User = User()
                user.id = cursor.getString(cursor.getColumnIndex(COL_USER_ID).toInt()).toInt()
                user.name = cursor.getString(cursor.getColumnIndex(COL_NAME).toInt())
                user.email = cursor.getString(cursor.getColumnIndex(COL_EMAIL).toInt())
                user.pass = cursor.getString(cursor.getColumnIndex(COL_PASSWORD).toInt())
                user.height = cursor.getString(cursor.getColumnIndex(COL_HEIGHT).toInt()).toDouble()
                UserDataList.add(user)
            }while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return UserDataList
    }
    fun deleteRegistrationUserData(){
        val db = this.writableDatabase
        db.delete(TABLE_USER,null,null)
        db.close()
    }

    fun verifyExistingUser(email:String):Boolean {
        val db = this.readableDatabase
        val existingEmail = "SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = '$email' "
        val cursor:Cursor = db.rawQuery(existingEmail,null)
        val existing = cursor.moveToFirst()

        cursor.close()
        db.close()

        // moveToFirst = true means existing user as there are data to move to
        // moveToFirst = false means non existing user
        return existing
    }

    fun login_details(user: User): User{
        val db = this.readableDatabase
        val entered_email = user.email
        val existingEmail = "SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = '$entered_email' "
        val cursor:Cursor = db.rawQuery(existingEmail,null)

        //set values to null unless there are values
        user.email = null
        user.pass = null
        if (cursor.moveToFirst()) {
            user.id = cursor.getInt(cursor.getColumnIndex(COL_USER_ID).toInt())
            user.email = cursor.getString(cursor.getColumnIndex(COL_EMAIL).toInt())
            user.pass = cursor.getString(cursor.getColumnIndex(COL_PASSWORD).toInt())
            user.name = cursor.getString(cursor.getColumnIndex(COL_NAME).toInt())
        }
        db.close()
        cursor.close()
        return user
    }

    fun getPrayerIDSensors(user:User):Int{
        val db = this.readableDatabase
        val gyroPrayerIDQuery = "SELECT MAX($COL_PRAYER_ID) AS $COL_PRAYER_ID FROM $TABLE_GYRO WHERE $COL_USER_ID = ${user.id}"
        val cursor:Cursor = db.rawQuery(gyroPrayerIDQuery,null)

        //if there are no prayer id associated with the user
        var prayerID= 0

        //get the maximum prayer id associated with the user
        if (cursor.moveToFirst()){
            prayerID = cursor.getInt(cursor.getColumnIndex(COL_PRAYER_ID).toInt())
        }
        cursor.close()
        db.close()
        return prayerID
    }

    fun getPrayerIDFaceDetection(user:User):Int{
        val db = this.readableDatabase
        val fdPrayerIDQuery = "SELECT MAX($COL_PRAYER_ID) AS $COL_PRAYER_ID FROM $TABLE_FACE_DETECTION WHERE $COL_USER_ID = ${user.id}"
        val cursor:Cursor = db.rawQuery(fdPrayerIDQuery,null)

        //if there are no prayer id associated with the user
        var prayerID= 0

        //get the maximum prayer id associated with the user
        if (cursor.moveToFirst()){
            prayerID = cursor.getInt(cursor.getColumnIndex(COL_PRAYER_ID).toInt())
        }
        cursor.close()
        db.close()
        return prayerID
    }

    fun insertGyroData(gyroData: GyroSensorData):Boolean{
        val db = this.writableDatabase
//        val query = "INSERT INTO $TABLE_USER ($COL_NAME,$COL_EMAIL,$COL_PASSWORD) VALUES('${user.name}','${user.email}','${user.pass}');"
        val cv = ContentValues()
        cv.put(COL_USER_ID,gyroData.userID)
        cv.put(COL_PRAYER_ID, gyroData.prayerID)
        cv.put(COL_TIMESTAMP, gyroData.timeStamp)
        cv.put(COL_X_GYRO, gyroData.xGyro)
        cv.put(COL_Y_GYRO, gyroData.yGyro)
        cv.put(COL_Z_GYRO, gyroData.zGyro)
        cv.put(COL_MOTION, gyroData.motion)
        cv.put(COL_PLACEMENT,gyroData.placement)
        cv.put(COL_SIDE,gyroData.side)
        cv.put(COL_ELEVATION,gyroData.elevation)


        val result = db.insert(TABLE_GYRO,null, cv)
        db.close()
        // if result is -1 than some error has occured
        return result == (-1).toLong()
    }

    fun insertLinAccData(linaccData: LinearaccSensorData):Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_USER_ID,linaccData.userID)
        cv.put(COL_PRAYER_ID, linaccData.prayerID)
        cv.put(COL_TIMESTAMP, linaccData.timeStamp)
        cv.put(COL_X_LINACC, linaccData.xLinAcc)
        cv.put(COL_Y_LINACC, linaccData.yLinAcc)
        cv.put(COL_Z_LINACC, linaccData.zLinAcc)
        cv.put(COL_MOTION, linaccData.motion)
        cv.put(COL_PLACEMENT,linaccData.placement)
        cv.put(COL_SIDE,linaccData.side)
        cv.put(COL_ELEVATION,linaccData.elevation)


        val result = db.insert(TABLE_LINACC,null, cv)
        db.close()
        // if result is -1 than some error has occured
        return result == (-1).toLong()
    }


    fun insertFaceDetectionData(faceDetectionData: FaceDetectionData):Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_USER_ID,faceDetectionData.userID)
        cv.put(COL_PRAYER_ID, faceDetectionData.prayerID)
        cv.put(COL_TIMESTAMP, faceDetectionData.timeStamp)
        cv.put(COL_FACE_DISTANCE, faceDetectionData.faceDistance)
        cv.put(COL_FACE_AREA, faceDetectionData.faceArea)


        val result = db.insert(TABLE_FACE_DETECTION,null, cv)
        db.close()
        // if result is -1 than some error has occured
        return result == (-1).toLong()
    }
    fun foreign_enabled(){
        val db = this.readableDatabase
        val cursor = db.rawQuery("PRAGMA foreign_keys;", null)

        if (cursor.moveToFirst()) {
            val foreignKeyStatus = cursor.getInt(0) // Index 0 corresponds to the result of PRAGMA foreign_keys
            if (foreignKeyStatus == 1) {
                Log.d("myTag","Foreign key constraints are enabled")
            } else {
                // Foreign key constraints are disabled
                Log.d("myTag","Foreign key constraints are disabled.")
            }
        }

        cursor.close()
        db.close()
    }

    fun deleteCurrentDataCollected(userid:Int,prayerid:Int){
        val db = this.writableDatabase
//        val deleteCurrentGyroPrayerID = "DELETE FROM $TABLE_GYRO WHERE $COL_USER_ID = $userid AND $COL_PRAYER_ID = $prayerid;"
//        val deleteCurrentLinearAccPrayerID = "DELETE FROM $TABLE_LINACC WHERE $COL_USER_ID = $userid AND $COL_PRAYER_ID = $prayerid;"
        val deleteCurrentGyroPrayerID = "DELETE FROM $TABLE_GYRO WHERE $COL_USER_ID = $userid AND $COL_PRAYER_ID = (SELECT MAX($COL_PRAYER_ID) FROM $TABLE_GYRO WHERE $COL_USER_ID = $userid);"
        val deleteCurrentLinearAccPrayerID = "DELETE FROM $TABLE_LINACC WHERE $COL_USER_ID = $userid AND $COL_PRAYER_ID = (SELECT MAX($COL_PRAYER_ID) FROM $TABLE_LINACC WHERE $COL_USER_ID = $userid);"
        val deleteCurrentFDTable = "DELETE FROM $TABLE_FACE_DETECTION WHERE $COL_USER_ID = $userid;"
        val result_gyro = db?.execSQL(deleteCurrentGyroPrayerID)
        val result_linacc = db?.execSQL(deleteCurrentLinearAccPrayerID)
        val result_fd = db?.execSQL(deleteCurrentFDTable)

        Log.d("DBTag","DeleteResultGyro $result_gyro\nDeleteResultLinAcc $result_linacc\nDeleteResultFD $result_fd")
        db.close()


    }

}