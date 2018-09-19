package com.xiao.aidlexamplereceiver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class NullBinderService : Service() {
    companion object {
        private val TAG = NullBinderService::class.java.simpleName
    }

    private val mBinder = object : INullBinderService.Stub() { }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Received onCreate")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "Received binding")
        return null
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