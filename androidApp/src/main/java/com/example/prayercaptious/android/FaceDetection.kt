package com.example.prayercaptious.android

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector

class FaceDetection(
    private val activity: MainActivity,
    private val faceDetector: FaceDetector,
    private val onFacesDetected: (faces: List<Face>) -> Unit,
    private val onFaceCropped: (face: Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var isAnalysisActive = true


    override fun analyze(imageProxy: ImageProxy) {
        // If analysis is not active, close the current image
        if (!isAnalysisActive) {
            imageProxy.close()
            return
        }

        // Check if the image format is RGBA_8888
        if (imageProxy.format == PixelFormat.RGBA_8888) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
            val bitmapBuffer = imageProxy.planes[0].buffer
            // Convert buffer to Bitmap
            val bitmap =
                Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(bitmapBuffer)

            // Rotate and resize the Bitmap
            val rotatedBitmap = bitmap?.let { rotateBitmap(it, rotationDegrees) }
            val resizedBitmap = rotatedBitmap?.let { resizePreviewImage(it) }

            // Process the image for face detection
            processImage(resizedBitmap, imageProxy)
        } else {
            imageProxy.close()
        }
    }

    private fun processImage(bitmap: Bitmap?, imageProxy: ImageProxy) {
        // Convert Bitmap to InputImage
        val image = bitmap?.let { InputImage.fromBitmap(it, 0) }
        if (image != null) {
            activity.bindingcameraview.graphicOverlay.setCameraInfo(
                image.width,
                image.height,
                CameraCharacteristics.LENS_FACING_FRONT
            )
        }

        // Process the image using ML Kit's Face Detector
        if (image != null) {
            this.faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    handleFaceDetectionSuccess(faces, bitmap)
                }
                .addOnFailureListener {
                    // Handle any errors here
                }
                .addOnCompleteListener {
                    // When done, close the image
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun handleFaceDetectionSuccess(
        faces: List<Face>,
        bitmapImage: Bitmap
    ) {
        if (faces.isNotEmpty()) {
            onFacesDetected(faces)
        }

        // Process each detected face
        faces.forEach { face ->
            try {
                val faceBitmap = cropFaceBitmap(bitmapImage, face.boundingBox)
                faceBitmap?.let { onFaceCropped(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error: $e")
            }
        }
    }

    private fun cropFaceBitmap(bitmapImage: Bitmap, boundingBox: Rect): Bitmap? {
        // Ensure the bounding box doesn't exceed the bitmap's boundaries
        val left = boundingBox.left.coerceAtLeast(0)
        val top = boundingBox.top.coerceAtLeast(0)
        val width = boundingBox.width().coerceAtLeast(bitmapImage.width - left)
        val height = boundingBox.height().coerceAtLeast(bitmapImage.height - top)

        return bitmapImage.let {
            Bitmap.createBitmap(
                it,
                left,
                top,
                width,
                height
            )
        }
    }

    private fun resizePreviewImage(previewImage: Bitmap): Bitmap {
        // Get the dimensions of the view
        val targetedSize: Pair<Int, Int> = activity.getTargetedWidthHeight()

        val targetWidth = targetedSize.first
        val maxHeight = targetedSize.second

        // Determine how much to scale down the image
        val scaleFactor: Float = Math.max(
            previewImage.width.toFloat() / targetWidth.toFloat(),
            previewImage.height.toFloat() / maxHeight.toFloat()
        )

        return Bitmap.createScaledBitmap(
            previewImage,
            (previewImage.width / scaleFactor).toInt(),
            (previewImage.height / scaleFactor).toInt(),
            true
        )
    }

    // Start the analysis
    fun start() {
        isAnalysisActive = true
    }

    // Stop the analysis
    fun stop() {
        isAnalysisActive = false
    }

    companion object {
        const val TAG = "FaceDetector"
    }
}