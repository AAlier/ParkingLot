package neobis.alier.parking.ui.main

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_data_view.view.*
import neobis.alier.parking.R
import neobis.alier.parking.models.Data
import neobis.alier.parking.utils.CommonUtils
import java.text.SimpleDateFormat
import java.util.*

class DataAdapter(private var list: MutableList<Data>,
                  val isHistory: Boolean,
                  val listener: Listener) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_data_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(newList: MutableList<Data>) {
        this.list = newList;
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: Data) {
            if(isHistory){
                val time = SimpleDateFormat("MM-dd HH:mm", Locale.ENGLISH).format(model.startTime)
                val difference = model.endTime - model.startTime
                val timeSpent = CommonUtils.printTime(difference)
                itemView.date_time.text = itemView.context.getString(R.string.date_s, time, timeSpent)
                itemView.date_time.visibility = View.VISIBLE
            }else{
                itemView.date_time.visibility = View.GONE
            }

            Glide.with(itemView.context).load(model.getImgUrl()).into(itemView.imageView)
            itemView.title.text = model.title
            itemView.cost.text = model.cost_per_min.toString()
            itemView.busy.text = model.busy_parking_places.toString()
            itemView.free.text = model.available().toString()

            itemView.mainView.setOnClickListener { v ->
                val temp = v.tag as Data
                listener.onClick(temp)
            }

            itemView.mainView.setOnLongClickListener { v ->
                val temp = v.tag as Data
                listener.onLongClick(temp.id)
                return@setOnLongClickListener true
            }
            itemView.mainView.tag = model
        }
    }

    fun getList(): MutableList<Data> = list

    interface Listener {
        fun onClick(model: Data)
        fun onLongClick(id: String?)
    }
}