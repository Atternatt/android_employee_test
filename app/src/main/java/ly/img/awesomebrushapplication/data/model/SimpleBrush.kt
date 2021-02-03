package ly.img.awesomebrushapplication.data.model

import android.os.Parcel
import android.os.Parcelable

internal class SimpleBrush(
    private val brushColor: Int,
    private val brushSize: Float): Brush {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(brushColor)
        parcel.writeFloat(brushSize)
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SimpleBrush> {
        override fun createFromParcel(parcel: Parcel): SimpleBrush {
            return SimpleBrush(parcel)
        }

        override fun newArray(size: Int): Array<SimpleBrush?> {
            return arrayOfNulls(size)
        }
    }

    override fun getColor(): Int = brushColor

    override fun getSize(): Float = brushSize

}