package neobis.alier.parking.utils

import android.app.Activity
import android.location.Location
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.LatLng
import io.realm.RealmList
import neobis.alier.parking.models.Coord

object CommonUtils {
    /*
   * This method is used for GoogleMaps.
   * It checks if Google Play Services are available or not.
   * If Google Play Services are not available,
   * and the @finish parameter is true,
   * then it shows a dialog with a button.
   * When the button is pressed the activity finishes.
   */
    fun isGooglePlayServicesAvailable(activity: Activity, finish: Boolean): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status) && finish) {
                val dialog = googleApiAvailability.getErrorDialog(activity, status, 9000)

                dialog.setOnDismissListener { activity.finish() }
                dialog.show()
            }
            return false
        }
        return true
    }

    // @REFERENCE
    // https://stackoverflow.com/questions/26014312/identify-if-point-is-in-the-polygon?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    fun isPointInPolygon(tap: Location, vertices: RealmList<Coord>): Boolean {
        val intersectCount = (0 until vertices.size - 1).count {
            val latlng1 = LatLng(vertices[it].lat, vertices[it].lng)
            val latlng2 = LatLng(vertices[it + 1].lat, vertices[it + 1].lng)
            rayCastIntersect(tap, latlng1, latlng2)
        }
        return intersectCount % 2 == 1 // odd = inside, even = outside;
    }

    private fun rayCastIntersect(tap: Location, vertA: LatLng, vertB: LatLng): Boolean {
        val aY = vertA.latitude
        val bY = vertB.latitude
        val aX = vertA.longitude
        val bX = vertB.longitude
        val pY = tap.latitude
        val pX = tap.longitude

        if (aY > pY && bY > pY || aY < pY && bY < pY
                || aX < pX && bX < pX) {
            return false // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        val m = (aY - bY) / (aX - bX) // Rise over run
        val bee = -aX * m + aY // y = mx + b
        val x = (pY - bee) / m // algebra is neat!

        return x > pX
    }

    fun printTime(difference: Long): String {
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = difference / daysInMilli
        var different = difference % daysInMilli

        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli

        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli

        val elapsedSeconds = different / secondsInMilli
        return "hour:$elapsedHours, minute:$elapsedMinutes, second:$elapsedSeconds"
    }
}