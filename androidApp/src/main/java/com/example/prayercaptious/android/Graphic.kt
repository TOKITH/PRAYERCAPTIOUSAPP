package com.example.prayercaptious.android

import android.graphics.Canvas
import android.hardware.camera2.CameraCharacteristics

abstract class Graphic(private val overlay: GraphicOverlay) {
    //Draws the graphic on the provided canvas
    abstract fun draw(canvas: Canvas)

    //Scales a horizontal value based on the overlay's width scale factor
    private fun scaleX(horizontal:Float): Float = horizontal*overlay.widthScaleFactor

    //Scales a vertical values based on the overlay's height scale factor
    private fun scaleY(vertical:Float): Float = vertical*overlay.heightScaleFactor

    // Translate the X coordinate based on the camera's orientation. If the camera is front facing
    //the X coordinate is mirrored
    fun translateX(x:Float):Float {
        return if (overlay.facing == CameraCharacteristics.LENS_FACING_FRONT){
            overlay.width - scaleX(x)
        } else {
            scaleX(x)
        }
    }

    // Translate the Y coordinate based on the overlay's scale factor
    fun translateY(y:Float): Float = scaleY(y)

    //Invalidate the overlay, causing it to redraw
    fun postInvalidate() {
        overlay.postInvalidate()
    }
}