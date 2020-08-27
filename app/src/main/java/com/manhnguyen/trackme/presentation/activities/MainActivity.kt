package com.manhnguyen.trackme.presentation.activities

import android.location.LocationManager
import android.os.Bundle
import android.widget.FrameLayout
import androidx.lifecycle.ProcessLifecycleOwner

import butterknife.BindView
import butterknife.ButterKnife
import com.manhnguyen.trackme.ApplicationController
import com.manhnguyen.trackme.base.ActivityBase
import com.manhnguyen.trackme.system.power.PowerViewModel
import com.manhnguyen.trackme.system.networking.NetworkViewModel

import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.manhnguyen.trackme.R

import javax.inject.Inject


class MainActivity : ActivityBase() {

    @BindView(R.id.mainNavigation)
    lateinit var mainNavigation: BottomNavigationView

    @BindView(R.id.main_toolbar)
    lateinit var mainToolbar: MaterialToolbar

    @Inject
    lateinit var powerViewModel: PowerViewModel

    @Inject
    lateinit var networkViewModel: NetworkViewModel

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var locationManager: LocationManager


    @BindView(R.id.childFragmentContainer)
    lateinit var childFragmentContainer: FrameLayout

    private var itemSelected: Int = -1
    private var isInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        ApplicationController.appComponent.inject(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            ActivityLifecycle(mainViewModel, this)
        )

        initToolbarTile()
        navAction()
    }


    private fun initToolbarTile() {
        try {
            mainNavigation.inflateMenu(R.menu.main_navigation_menu)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun navAction() {
        try {
            val listener =
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when (item.itemId) {
                    }
                }

            mainNavigation.setOnNavigationItemSelectedListener(listener)
            mainNavigation.selectedItemId = R.id.main_nav_admin_site_map

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}




