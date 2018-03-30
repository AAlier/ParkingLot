package neobis.alier.parking.ui.history

import android.content.Context
import android.text.TextUtils
import io.realm.Realm
import neobis.alier.parking.models.Data
import neobis.alier.parking.utils.Const

class HistoryPresenter(val view: HistoryContract.View?, val realm: Realm, val context: Context) : HistoryContract.Presenter {

    override fun loadHistory() {
        /*val list = FileUtils.readFile("history.json", context)
        view?.onSuccess(list)*/
        val result = realm.where(Data::class.java).equalTo("isHistory", true).findAll()
        if (result != null && isViewAttached()) view!!.onSuccess(realm.copyFromRealm(result))
        else if (isViewAttached()) view!!.onError(Const.ERROR_LOAD)
    }

    private fun isViewAttached(): Boolean = view != null

    override fun removeFromDB(id: String?) {
        if (id != null && !TextUtils.isEmpty(id)) {
            realm.executeTransaction({ _ ->
                realm.where(Data::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
            })
        }
        loadHistory()
    }
}