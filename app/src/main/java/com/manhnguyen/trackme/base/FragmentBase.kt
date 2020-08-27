package com.manhnguyen.trackme.base

import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import me.zhanghai.android.materialprogressbar.MaterialProgressBar


abstract class FragmentBase : Fragment() {


    protected fun showProgressBar(progressBar: MaterialProgressBar) {
        try {
            activity?.window?.setFlags(
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
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.visibility = View.GONE
        } catch (it: Exception) {
            it.printStackTrace()
        }
    }

}