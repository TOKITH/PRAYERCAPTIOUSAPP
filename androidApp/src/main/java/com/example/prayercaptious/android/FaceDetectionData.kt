package com.example.prayercaptious.android
class FaceDetectionData(
    var userID: Int? = 0, //main acitvity
    var prayerID: Int = 0, // face detection prayer id
    var timeStamp: String = "", //face detection conducted
    var faceDistance: Double = 0.0, //face detection face distance from camera
    var faceArea: Double = 0.0, //face detection face area
) {

}