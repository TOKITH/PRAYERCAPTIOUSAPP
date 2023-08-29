package com.example.prayercaptious.android

class User(
    var id: Int=0,
    var name: String="",
    var email: String?="",
    var pass: String?="",
    var height:Double = 0.00
){
    constructor(name:String,email:String,pass:String,height:Double) : this(0,name,email,pass,height) {
        this.name = name
        this.email = email
        this.pass = pass
        this.height = height
    }
    constructor(email:String): this(0,"",email,""){
        this.email = email
    }

    constructor(email:String, pass:String):this(0,"",email,pass){
        this.email = String()
        this.pass = String()
    }

}