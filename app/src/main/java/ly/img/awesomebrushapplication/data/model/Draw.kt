package ly.img.awesomebrushapplication.data.model

import android.os.Parcel
import android.os.Parcelable

data class Draw(val points: MutableList<Point>, val drawingOptions: DrawingOptions): Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Point)!!, parcel.readParcelable(DrawingOptions::class.java.classLoader)!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(points)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Draw> {
        override fun createFromParcel(parcel: Parcel): Draw {
            return Draw(parcel)
        }

        override fun newArray(size: Int): Array<Draw?> {
            return arrayOfNulls(size)
        }
    }
}