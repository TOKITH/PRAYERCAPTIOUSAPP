package com.example.prayercaptious.android

import android.util.Log

class PrayerAlgorithmFD(var faceDistance:Float,
                        var faceArea:Float,
                        var timestamp: String,
                        var myUtils: MyUtils
                        ){

    companion object{
        var time:Int = 0
        var QiamHeight:Float = 0f
        var QiamArea:Float = 0f
        var MotionD:Float = 0f
        var MotionA:Float = 0f
        var QiamPerformed:Boolean = false
        var QiamD:Float = 0f
        var QiamHeightRangeUpper:Float = 0f
        var QiamHeightRangeLower = 0f
        var QiamA:Float = 0f
        var QiamAreaRangeUpper:Float = 0f
        var QiamAreaRangeLower = 0f
        var RukuInit = false
        var RukuPerformed:Boolean = false
        var RukuD:Float = 0f
        var RukuHeightRangeUpper:Float = 0f
        var RukuHeightRangeLower:Float = 0f
        var RukuA:Float = 0f
        var RukuAreaRangeUpper:Float = 0f
        var RukuAreaRangeLower:Float = 0f
        var ShejdaPerformed:Boolean = false
        var ShejdaD:Float = 0f
        var ShejdaHeightRangeUpper:Float = 0f
        var ShejdaHeightRangeLower:Float = 0f
        var ShejdaA:Float = 0f
        var ShejdaAreaRangeUpper:Float = 0f
        var ShejdaAreaRangeLower:Float = 0f
        var TahassudPerformed:Boolean = false
        var TahassudD:Float = 0f
        var TahassudHeightRangeUpper:Float = 0f
        var TahassudHeightRangeLower:Float = 0f
        var TahassudA:Float = 0f
        var TahassudAreaRangeUpper:Float = 0f
        var TahassudAreaRangeLower:Float = 0f
    }


    fun setQiamAndInitPrayerMovementMeasurement(){
        //Setting Qiam height and area
        QiamHeight = faceDistance
        QiamArea = faceArea

        //Initializing prayer movement measurement

        //Qiam position
        QiamHeightRangeUpper = QiamHeight*1.2f
        QiamHeightRangeLower = QiamHeight*0.8f
        QiamAreaRangeUpper = QiamArea*1.2f
        QiamAreaRangeLower = QiamArea*0.8f

        //Ruku position
        RukuHeightRangeUpper = QiamHeight*0.5f
        RukuHeightRangeLower = QiamHeight*0.4f
        RukuAreaRangeUpper = QiamArea*0.6f
        RukuAreaRangeLower = QiamArea*0.5f

        //Shejda Position
        ShejdaHeightRangeUpper = QiamHeight*0.2f
        ShejdaHeightRangeLower = QiamHeight*0.01f
        ShejdaAreaRangeUpper = QiamArea*0.2f
        ShejdaAreaRangeLower = QiamArea*0.01f

        //Tahassud Position
        TahassudHeightRangeUpper = QiamHeight*0.5f
        TahassudHeightRangeLower = QiamHeight*0.4f
        TahassudAreaRangeUpper = QiamArea*1.2f
        TahassudAreaRangeLower = QiamArea*0.8f
    }

    fun Motion(){
        MotionD = faceDistance
        MotionA = faceArea
    }

    fun RunningMotion(){
        Motion()

        //Qiam performed
        if (!QiamPerformed
            && (MotionD in QiamHeightRangeLower..QiamHeightRangeUpper)
            && (MotionA in QiamAreaRangeLower..QiamAreaRangeUpper))
        {
            QiamPerformed = true
            myUtils.beginPrayerAudio()
        }

        //Ruku initialized
        if(!RukuPerformed && !RukuInit
            && (MotionD in RukuHeightRangeLower..RukuHeightRangeUpper)
            && (MotionA in RukuAreaRangeLower..RukuAreaRangeUpper))
        {
            RukuInit = true
            myUtils.bowInitializedAudio()
        }

        //Ruku performed
        if (RukuInit && !RukuPerformed
            && (MotionD in QiamHeightRangeLower..QiamHeightRangeUpper)
            && (MotionA in QiamAreaRangeLower..QiamAreaRangeUpper))
        {
            RukuPerformed = true
            myUtils.rukuPerformedAudio()
        }

        //Shejda performed
        if (!ShejdaPerformed
            && (MotionD in ShejdaHeightRangeLower..ShejdaHeightRangeUpper)
            && (MotionA in ShejdaAreaRangeLower..ShejdaAreaRangeUpper))
        {
            ShejdaPerformed = true
            myUtils.prostrationOnePerformed()
        }

        //Tahassud performed
        if (!TahassudPerformed
            && (MotionD in TahassudHeightRangeLower..TahassudHeightRangeUpper)
            && (MotionA in TahassudAreaRangeLower..TahassudAreaRangeUpper))
        {
            TahassudPerformed = true
            myUtils.prostrationMissedOne()
        }
    }
    fun Testing(){
//        Log.e("PrayerAlgo",time.toString())
//        Log.e("QiamInit", QiamHeight.toString())
        Log.e("vars",faceArea.toString())

    }

}