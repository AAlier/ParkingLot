package neobis.alier.parking

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class StartApplication : Application() {
    lateinit var service: ForumService
    lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()
        service = Network.initService()
        initRealm()
    }

    @Synchronized private fun initRealm() {
        Realm.init(applicationContext)
        val realmConfiguration = RealmConfiguration.Builder()
                .name("parking_lot")
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        realm = Realm.getDefaultInstance()
    }
}