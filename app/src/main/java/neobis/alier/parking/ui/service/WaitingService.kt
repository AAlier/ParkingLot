package neobis.alier.parking.ui.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import io.realm.Realm
import neobis.alier.parking.R
import neobis.alier.parking.StartApplication
import neobis.alier.parking.models.Data
import neobis.alier.parking.ui.history.HistoryActivity
import neobis.alier.parking.utils.CommonUtils
import neobis.alier.parking.utils.Shares
import java.util.*

class WaitingService : Service() {
    companion object {
        val TAG = "WAITINGSERVICE"
    }
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startService()
    }

    /*
    *  STEPS DESCRIBE USER LOCATION
    *  0 - USER IS OUT OF ANY PARKING LOT
    *  1 - USER IS IN TRIAL PERIOD (3 minutes before timer starts counting)
    *  2 - TIMER IS RUNNING/ USER IS IN PARKING LOT
    */
    private fun startService() {
        val app = application as StartApplication
        val list = getFromDB(app.realm)
        val latLng = Shares.getUserCoords(applicationContext)
        if (list != null && latLng != null) {
            for (data in list) {
                val isInPark = CommonUtils.isPointInPolygon(latLng, data.coordinates)
                if (isInPark) {
                    Shares.setPlaceID(applicationContext, data.id!!)
                    val step = Shares.getStep(applicationContext)
                    Log.e(TAG, "USER IN PARKING LOT $step")
                    when (step) {
                        0 -> {
                            // USER ENTERED PARKING LOT
                            // Set 3 minute timer
                            Shares.setStartTime(applicationContext, Calendar.getInstance().timeInMillis)
                            Log.e(TAG, "USER ENTERED PARKING LOT. OPERATION STARTS\n3 MINUTE TIMER BEGAN")
                            Shares.setStep(applicationContext, step + 1)
                        }
                        1 -> {
                            // USER IS IN TRIAL PERIOD (3 minutes before timer starts counting)
                            val curTime = Calendar.getInstance().timeInMillis
                            val pastTime = Shares.getStartTime(applicationContext)
                            val difference = curTime - pastTime
                            CommonUtils.printTime(difference)
                            // Checking whether 3 MINUTES have passed and if yes going to the next step
                            if (difference >= 180000L) {
                                Shares.setStartTime(applicationContext, Calendar.getInstance().timeInMillis)
                                Shares.setStep(applicationContext, step + 1)
                                Log.e(TAG, "3 MINUTES PASSED TIMER HAS STARTED")
                            } else
                                Log.e(TAG, "3 MINUTES NOT PASSED YET")
                        }
                    }
                } else if(Shares.getPlaceID(applicationContext) == data.id) {
                    val step = Shares.getStep(applicationContext)
                    when (step) {
                        2 -> {
                            // USER LEFT PARKING LOT. SAVE PLACE IN A DATABASE
                            val model = getParkingLot(app.realm, Shares.getPlaceID(applicationContext))
                            if (model != null) {
                                saveHistory(app.realm, model)
                                setNotificationPanel(model)
                            }
                            Log.e(TAG, model?.title + " " + model?.id + " has been saved")
                            Shares.clear(applicationContext)
                        }
                    }
                }
            }
        }
        this.stopSelf()
    }


    private fun setNotificationPanel(data: Data) {
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0,
                Intent(applicationContext, HistoryActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 30)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifyBuilder = NotificationCompat.Builder(applicationContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("${data.title} " +
                        "${CommonUtils.printTime(data.endTime - data.startTime)}")
                .setContentText(data.description)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notifyBuilder.build())
    }

    private fun saveHistory(realm: Realm, data: Data) {
        data.isHistory = true
        data.id = UUID.randomUUID().toString()
        data.startTime = Shares.getStartTime(applicationContext)
        data.endTime = Calendar.getInstance().timeInMillis

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(data)
        realm.commitTransaction()
    }

    private fun getParkingLot(realm: Realm, id: String): Data? {
        val result = realm.where(Data::class.java).equalTo("id", id).findFirst()
        return if (result != null) realm.copyFromRealm(result) else null
    }

    private fun getFromDB(realm: Realm): MutableList<Data>? {
        val result = realm.where(Data::class.java).findAll()
        return if (result != null) realm.copyFromRealm(result) else null
    }
}