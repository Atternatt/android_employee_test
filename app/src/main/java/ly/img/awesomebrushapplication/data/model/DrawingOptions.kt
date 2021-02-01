package ly.img.awesomebrushapplication.data.model

import android.os.Parcel
import android.os.Parcelable

data class DrawingOptions(
    val color: Int,
    val strokeSize: Float
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(color)
        parcel.writeFloat(strokeSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DrawingOptions> {
        override fun createFromParcel(parcel: Parcel): DrawingOptions {
            return DrawingOptions(parcel)
        }

        override fun newArray(size: Int): Array<DrawingOptions?> {
            return arrayOfNulls(size)
        }
    }
}