package com.example.prayercaptious.android


import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Context
import android.util.Log
import android.util.Patterns
import com.example.prayercaptious.android.databinding.RegistrationLoginBinding
import com.example.prayercaptious.android.databinding.RegistrationPageBinding


class verifyregistratoin(
    var reg_name: EditText,
    var reg_email: EditText,
    var reg_pass: EditText,
    var reg_confirm_pass: EditText,
    )
{
    fun verify_blank(context:Context): Boolean {
        if (!(reg_name.text.isNotEmpty()
                && reg_email.text.isNotEmpty()
                && reg_pass.text.isNotEmpty()
                && reg_confirm_pass.text.isNotEmpty())){
            MyUtils.showToast(context,"Please do not leave any form blank")
        }

       return (reg_name.text.isNotEmpty()
               && reg_email.text.isNotEmpty()
               && reg_pass.text.isNotEmpty()
               && reg_confirm_pass.text.isNotEmpty())
    }

    fun verify_name(context: Context): Boolean{
        var name = reg_name.text.toString().trim()
        // useful function to see printed stuff in logcat
        Log.d("myTag",name)
        if (!(name.length in 2..12 && name.matches(Regex("^[a-zA-Z]+$")))) {
            MyUtils.showToast(context,"Name size should be between 2 to 12 letter with letters only with no spaces")
        }

        return (name.length in 2..12
            && name.matches(Regex("^[a-zA-Z]+$")))

    }

    fun verify_email(context: Context): Boolean{
        var email = reg_email.text.toString().trim()
        Log.d("myTag",email)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            MyUtils.showToast(context,"Please enter a valid email address")
        }

        return (Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun verify_password(context: Context): Boolean{
        var pass = reg_pass.text.toString().trim()
        var confrim_pass = reg_confirm_pass.text.toString().trim()
        // useful function to see printed stuff in logcat
        Log.d("myTag",("Password:${pass}  Confirm Passsword:${confrim_pass}"))

        val minLength = 8
        val hasUpperCase = Regex("[A-Z]").containsMatchIn(pass)
        val hasLowerCase = Regex("[a-z]").containsMatchIn(pass)
        val hasDigit = Regex("\\d").containsMatchIn(pass)

        if (!(pass.length >= minLength && hasUpperCase && hasLowerCase && hasDigit)){
            MyUtils.showToast(context,"Password must have: mixed cases, 8 characters and digits")
        }
        if (pass!= confrim_pass){
            MyUtils.showToast(context,"Passwords don't match :(")
        }

        return (pass.length >= minLength && hasUpperCase && hasLowerCase && hasDigit && pass == confrim_pass)
    }

    fun verify_existing_user(){
        TODO("Check from DB if the user email exist to verify existing user")
    }
}