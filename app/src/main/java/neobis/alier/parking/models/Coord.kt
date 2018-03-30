package neobis.alier.parking.models

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Coord() : RealmObject(), Parcelable {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var lat: Double = 0.0
    var lng: Double = 0.0

    constructor(parcel: Parcel) : this() {
        lat = parcel.readDouble()
        lng = parcel.readDouble()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Coord> {
        override fun createFromParcel(parcel: Parcel): Coord {
            return Coord(parcel)
        }

        override fun newArray(size: Int): Array<Coord?> {
            return arrayOfNulls(size)
        }
    }

}