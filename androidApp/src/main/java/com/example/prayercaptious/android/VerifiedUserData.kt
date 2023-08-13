package com.example.prayercaptious.android

class User(
    var id: Int=0,
    var name: String="",
    var email: String?="",
    var pass: String?=""
){
    constructor(name:String,email:String,pass:String) : this(0,name,email,pass) {
        this.name = name
        this.email = email
        this.pass = pass
    }
    constructor(email:String): this(0,"",email,""){
        this.email = email
    }

    constructor(email:String, pass:String):this(0,"",email,pass){
        this.email = String()
        this.pass = String()
    }

}