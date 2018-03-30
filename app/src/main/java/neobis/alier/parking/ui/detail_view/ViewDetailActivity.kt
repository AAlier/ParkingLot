package neobis.alier.parking.ui.detail_view

import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view.*
import neobis.alier.parking.R
import neobis.alier.parking.models.Data
import neobis.alier.parking.ui.BaseActivity

class ViewDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        init()
    }

    private fun init() {
        val model = intent.getParcelableExtra<Data>("data")
        title = model.title
        titleView.text = model.title
        description.text = model.description
        cost.text = model.cost_per_min.toString()
        busy.text = model.busy_parking_places.toString()
        free.text = model.available().toString()
        Glide.with(this).load(model.getImgUrl()).into(imageView)
    }
}