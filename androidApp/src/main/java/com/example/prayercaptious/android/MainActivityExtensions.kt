package com.example.prayercaptious.android

import com.example.prayercaptious.android.MainActivity
fun MainActivity.getTargetedWidthHeight():Pair<Int,Int> {
    val maxWidthForPortraitMode: Int = bindingcameraview.viewFinder.width
    val maxHeightForPortraitMode: Int = bindingcameraview.viewFinder.height

    return Pair(maxWidthForPortraitMode,maxHeightForPortraitMode)
}