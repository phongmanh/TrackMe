package com.manhnguyen.trackme.presentation.recording

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.manhnguyen.trackme.ApplicationController
import com.manhnguyen.trackme.R
import com.manhnguyen.trackme.base.FragmentBase
import com.manhnguyen.trackme.common.Constants
import com.manhnguyen.trackme.presentation.MapDrawer
import com.manhnguyen.trackme.presentation.activities.MainActivity
import com.manhnguyen.trackme.system.locations.LocationChangeEvent
import com.manhnguyen.trackme.system.locations.LocationViewModel
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.coroutines.launch
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import javax.inject.Inject

class RecordFragment : FragmentBase(), OnMapReadyCallback {


    @Inject
    lateinit var locationViewModel: LocationViewModel

    @Inject
    lateinit var recordViewModel: RecordViewModel

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @BindView(R.id.pauseButton)
    lateinit var pauseButton: ImageButton

    @BindView(R.id.resumeButton)
    lateinit var resumeButton: ImageButton

    @BindView(R.id.stopButton)
    lateinit var stopButton: ImageButton

    @BindView(R.id.durationLbl)
    lateinit var durationLbl: TextView

    @BindView(R.id.distanceValueLbl)
    lateinit var distanceValueLbl: TextView

    @BindView(R.id.speedValueLbl)
    lateinit var speedValueLbl: TextView

    @BindView(R.id.progressBar)
    lateinit var progressBar: MaterialProgressBar

    private var _unbinder: Unbinder? = null
    private var _mapDrawer: MapDrawer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("onCreateView")
        ApplicationController.appComponent.inject(this)
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        _unbinder = ButterKnife.bind(this, view)
        _mapDrawer = MapDrawer(requireContext())
        showProgressBar(progressBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onCreateView")
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unRegisterLocationChanged()
        _unbinder?.unbind()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        try {
            _googleMap = map
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                // Add a marker in Sydney and move the camera
                val currentLocation = LatLng(it.latitude, it.longitude)
                _mapDrawer?.drawStartLocation(map, currentLocation)
                map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLocation,
                        Constants.ZOOM_DEFAULT
                    )
                )

                map?.isMyLocationEnabled = true

                closeProgressbar(progressBar)

                initObserve()
                registerLocationChanged()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var _locationObserver: Observer<LocationChangeEvent>? = null
    private var _currentPolyline: Polyline? = null
    private var _googleMap: GoogleMap? = null

    @SuppressLint("SetTextI18n")
    private fun initObserve() {
        _locationObserver = Observer {
            it.location?.let { loc ->
                viewLifecycleOwner.lifecycleScope.launch {
                    recordViewModel.saveUserLocation(loc)
                }
            }
        }
        /*Update UI*/
        recordViewModel.getUserRecordInfo().observe(viewLifecycleOwner, Observer {
            distanceValueLbl.text = it.distance
            speedValueLbl.text = it.speed
            _currentPolyline = _mapDrawer?.drawSegment(_googleMap, it.points, _currentPolyline)
        })

        recordViewModel.getDurationData().observe(viewLifecycleOwner, Observer {
            durationLbl.text = it
        })

        /*Calc duration*/
        recordViewModel.startDurationChange()

    }

    private fun registerLocationChanged() {
        _locationObserver?.let {
            locationViewModel.getCurrentLocation().observeForever(it)
        }
    }

    private fun unRegisterLocationChanged() {
        _locationObserver?.let {
            locationViewModel.getCurrentLocation().removeObserver(it)
        }
    }

    private fun excAction(action: String) {
        when (action) {
            Constants.Action.RESUME -> {
                recordViewModel.setIsRunning(true)
                registerLocationChanged()
            }
            Constants.Action.PAUSE -> {
                recordViewModel.setIsRunning(false)
                unRegisterLocationChanged()
            }
            Constants.Action.STOP -> {
                showProgressBar(progressBar)
                recordViewModel.setIsRunning(false)
                unRegisterLocationChanged()
                recordViewModel.finishRecord()
                closeProgressbar(progressBar)
                (activity as MainActivity).initView(Constants.ViewType.HISTORY)
            }
        }
    }

    private fun buttonAction(id: Int) {
        try {
            when (id) {
                R.id.pauseButton -> {
                    pauseButton.visibility = View.GONE
                    resumeButton.visibility = View.VISIBLE
                    stopButton.visibility = View.VISIBLE
                    excAction(Constants.Action.PAUSE)
                    println("Pause")
                }
                R.id.resumeButton -> {
                    pauseButton.visibility = View.VISIBLE
                    resumeButton.visibility = View.GONE
                    stopButton.visibility = View.GONE
                    excAction(Constants.Action.RESUME)
                    println("Resume")
                }
                R.id.stopButton -> {
                    pauseButton.visibility = View.GONE
                    resumeButton.visibility = View.GONE
                    stopButton.visibility = View.GONE
                    excAction(Constants.Action.STOP)
                    println("Stop")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnClick(R.id.pauseButton, R.id.resumeButton, R.id.stopButton)
    fun onAction(view: View) {
        buttonAction(view.id)
    }
}