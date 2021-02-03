package ly.img.awesomebrushapplication.data.model

import android.os.Parcelable

interface Brush: Parcelable {
    /**
     * The color of the brush
     */
    fun getColor(): Int

    /**
     * The width of the brush
     */
    fun getSize(): Float
}