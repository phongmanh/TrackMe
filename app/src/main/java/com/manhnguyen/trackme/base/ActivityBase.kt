package com.manhnguyen.trackme.base

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.manhnguyen.trackme.R
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

abstract class ActivityBase : AppCompatActivity() {

    private var currentChildFragmentTag: String? = null

    protected fun startMainActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    protected fun showProgressBar(progressBar: MaterialProgressBar) {
        try {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            progressBar.visibility = View.VISIBLE
        } catch (it: Exception) {
            it.printStackTrace()
        }
    }

    protected fun closeProgressbar(progressBar: MaterialProgressBar) {
        try {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.GONE
        } catch (it: Exception) {
            it.printStackTrace()
        }
    }

    protected fun replaceFragment(isAdmin: Boolean, fragment: Fragment, newTag: String) {
        try {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.mainContainer, fragment, newTag).commit()
            currentChildFragmentTag = null

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun replaceChildFragment(fragment: Fragment, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.mainContainer, fragment, tag)
        ft.addToBackStack(null)
        ft.commit()
    }

    protected fun isPerformDestroy(): Boolean {
        return currentChildFragmentTag == null
    }

}