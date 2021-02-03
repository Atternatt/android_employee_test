package ly.img.awesomebrushapplication.data.model

import android.os.Parcelable

internal interface Draw: Parcelable {

    fun getId(): Long
    fun addPoint(point: Point)
    fun getPath(): List<Point>
}