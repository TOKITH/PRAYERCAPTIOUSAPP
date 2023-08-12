package com.example.prayercaptious.android

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf

class SQLliteDB(
    context:Context,
    ): SQLiteOpenHelper(context, DATABASENAME,null, VERSION) {

    companion object{
        private val DATABASENAME:String = "pcDB"
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
        val createUserTable = ("CREATE TABLE "+ TABLE_USER+ " ("
                + COL_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME+" VARCHAR(255) NOT NULL,"
                + COL_EMAIL+" VARCHAR(255) NOT NULL,"
                + COL_PASSWORD+" VARCHAR(255) NOT NULL"
                +");"
                )
        val createGyroscopeTable = ("CREATE TABLE "+ TABLE_GYRO+" ("
                + COL_USER_ID+"INTEGER,"
                + COL_PRAYER_ID+"INTEGER,"
                + COL_TIMESTAMP+"TEXT,"
                + COL_X_GYRO+"REAL,"
                + COL_Y_GYRO+"REAL,"
                + COL_Z_GYRO+"REAL,"
                + COL_MOTION+"VARCHAR(10)"
                +");"
                )

        val createLinAccTable = ("CREATE TABLE "+ TABLE_LINACC+" ("
                + COL_USER_ID+"INTEGER,"
                + COL_PRAYER_ID+"INTEGER,"
                + COL_TIMESTAMP+"TEXT,"
                + COL_X_LINACC+"REAL,"
                + COL_Y_LINACC+"REAL,"
                + COL_Z_LINACC+"REAL,"
                + COL_MOTION+"VARCHAR(10)"
                +");"
                )
        //Creating user table :)
        db?.execSQL(createUserTable)
//        db?.execSQL(createGyroscopeTable)
//        db?.execSQL(createLinAccTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    //Inserting data into DB
    fun insertRegistrationUserData(user:User):Boolean{
        val db = this.writableDatabase
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

        cursor.close()
        db.close()

        // moveToFirst = true means existing user as there are data to move to
        // moveToFirst = false means non existing user
        return cursor.moveToFirst()
    }

    fun login_details(email: String): MutableList<User>{
        val db = this.readableDatabase
        var userLoginDetails:MutableList<User> = arrayListOf()
        val existingEmail = "SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = '$email' "
        val cursor:Cursor = db.rawQuery(existingEmail,null)

        if (cursor.moveToFirst()){
            val user:User = User()
            user.email = cursor.getString(cursor.getColumnIndex(COL_EMAIL).toInt())
            user.pass = cursor.getString(cursor.getColumnIndex(COL_PASSWORD).toInt())
            userLoginDetails.add(user)
        }
        db.close()
        return userLoginDetails
    }

}