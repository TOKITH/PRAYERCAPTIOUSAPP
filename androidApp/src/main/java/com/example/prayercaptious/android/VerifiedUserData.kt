package com.example.prayercaptious.android

class User(
    var id: Int=0,
    var name: String="",
    var email: String="",
    var pass: String=""
) {
    constructor(name:String,email:String,pass:String) : this() {
        this.name = name
        this.email = email
        this.pass = pass
    }

}