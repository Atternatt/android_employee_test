package ly.img.awesomebrushapplication.controllers

import android.graphics.Bitmap

/**
 * Abstraction to implement a bitmap saving.
 *
 * - In this exercise we implemented an Android Specific Gallery Saver @see [AndroidGallerySaver] for more details
 */
internal interface Saver {

    /**
     * Save the specified bitmap.
     */
    fun save(bitmap: Bitmap)

}