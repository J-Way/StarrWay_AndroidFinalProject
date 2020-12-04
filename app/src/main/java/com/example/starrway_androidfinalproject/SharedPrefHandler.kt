package com.example.starrway_androidfinalproject

import android.content.Context
import android.content.SharedPreferences
import com.example.R

class SharedPrefHandler(val context:Context) {
    // initial prefs setup
    private val PREFS_NAME : String = context?.getString(R.string.pref_file)
    private val sharedPref: SharedPreferences = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLastEdited(KEY_NAME: String, id: Long) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putLong(KEY_NAME, id)

        editor!!.commit()
    }

    fun getValueLong(KEY_NAME: String): Long? {
        return sharedPref.getLong(KEY_NAME, -1)
    }

    // not currently used as there isn't an official delete / clear cache implemented
    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.clear()
        editor.commit()
    }
}