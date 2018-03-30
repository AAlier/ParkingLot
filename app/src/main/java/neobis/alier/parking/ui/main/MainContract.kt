package neobis.alier.parking.ui.main

import neobis.alier.parking.models.Data
import neobis.alier.parking.utils.IProgressBar

interface MainContract {
    interface View : IProgressBar {
        fun onSuccess(list: MutableList<Data>)
        fun onResumeError(message: String)
    }

    interface Presenter {
        fun loadData()
        fun request()
        fun sortNearestPlace(list: MutableList<Data>, isFree: Boolean): MutableList<Data>
    }
}