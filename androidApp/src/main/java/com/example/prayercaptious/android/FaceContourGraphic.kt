package com.example.prayercaptious.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.face.Face
import java.lang.Math.round
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class FaceContourGraphic(overlay: GraphicOverlay, db: SQLliteDB, user: User,prayerID:Int,myUtils: MyUtils): Graphic(overlay) {

    companion object {
        private const val BOX_STROKE_WIDTH = 5.0f

        private val COLOR_CHOICES = arrayOf(
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
        )

        private var currentColorIndex:Int = 0
        private var qiam_init:Boolean = false
    }

    private val boxPaint: Paint
    private val userid:Int
    private val prayerid:Int = prayerID
    private var cameraDistance: Float = 0f
    private var left:Float = 0f
    private var right:Float = 0f
    private var bottom:Float = 0f
    private var top:Float = 0f
    private var faceArea: Float = 0f
    private var faceAreabyDistance:Float = 0f
    private var actual_width:Float = 15f
    private var focal_lense:Float = 1000f

    private var timestamps:String = ""

    //The detected face instance
    private var face: Face?=null

    private val userData:User = user
    private val db:SQLliteDB = db
    private var faceDetectionData = FaceDetectionData()
    private var myUtils:MyUtils = myUtils

    //Prayer algo vars
    private var time = 0


    init {
        currentColorIndex = (currentColorIndex+1)% COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[currentColorIndex]

        //db inits
        userid = db.login_details(user).id

        //Paint for drawing the bounding box of the detected face
        boxPaint = Paint().apply{
            color = selectedColor
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
            textSize = 100.0f
        }
    }

    //Updates the face instance from the latest detection
    fun updatedFace(face: Face){
        this.face= face
        postInvalidate()
    }

    fun boundingBoxCoordinates(){
        val face = this.face?: return
        // Translate the bounding box coordinates to fit the overlay view
        left = translateX(face.boundingBox.left.toFloat())
        top = translateY(face.boundingBox.top.toFloat())
        right = translateX(face.boundingBox.right.toFloat())
        bottom = translateY(face.boundingBox.bottom.toFloat())
    }
    fun faceArea(canvas: Canvas){
        faceArea = (top*right)/1000
        faceArea = round(faceArea.toDouble()).toFloat()
        // State face area :: formula (base*height)
        canvas.drawText("FA: "+faceArea.toString()+"cm^2",right,top-200,boxPaint)
    }
    fun distanceBetweenFaceAndCamera(canvas:Canvas){
        //Camera to face distance formula
        //(actual_width * focal_length) / face_width
        /*
        actual width = width of face (can set a value like 15)
        focal length = focal length of camera in pixels (camera forcal length)
        face width = camera detected width
         */

        cameraDistance = ((actual_width*focal_lense)/(left-right))
        cameraDistance = round(cameraDistance.toDouble()).toFloat()

        // State face to camera distance information in cm
        canvas.drawText("Distance: "+cameraDistance.toString()+"cm",right,top,boxPaint)
    }

    fun foundFaceRectangleOverlay(canvas: Canvas){
        canvas.drawRect(left,top,right,bottom,boxPaint)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStamp() {
        timestamps = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun draw(canvas: Canvas) {
        timeStamp()
        // Translate the bounding box coordinates to fit the overlay view
        boundingBoxCoordinates()

        // Draw the bounding box on the canvas matching face coordinates
        foundFaceRectangleOverlay(canvas)

        // State face to camera distance information
        distanceBetweenFaceAndCamera(canvas)

        //State face area information
        faceArea(canvas)

        faceDetectionData = FaceDetectionData(userid,prayerid,timestamps,cameraDistance.toDouble(),faceArea.toDouble())
        db.insertFaceDetectionData(faceDetectionData)


        //Prayer Algorithm stuff happens here
//        time+=1
//        Log.e("PrayerAlgo",time.toString())

       val prayerAlgorithmFD = PrayerAlgorithmFD(cameraDistance,faceArea,timestamps, myUtils)
        if(!qiam_init) {
            prayerAlgorithmFD.setQiamAndInitPrayerMovementMeasurement()
            qiam_init = true
        }
        prayerAlgorithmFD.RunningMotion()
        prayerAlgorithmFD.Testing()

        //Ensuring data is correctly registered
//    Log.e("init test",prayerid.toString())
//    Log.e("FaceDetectionDB",
//        userid.toString()+" "
//            +prayerid.toString()+" "
//            +faceDetectionData.timeStamp+" "
//            +faceDetectionData.faceDistance+" "
//            +faceDetectionData.faceArea)
    }
}