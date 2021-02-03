package ly.img.awesomebrushapplication.controllers

import android.graphics.Bitmap

/**
 * Abstraction to create a image loading function that can modify the bitmap as it wants to
 * fulfill its needs.
 *
 * @see [BrushCanvas#loadImage] in order to se a implementation.
 */
interface ImageLoader {

    /**
     * Receives a bitmap to be loaded.
     */
    suspend fun loadImage(bitmap: Bitmap)
}