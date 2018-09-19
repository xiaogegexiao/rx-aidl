package com.xiao.aidlexamplereceiver

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

import com.xiao.aidlexample.IMainService
import com.xiao.aidlexample.MainObject

import java.util.ArrayList
import android.graphics.Bitmap
import android.support.v4.app.NotificationCompat
import android.graphics.BitmapFactory
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import com.xiao.Constants
import com.xiao.MainActivity
import com.xiao.NOTIFICATION_ID


/**
 * @author Xiao
 */
class MainService : Service() {

    private val mBinder = object : IMainService.Stub() {
        @Throws(RemoteException::class)
        override fun listFiles(path: String): Array<MainObject> {
            log("Received list command for: $path")
            val toSend = ArrayList<MainObject>()
            // Generates a list of 1000 objects that aren't sent back to the binding Activity
            for (i in 0..999)
                toSend.add(MainObject("/example/item" + (i + 1)))
            return toSend.toTypedArray()
        }
    }

    private fun log(message: String) {
        Log.v("MainService", message)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String{
        val channelId = "my_service"
        val channelName = "My Background Service"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun createForegroundNotification(): Notification {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = Constants.MAIN_ACTION
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)

        val stopIntent = Intent(this, MainService::class.java)
        stopIntent.action = Constants.STOPFOREGROUND_ACTION
        val pStopIntent = PendingIntent.getService(this, 0,
                stopIntent, 0)

        val icon = BitmapFactory.decodeResource(resources,
                R.mipmap.ic_launcher)

        return NotificationCompat.Builder(this, channelId)
                .setContentTitle("Foreground service")
                .setTicker("Foreground service")
                .setContentText("My Service")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_baseline_pause, "Stop", pStopIntent)
                .build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        log("Received start command.")
        if (intent.action == Constants.STARTFOREGROUND_ACTION) {
            log("Received Start Foreground Intent ")
            startForeground(NOTIFICATION_ID.FOREGROUND_SERVICE, createForegroundNotification())
        } else if (intent.action == Constants.STOPFOREGROUND_ACTION) {
            log("Received Stop Foreground Intent")
            stopForeground(true)
            stopSelf()
        }
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log("Received service onCreate")
    }

    override fun onBind(intent: Intent): IBinder? {
        log("Received binding.")
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        log("Received unbind")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        log("Received service destroyed")
    }
}
