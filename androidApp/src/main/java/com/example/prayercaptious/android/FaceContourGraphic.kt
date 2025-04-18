package com.example.prayercaptious.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.face.Face
import java.lang.Math.round

class FaceContourGraphic(overlay:GraphicOverlay): Graphic(overlay) {
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
    }

    private val boxPaint: Paint
    private var cameraDistance: Float = 0f
    private var left:Float = 0f
    private var right:Float = 0f
    private var bottom:Float = 0f
    private var top:Float = 0f
    private var faceArea: Float = 0f
    private var faceAreabyDistance:Float = 0f
    private var actual_width:Float = 15f
    private var focal_lense:Float = 1000f

    //The detected face instance
    private var face: Face?=null

    init {
        currentColorIndex = (currentColorIndex+1)% COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[currentColorIndex]

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
    override fun draw(canvas: Canvas) {

        // Translate the bounding box coordinates to fit the overlay view
        boundingBoxCoordinates()

        // Draw the bounding box on the canvas matching face coordinates
        foundFaceRectangleOverlay(canvas)

        // State face to camera distance information
        distanceBetweenFaceAndCamera(canvas)

        //State face area information
        faceArea(canvas)
    }
}