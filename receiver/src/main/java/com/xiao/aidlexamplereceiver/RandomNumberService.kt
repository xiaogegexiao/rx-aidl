package com.xiao.aidlexamplereceiver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class RandomNumberService : Service() {
    companion object {
        private val TAG = RandomNumberService::class.java.simpleName
    }

    private val mBinder = object : IRandomNumberService.Stub() {
        override fun getRandomNumber(range: Int): Int {
            Log.d(TAG, "Received getting random number")
            return (Math.random() * range).toInt()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Received onCreate")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "Received binding")
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Received unbinding")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Received destroy")
    }
}