package com.manhnguyen.trackme.common

import android.content.Context
import android.content.SharedPreferences
import com.manhnguyen.trackme.ApplicationController

/**
 * file will be save in /data/data/YOUR_PACKAGE_NAME/shared_prefs/YOUR_PREFS_NAME.xml
 */
class SharedPreferenceHelper {
    private var context: Context

    private constructor() {
        context = ApplicationController.instance
    }

    private constructor(context: Context) {
        this.context = context
    }

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    private val pref: SharedPreferences
        get() = getPref(context)

    private val editor: SharedPreferences.Editor
        get() = pref.edit()

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPref(context).edit()
    }

    fun setString(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    /**
     * @param key
     * @return
     */
    fun getString(key: String?): String? {
        return pref.getString(key, "")
    }

    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun getInt(key: String?, defValue: Int): Int {
        return pref.getInt(key, defValue)
    }

    fun getLong(key: String?, defValue: Long): Long {
        return pref.getLong(key, defValue)
    }

    fun getFloat(key: String?, defValue: Float): Float {
        return pref.getFloat(key, defValue)
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return pref.getBoolean(key, defValue)
    }

    fun setBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun setBoolean(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        getPref(context).edit().putBoolean(key, value)
    }

    fun getBoolean(
        context: Context,
        key: String?,
        defValue: Boolean
    ): Boolean {
        return getPref(context).getBoolean(key, defValue)
    }

    fun getBoolean(key: String?): Boolean {
        return pref.getBoolean(key, false)
    }

    companion object {
        private const val PREF_FILE_NAME = "PREF"
        private var instance: SharedPreferenceHelper? = null
        fun getInstance(): SharedPreferenceHelper? {
            if (instance == null) {
                instance = SharedPreferenceHelper()
            }
            return instance
        }

        fun getInstance(context: Context): SharedPreferenceHelper? {
            if (instance == null) {
                instance = SharedPreferenceHelper(context)
            }
            return instance
        }
    }
}