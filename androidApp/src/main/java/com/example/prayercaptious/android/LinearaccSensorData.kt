package com.example.prayercaptious.android

class LinearaccSensorData(
    var userID: Int? = 0, //main acitvity -> sensor
    var prayerID: Int = 0, //sensor
    var timeStamp: String = "", //sensor can be figured later
    var xLinAcc: Double = 0.0, //sensor
    var yLinAcc: Double = 0.0, //sensor
    var zLinAcc: Double = 0.0, //sensor
    var motion: String = "", //sensor
    var placement:String = "", //sensor
    var side:String = "", //sensor
    var elevation:String = "" //sensor
) {

}