package neobis.alier.parking.ui.history

import neobis.alier.parking.models.Data

interface HistoryContract {

    interface View {
        fun onSuccess(list: MutableList<Data>)
        fun onError(message: String)
    }

    interface Presenter{
        fun loadHistory()
        fun removeFromDB(id: String?)
    }
}