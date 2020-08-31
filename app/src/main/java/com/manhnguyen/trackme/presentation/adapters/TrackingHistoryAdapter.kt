package com.manhnguyen.trackme.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.reflect.TypeToken
import com.manhnguyen.trackme.R
import com.manhnguyen.trackme.common.Constants
import com.manhnguyen.trackme.domain.model.AvgTrackingDataModel
import com.manhnguyen.trackme.domain.model.LatLngModel
import com.manhnguyen.trackme.presentation.MapDrawer
import com.manhnguyen.trackme.util.JsonUtil
import com.manhnguyen.trackme.util.durationFormat
import kotlinx.android.synthetic.main.tracking_history_item_layout.view.*
import java.math.BigDecimal
import java.math.RoundingMode


class TrackingHistoryAdapter constructor(
    val context: Context,
    private val items: ArrayList<AvgTrackingDataModel>
) :
    RecyclerView.Adapter<TrackingHistoryAdapter.MapViewHolder>() {

    val mapDrawer = MapDrawer(context)
    val layoutInflater = LayoutInflater.from(context)


    inner class MapViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var _googleMap: GoogleMap? = null
        private var _mapView: MapView? = null
        private var points = ArrayList<LatLngModel>()

        init {
            _mapView = itemView.mapView
            _mapView?.onCreate(null)
            _mapView?.onResume()
            Log.e("MapViewHolder", "init")
        }

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            try {
                Log.e("bind", "Start")
                val item: AvgTrackingDataModel = items[position]
                itemView.distanceValueLbl.text =
                    "${BigDecimal((item.distance / 1000).toDouble()).setScale(
                        2,
                        RoundingMode.HALF_EVEN
                    ).toFloat()} km"
                itemView.avgSpeedValueLbl.text = "${BigDecimal(item.agvSpeed * 3.6).setScale(
                    2,
                    RoundingMode.HALF_EVEN
                ).toFloat()} km/h"
                itemView.durationLbl.text = item.duration.durationFormat()

                _mapView?.getMapAsync { map ->
                    if (!item.points.isBlank())
                        points = JsonUtil.instance.gson.fromJson(
                            item.points,
                            object : TypeToken<ArrayList<LatLngModel>>() {}.type
                        )
                    if (points.size > 0)
                        mapDrawer.drawUserHistory(map, points)
                }
                if (position == items.size - 1)
                    itemView.dividerView.visibility = View.GONE
                else
                    itemView.dividerView.visibility = View.VISIBLE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        Log.e("onCreateViewHolder", "create")
        val view = layoutInflater
            .inflate(R.layout.tracking_history_item_layout, parent, false)
        Log.e("onCreateViewHolder", "create")
        return MapViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position).hashCode().toLong()
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bind(position)
    }

}