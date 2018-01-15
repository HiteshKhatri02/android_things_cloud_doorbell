package com.ranosys.clouddoorbell

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import org.json.JSONObject
import com.bumptech.glide.Glide
import android.R.attr.name
import android.annotation.TargetApi
import android.app.NotificationChannel


/**
 * @author Hitesh Khatri
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        val TAG = "MyFirebaseMessaging"


    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        Log.d(TAG, "From: " + p0?.getFrom())

        if (p0?.getData()?.size!! > 0) {
            Log.d(TAG, "Message data payload: " + p0.getData())
            sendNotification(p0.getData())
        }
    }




    fun sendNotification(map:Map<String,String>){
        val CHANNEL_ID = "my_channel_01"



        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_none_black_24dp)
                .setContentTitle(map.get("title"))
                .setContentText(map.get("message"))
                .setChannelId(CHANNEL_ID)
                .setLargeIcon(getImage(map.get("largeIcon")!!))

        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(this@MyFirebaseMessagingService, MainActivity::class.java)
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        val stackBuilder = TaskStackBuilder.create(this)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().





        var mChannel:NotificationChannel?=null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My private channel"// The user-visible name of the channel.
            mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(mChannel)
        }

        mNotificationManager.notify(15456, mBuilder.build())
    }

    fun getImage(url:String):Bitmap{
        return Glide.with(this).load(url).asBitmap().into(100, 100). // Width and height
                get()
    }
}