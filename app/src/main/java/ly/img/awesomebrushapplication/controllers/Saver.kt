package ly.img.awesomebrushapplication.controllers

import android.graphics.Bitmap

internal interface Saver {

    fun save(bitmap: Bitmap)

}