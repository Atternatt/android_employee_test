package ly.img.awesomebrushapplication.data.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

internal data class SimpleDraw(private val identifier: Long = Date().time,
    private val points: MutableList<Point> = mutableListOf()): Draw {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.createTypedArrayList(Point)!!
    )

    override fun getId(): Long = identifier

    override fun addPoint(point: Point) {
        points.add(point)
    }

    override fun getPath(): List<Point> = points

    //region parcelable
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(identifier)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SimpleDraw> {
        override fun createFromParcel(parcel: Parcel): SimpleDraw {
            return SimpleDraw(parcel)
        }

        override fun newArray(size: Int): Array<SimpleDraw?> {
            return arrayOfNulls(size)
        }
    }
    //endregion
}