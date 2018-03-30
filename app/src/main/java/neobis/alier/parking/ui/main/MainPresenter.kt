package neobis.alier.parking.ui.main

import android.content.Context
import io.realm.Realm
import io.realm.RealmList
import neobis.alier.parking.ForumService
import neobis.alier.parking.models.Data
import neobis.alier.parking.utils.Const.ERROR_LOAD
import neobis.alier.parking.utils.Shares
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainPresenter(val view: MainContract.View?,
                    private val service: ForumService,
                    val context: Context,
                    val realm: Realm) : MainContract.Presenter {

    override fun request() {
        if (isViewAttached()) view!!.showProgress()
        service.data.enqueue(object : Callback<RealmList<Data>> {
            override fun onResponse(call: Call<RealmList<Data>>?, response: Response<RealmList<Data>>?) {
                if (isViewAttached()) {
                    if (response!!.isSuccessful && response.body() != null) {
                        save(response.body()!!)
                        view!!.onSuccess(response.body()!!)
                    } else
                        view!!.onResumeError(ERROR_LOAD)
                    view.hideProgress()
                }
            }

            override fun onFailure(call: Call<RealmList<Data>>?, t: Throwable?) {
                if (isViewAttached()) {
                    view!!.onResumeError(ERROR_LOAD)
                    view.hideProgress()
                }
                t?.printStackTrace()
            }
        })
    }

    override fun loadData() {
        val list = getFromDB()
        if (list != null && list.size > 0)
            view?.onSuccess(list)
        else
            request()
    }

    override fun sortNearestPlace(list: MutableList<Data>, isFree: Boolean): MutableList<Data> {
        val latLng = Shares.getUserCoords(context)
        if (latLng != null) {
            for (data in list) {
                val startLat = latLng.latitude
                val startLon = latLng.longitude
                val endLat = data.coordinates[0].lat
                val endLon = data.coordinates[0].lng

                data.distance = distance(startLat, startLon, endLat, endLon, 0.0, 0.0)
            }
            synchronized(list) {
                Collections.sort(list) { o1, o2 ->
                    if (isFree)
                        return@sort if (o1.distance >= o2.distance && o1.available() >= o2.available()) -1 else 1
                    return@sort if (o1.distance >= o2.distance) 1 else -1
                }
            }
        }
        return list
    }

    private fun getFromDB(): MutableList<Data>? {
        val result = realm.where(Data::class.java).equalTo("isHistory", false).findAll()
        return if (result != null) realm.copyFromRealm(result) else null
    }

    private fun save(list: RealmList<Data>) {
        realm.executeTransaction({ _ ->
            realm.where(Data::class.java).findAll().deleteAllFromRealm()
        })
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(list)
        realm.commitTransaction()
    }

    private fun isViewAttached(): Boolean = view != null

    /**
     * @reference https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, el1: Double, el2: Double): Double {

        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        +(Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var distance = R.toDouble() * c * 1000.0 // convert to meters

        val height = el1 - el2

        distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)

        return Math.sqrt(distance)
    }

}