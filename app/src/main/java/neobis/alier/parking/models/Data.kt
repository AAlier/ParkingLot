package neobis.alier.parking.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Data() : RealmObject(), Parcelable {
    @PrimaryKey
    var id: String? = null
    var coordinates: RealmList<Coord> = RealmList()
    var busy_parking_places: Int = 0
    var parking_places: Int = 0
    var image: String? = null
    var description: String? = null
    var title: String? = null
    var cost_per_min: Float = 0f
    var isHistory: Boolean = false
    var startTime: Long = 0
    var endTime: Long = 0
    var distance: Double = 0.0

    constructor(parcel: Parcel) : this() {
        val temp = parcel.createTypedArrayList(Coord.CREATOR)
        coordinates = RealmList()
        if (temp != null) coordinates.addAll(temp)

        busy_parking_places = parcel.readInt()
        parking_places = parcel.readInt()
        image = parcel.readString()
        description = parcel.readString()
        title = parcel.readString()
        cost_per_min = parcel.readFloat()
        id = parcel.readString()
        isHistory = parcel.readByte() != 0.toByte()
        distance = parcel.readDouble()
    }

    fun getImgUrl(): String? {
        return image?.replace(" ", "")
    }

    fun available(): Int {
        return parking_places - busy_parking_places
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeTypedList(coordinates)
        dest?.writeInt(busy_parking_places)
        dest?.writeInt(parking_places)
        dest?.writeString(image)
        dest?.writeString(description)
        dest?.writeString(title)
        dest?.writeFloat(cost_per_min)
        dest?.writeString(id)
        dest?.writeByte((if (isHistory) 1 else 0).toByte())
        dest?.writeDouble(distance)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }

}