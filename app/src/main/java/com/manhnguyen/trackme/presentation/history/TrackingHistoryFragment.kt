package com.manhnguyen.trackme.presentation.history

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.manhnguyen.trackme.ApplicationController
import com.manhnguyen.trackme.R
import com.manhnguyen.trackme.base.FragmentBase
import com.manhnguyen.trackme.common.Constants
import com.manhnguyen.trackme.domain.model.AvgTrackingDataModel
import com.manhnguyen.trackme.presentation.activities.MainActivity
import com.manhnguyen.trackme.presentation.adapters.TrackingHistoryAdapter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine


class TrackingHistoryFragment : FragmentBase() {


    @Inject
    lateinit var historyViewModel: HistoryViewModel

    private var _unbinder: Unbinder? = null

    @BindView(R.id.recycleView)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.recordButton)
    lateinit var recordButton: ImageButton

    @BindView(R.id.progressBar)
    lateinit var progressBar: MaterialProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ApplicationController.appComponent.inject(this)
        val view = inflater.inflate(R.layout.fragment_tracking_history, container, false)
        _unbinder = ButterKnife.bind(this, view)
        historyViewModel.loadTrackingHistoryData()
        Log.e("onCreateView","Show progress")
        showProgressBar(progressBar)
        return view
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {

        if (nextAnim != 0x0) {

            val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    Log.e("TAG", "Animation started.")
                    // additional functionality

                }

                override fun onAnimationRepeat(animation: Animation) {
                    Log.e("TAG", "Animation repeating.")
                    // additional functionality
                }

                override fun onAnimationEnd(animation: Animation) {
                    Log.e("TAG", "Animation ended.")
                    initView()
                }
            })

            return anim
        }
        return null
    }

    private fun initView() {
        try {
            historyViewModel.getTrackingHistoryData()
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    Log.e("initView", "Read data")
                    val mAdapter: TrackingHistoryAdapter
                    if (it.isEmpty())
                        mAdapter =
                            TrackingHistoryAdapter(
                                requireContext(),
                                arrayListOf(
                                    AvgTrackingDataModel(
                                        routeId = 0L,
                                        duration = 0,
                                        distance = 0f,
                                        timestamp = 0L,
                                        agvSpeed = 0f,
                                        points = ""
                                    )
                                )
                            )
                    else
                        mAdapter =
                            TrackingHistoryAdapter(
                                requireContext(),
                                it
                            )

                    recyclerView.apply {
                        layoutManager = object : LinearLayoutManager(requireContext()) {
                            override fun smoothScrollToPosition(
                                recyclerView: RecyclerView?,
                                state: RecyclerView.State?,
                                position: Int
                            ) {
                                val smoothScroller: LinearSmoothScroller =
                                    object : LinearSmoothScroller(requireContext()) {
                                        private val SPEED = 5f // Change this value (default=25f)

                                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                                            return SPEED / displayMetrics.densityDpi
                                        }
                                    }
                                smoothScroller.targetPosition = position
                                startSmoothScroll(smoothScroller)

                            }

                            override fun onLayoutCompleted(state: RecyclerView.State?) {

                            }
                        }
                        adapter = mAdapter
                        setHasFixedSize(true)
                    }
                    closeProgressbar(progressBar)
                })


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnClick(R.id.recordButton)
    fun onAction() {
        (activity as MainActivity).initView(Constants.ViewType.RECORD)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _unbinder?.unbind()
    }
}