package ly.img.awesomebrushapplication.controllers

import android.graphics.Bitmap

interface ImageLoader {

    suspend fun loadImage(bitmap: Bitmap)
}