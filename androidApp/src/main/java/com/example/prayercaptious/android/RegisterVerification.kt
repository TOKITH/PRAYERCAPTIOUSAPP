package com.example.prayercaptious.android


import android.widget.EditText
import android.content.Context
import android.service.autofill.UserData
import android.util.Log
import android.util.Patterns
import java.text.DecimalFormat


class VerifyRegistratoin(
    var reg_name: EditText,
    var reg_email: EditText,
    var reg_pass: EditText,
    var reg_confirm_pass: EditText,
    var reg_height:EditText,
    var MyUtils:MyUtils
)
{
//    private val MyUtils:MyUtils = MyUtils()
    fun verify_blank(): Boolean {
        if (!(reg_name.text.isNotEmpty()
                    && reg_email.text.isNotEmpty()
                    && reg_pass.text.isNotEmpty()
                    && reg_confirm_pass.text.isNotEmpty())
                    && reg_height.text.isNotEmpty()){
            MyUtils.showToast("Please do not leave any form blank")
        }

        return (reg_name.text.isNotEmpty()
                && reg_email.text.isNotEmpty()
                && reg_pass.text.isNotEmpty()
                && reg_confirm_pass.text.isNotEmpty()
                && reg_height.text.isNotEmpty())
    }

    fun verify_name(): Boolean{
        val name = reg_name.text.toString().trim()
        // useful function to see printed stuff in logcat
        Log.d("myTag",name)
        if (!(name.length in 2..12 && name.matches(Regex("^[a-zA-Z]+$")))) {
            MyUtils.showToast("Name size should be between 2 to 12 letter with letters only with no spaces")
        }

        return (name.length in 2..12
                && name.matches(Regex("^[a-zA-Z]+$")))

    }

    fun verify_email(): Boolean{
        val email = reg_email.text.toString().trim()
        Log.d("myTag",email)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            MyUtils.showToast("Please enter a valid email address")
        }

        return (Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun verify_height(): Boolean{
        val height = reg_height.text.toString()
        Log.d("myTag",height)
        //1 digit and between 1 to 2 decimal place
        val pattern = Regex("""^\d\.\d{1,2}$""")

        if (!pattern.matches(height)) {
            MyUtils.showToast("Enter height in ft.inches format")
        }

        return pattern.matches(height)
    }

    fun verify_password(): Boolean{
        val pass = reg_pass.text.toString().trim()
        val confrim_pass = reg_confirm_pass.text.toString().trim()
        // useful function to see printed stuff in logcat
        Log.d("myTag",("Password:${pass}  Confirm Passsword:${confrim_pass}"))

        val minLength = 8
        val hasUpperCase = Regex("[A-Z]").containsMatchIn(pass)
        val hasLowerCase = Regex("[a-z]").containsMatchIn(pass)
        val hasDigit = Regex("\\d").containsMatchIn(pass)

        if (!(pass.length >= minLength && hasUpperCase && hasLowerCase && hasDigit)){
            MyUtils.showToast("Password must have: mixed cases, 8 characters and digits")
        }
        if (pass!= confrim_pass){
            MyUtils.showToast("Passwords don't match :(")
        }

        return (pass.length >= minLength && hasUpperCase && hasLowerCase && hasDigit && pass == confrim_pass)
    }

    fun verify_existing_user(): Boolean {
        val email = reg_email.text.toString().trim().lowercase()
        val db = MyUtils.myDB()
        if (db.verifyExistingUser(email)){
            MyUtils.showToast("Your email already exist")
        }
        return !db.verifyExistingUser(email)
    }

    fun verified_user_data(): Boolean {
        val isDataValid= (this.verify_blank()
                        && this.verify_existing_user()
                        && this.verify_name()
                        && this.verify_email()
                        && this.verify_height()
                        && this.verify_password()
                )

        return (isDataValid)
    }

    fun clear_register(){
        reg_email.text.clear()
        reg_name.text.clear()
        reg_pass.text.clear()
        reg_confirm_pass.text.clear()
        reg_height.text.clear()
    }
}