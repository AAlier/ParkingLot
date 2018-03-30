package neobis.alier.parking.ui.history

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_history.*
import neobis.alier.parking.R
import neobis.alier.parking.models.Data
import neobis.alier.parking.ui.BaseActivity
import neobis.alier.parking.ui.main.DataAdapter
import neobis.alier.parking.ui.map_view.MapViewActivity

class HistoryActivity : BaseActivity(), HistoryContract.View, DataAdapter.Listener {
    private lateinit var presenter: HistoryPresenter
    private lateinit var adapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        init()
    }

    private fun init() {
        initRecyclerView()
        presenter = HistoryPresenter(this, app.realm, this)
        presenter.loadHistory()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DataAdapter(ArrayList(), true, this)
        recyclerView.adapter = adapter
    }

    private fun openViewDetail(data: Data) {
        val intent = Intent(this, MapViewActivity::class.java)
        intent.putExtra("data", data)
        startActivity(intent)
    }

    override fun onSuccess(list: MutableList<Data>) {
        adapter.setList(list)
    }

    override fun onError(message: String) {
        showWarningMessage(message)
    }

    override fun onClick(model: Data) {
        openViewDetail(model)
    }

    override fun onLongClick(id: String?) {
        val args = arrayOf<String>(getString(R.string.delete))
        AlertDialog.Builder(this)
                .setItems(args, { dialog, index ->
                    if (index == 0) verifyDelete(id)
                }).show()
    }

    private fun verifyDelete(id: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.verify_message))
                .setPositiveButton(getString(R.string.yes), { _: DialogInterface, _: Int ->
                    presenter.removeFromDB(id)
                }).setNegativeButton(getString(R.string.no), { dialogView: DialogInterface, _: Int ->
                    dialogView.dismiss()
        }).show()
    }
}