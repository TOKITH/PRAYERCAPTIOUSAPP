package com.example.prayercaptious.android

import android.content.Context
import android.widget.EditText

class InsertDataDB(var db: SQLliteDB){

    fun insert_user_data(userData:MutableList<String>):Boolean{
        //[0] name , [1] email, [2] pass
        val result = db.insertRegistrationUserData(userData[0],userData[1],userData[2])
        return result
    }
}