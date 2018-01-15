package com.ranosys.clouddoorbell

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId



/**
 * @author Hitesh Khatri
 */
class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    companion object {
        val TAG = "MyFirebaseIIDService"
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken)
        val pref = SavedPreference(this)
        pref.storeDeviceToken(refreshedToken!!)
    }



}