package neobis.alier.parking.ui.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LocationStarterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val service = Intent(context, LocationUpdateService::class.java)
        context.startService(service)
        Log.e("__________________", "Start Location Update Service")
    }
}