package neobis.alier.parking.ui

import android.app.ProgressDialog
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MenuItem
import neobis.alier.parking.R
import neobis.alier.parking.StartApplication

open class BaseActivity : AppCompatActivity() {
    private var progressBar: ProgressDialog? = null
    lateinit var app: StartApplication

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        app = application as StartApplication

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    protected fun showWarningMessage(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.error)).setMessage(message)
                .setPositiveButton(android.R.string.ok, { v: DialogInterface, _: Int ->
                    v.dismiss()
                }).show()
    }

    protected fun showProgressBar() {
        if (progressBar == null) {
            progressBar = ProgressDialog(this)
            progressBar!!.setTitle(R.string.loading)
            progressBar!!.setCanceledOnTouchOutside(false)
            progressBar!!.show()
        }
    }

    protected fun hideProgressBar() {
        if (progressBar != null && progressBar!!.isShowing) progressBar!!.dismiss()
        progressBar = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressBar()
    }
}