package com.example.prayercaptious.android

class GyroSensorData(
    var userID: Int? = 0, //main acitvity -> sensor
    var prayerID: Int = 0, //sensor
    var timeStamp: String = "", //sensor can be figured later
    var xGyro: Double = 0.0, //sensor
    var yGyro: Double = 0.0, //sensor
    var zGyro: Double = 0.0, //sensor
    var motion: String = "", //sensor
    var placement:String = "", //sensor
    var side:String = "", //sensor
    var elevation:String = "" //sensor

) {

}