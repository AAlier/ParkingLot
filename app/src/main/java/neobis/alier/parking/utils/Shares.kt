package neobis.alier.parking.utils

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson


object Shares {

    private val USER_COORDINATES = "user_position"
    private val IS_FAKE = "is_fake_location_access"
    private val IS_IN_PARKING_LOT = "user_is_in_parking_lot"
    private val STEP = "which_step_user_is"
    private val START_TIMER = "this_is_start_time"
    private val PARKING_LOT_ID = "parking_lot_id"

    private val SETTINGS_STORAGE_NAME = "parking_lot"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(SETTINGS_STORAGE_NAME, Context.MODE_PRIVATE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(SETTINGS_STORAGE_NAME, Context.MODE_PRIVATE).edit()
    }

    fun getUserCoords(context: Context): Location? {
        val json = getPrefs(context).getString(USER_COORDINATES, null)
        val location = if (json == null) null else Gson().fromJson<Location>(json, Location::class.java)
        if (isFake(context)) {
            location?.latitude = 42.87496900989505
            location?.longitude = 74.61529936641455
        }
        return location
    }

    fun setUserCoords(context: Context, location: Location?) {
        val json = if (location == null) null else Gson().toJson(location)
        getEditor(context).putString(USER_COORDINATES, json).apply()
    }

    fun isFake(context: Context): Boolean {
        return getPrefs(context).getBoolean(IS_FAKE, false)
    }

    fun setIsFake(context: Context, isFake: Boolean) {
        getEditor(context).putBoolean(IS_FAKE, isFake).commit()
    }

    /*
      *  STEPS DESCRIBE USER LOCATION
      *  0 - USER IS OUT OF ANY PARKING LOT
      *  1 - USER IS IN TRIAL PERIOD (3 minutes before timer starts counting)
      *  2 - TIMER IS RUNNING/ USER IS IN PARKING LOT
      */
    fun getStep(context: Context): Int {
        return getPrefs(context).getInt(STEP, 0)
    }

    fun setStep(context: Context, step: Int) {
        getEditor(context).putInt(STEP, step).commit()
    }

    fun getStartTime(context: Context): Long {
        return getPrefs(context).getLong(START_TIMER, 0)
    }

    fun setStartTime(context: Context, time: Long) {
        getEditor(context).putLong(START_TIMER, time).commit()
    }

    fun getPlaceID(context: Context): String {
        return getPrefs(context).getString(PARKING_LOT_ID, "")
    }

    fun setPlaceID(context: Context, id: String) {
        getEditor(context).putString(PARKING_LOT_ID, id).commit()
    }

    fun clear(context: Context){
        getEditor(context).clear().commit()
    }
}