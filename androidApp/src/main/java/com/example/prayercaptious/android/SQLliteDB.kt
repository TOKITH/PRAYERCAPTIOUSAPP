package com.example.prayercaptious.android

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLliteDB(
    context:Context,
    ): SQLiteOpenHelper(context, DATABASENAME,null, VERSION) {

    companion object{
        private val DATABASENAME:String = "paDB"
        private val VERSION:Int = 1

        // Tables
        private val TABLE_USER = "dim_user"
        private val TABLE_GYRO = "gyroscope"
        private val TABLE_LINACC = "linear_acceleration"

        // user table columns
        private val COL_USER_ID = "user_id"
        private val COL_NAME = "name"
        private val COL_EMAIL = "email"
        private val COL_PASSWORD = "password"

        // gyro/lin acc table columns
        private val COL_PRAYER_ID = "prayer_id"
        private val COL_TIMESTAMP = "event_time"
        private val COL_MOTION = "motion"
        private val COL_X_GYRO = "x_gyro"
        private val COL_Y_GYRO = "y_gyro"
        private val COL_Z_GYRO = "z_gyro"
        private val COL_X_LINACC = "x_linacc"
        private val COL_Y_LINACC = "y_linacc"
        private val COL_Z_LINACC = "z_linacc"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = ("CREATE TABLE IF NOT EXISTS "+ TABLE_USER+ " ("
                + COL_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME+" VARCHAR(255) NOT NULL,"
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
                + " FOREIGN KEY ($COL_USER_ID)"
                + " REFERENCES $TABLE_USER ($COL_USER_ID) ON UPDATE CASCADE ON DELETE CASCADE"
                +");"
                )

        //Creating user table :)
        db?.execSQL(createUserTable)
        db?.execSQL(createGyroscopeTable)
        db?.execSQL(createLinAccTable)

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

    fun getPrayerID(user:User):Int{
        val db = this.readableDatabase
        val gyroPrayerIDQuery = "SELECT MAX($COL_PRAYER_ID) AS $COL_PRAYER_ID FROM $TABLE_GYRO WHERE $COL_USER_ID = ${user.id}"
        val cursor:Cursor = db.rawQuery(gyroPrayerIDQuery,null)

        //if there are no prayer id associated with the user
        var prayerID= 0

        //get the maximum prayer id associated with the user
        if (cursor.moveToFirst()){
            prayerID = cursor.getInt(cursor.getColumnIndex(COL_PRAYER_ID).toInt())
        }

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


        val result = db.insert(TABLE_GYRO,null, cv)
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


        val result = db.insert(TABLE_LINACC,null, cv)
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

}