package com.manhnguyen.trackme.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.manhnguyen.trackme.R
import com.manhnguyen.trackme.common.Constants
import com.manhnguyen.trackme.domain.model.LatLngModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.*

import kotlin.collections.ArrayList


class MapDrawer constructor(val context: Context) {

    private val PATTERN_GAP_LENGTH_PX = 20
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    private val WIDTH = 3f
    private val USER_LINE_WIDTH = 5f
    private val VIEW_ROUTE_LINE_WIDTH = 8f

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_DASH = listOf(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    val PATTERN_POLYGON_DASH_DOT = listOf(DOT, GAP, DASH, GAP)

    // Create a stroke pattern of a gap followed by a dot.
    val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)


    private val checkPointZIndex = 1f
    private val userLineZIndex = 2f
    private var endPointBitmapIcon: Bitmap?
    private var startPointBitmapIcon: Bitmap?

    init {

        val drawable1 = context.getDrawable(R.drawable.ic_map_marker_alt_solid)!!
        val drawable2 = context.getDrawable(R.drawable.ic_dot_circle_regular)!!
        startPointBitmapIcon =
            convertToBitmap(drawable1, drawable1.intrinsicWidth, drawable1.intrinsicHeight)
        endPointBitmapIcon =
            convertToBitmap(drawable2, drawable2.intrinsicWidth, drawable2.intrinsicHeight)

    }

    private fun convertToBitmap(
        drawable: Drawable,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, widthPixels, heightPixels)
        drawable.draw(canvas)
        return bitmap
    }


    fun drawSegment(
        googleMap: GoogleMap?,
        points: ArrayList<LatLng>,
        oldPolyline: Polyline?
    ): Polyline? {
        try {

            val polylineOptions = PolylineOptions().width(WIDTH).clickable(false).addAll(points)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                polylineOptions.color(context.resources.getColor(R.color.colorRed))
            else
                polylineOptions.color(context.getColor(R.color.colorRed))

            oldPolyline?.remove()
            return googleMap?.addPolyline(polylineOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun drawStartLocation(googleMap: GoogleMap?, location: LatLng) {
        try {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.fromBitmap(startPointBitmapIcon))
                    .anchor(0.5f, 0.5f)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun drawUserHistory(
        googleMap: GoogleMap?,
        points: ArrayList<LatLngModel>
    ) {
        try {
            GlobalScope.launch {
                val latLng = ArrayList<LatLng>()
                val polylineOptions = PolylineOptions()

                withContext(Dispatchers.Default) {
                    points.forEach { latLng.add(LatLng(it.lat, it.lng)) }

                    polylineOptions
                        .clickable(false)
                        .addAll(latLng)
                        .width(USER_LINE_WIDTH)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                        polylineOptions.color(context.resources.getColor(R.color.colorRed))
                    else
                        polylineOptions.color(context.getColor(R.color.colorRed))


                }
                withContext(Dispatchers.Main) {
                    drawStartLocation(googleMap, latLng[0])

                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(latLng[latLng.size - 1])
                            .icon(BitmapDescriptorFactory.fromBitmap(endPointBitmapIcon))
                            .title("End")
                            .anchor(0.5f, 0.5f)
                    )
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng[0],
                            Constants.ZOOM_DEFAULT
                        )
                    )
                    googleMap?.addPolyline(polylineOptions)
                    Log.e("Drawer", "drawUserHistory")
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
