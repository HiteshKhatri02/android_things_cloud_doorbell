package com.ranosys.clouddoorbell

import android.content.Context
import android.content.SharedPreferences

/**
 * @author Ranosys Technologies
 */
internal class SavedPreference (val context: Context) {
    companion object {
        private val MODE_PRIVATE = 0
    }

    private var preferences: SharedPreferences? = null

    init {
        val user_pref = "com.mytake.missions"
        preferences = context.getSharedPreferences(user_pref, MODE_PRIVATE)
    }

    fun storeDeviceToken(deviceToken : String){
        val editor = preferences?.edit()
        editor?.putString("deviceToken", deviceToken)
        editor?.apply()
    }

    fun getDeviceToken():String?{
        return preferences?.getString("deviceToken","")
    }
}