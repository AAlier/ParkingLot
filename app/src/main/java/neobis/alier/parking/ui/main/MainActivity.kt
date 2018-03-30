package neobis.alier.parking.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_main.*
import neobis.alier.parking.R
import neobis.alier.parking.models.Data
import neobis.alier.parking.ui.service.LocationUpdateService
import neobis.alier.parking.ui.BaseActivity
import neobis.alier.parking.ui.history.HistoryActivity
import neobis.alier.parking.ui.map_view.MapViewActivity
import neobis.alier.parking.utils.Permissions
import neobis.alier.parking.utils.Shares


class MainActivity : BaseActivity(), MainContract.View, DataAdapter.Listener {
    private lateinit var presenter: MainPresenter
    private lateinit var adapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        init()
    }

    private fun init() {
        initRecyclerView()
        initPresenter()
        startReceiver()
        refreshView.setOnRefreshListener {
            presenter.request()
        }
    }

    private fun startReceiver() {
        if (Permissions.iPermissionLocation(this))
            startService(Intent(this, LocationUpdateService::class.java))
    }

    private fun initPresenter() {
        presenter = MainPresenter(this, app.service, this, app.realm)
        presenter.loadData()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DataAdapter(ArrayList(), false, this)
        recyclerView.adapter = adapter
    }

    override fun onSuccess(list: MutableList<Data>) {
        adapter.setList(list)
    }

    override fun onClick(model: Data) {
        val intent = Intent(this, MapViewActivity::class.java)
        intent.putExtra("data", model)
        startActivity(intent)
    }

    override fun onLongClick(id: String?) {}

    override fun onResumeError(message: String) {
        showWarningMessage(message)
    }

    override fun showProgress() {
        refreshView.isRefreshing = true
    }

    override fun hideProgress() {
        refreshView.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (Permissions.iPermissionLocation(this)) {
            when (item.itemId) {
                R.id.item_sort_nearest -> {
                    val list = presenter.sortNearestPlace(adapter.getList(), false)
                    adapter.setList(list)
                }
                R.id.item_sort_available -> {
                    val list = presenter.sortNearestPlace(adapter.getList(), true)
                    adapter.setList(list)
                }
                R.id.item_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }
            }
        } else {
            showWarningMessage(getString(R.string.error_give_perm))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val alertMenuItem = menu.findItem(R.id.item_fict_location)
        val rootView = alertMenuItem.actionView as LinearLayout

        val isFakeSwitch = rootView.findViewById<Switch>(R.id.isFake)
        isFakeSwitch.isChecked = Shares.isFake(this)
        isFakeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Shares.setIsFake(applicationContext, isChecked)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permissions.Request.ACCESS_FINE_LOCATION
                && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startReceiver()
        }
    }
}