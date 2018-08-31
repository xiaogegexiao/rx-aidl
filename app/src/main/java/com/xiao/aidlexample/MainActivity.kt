package com.xiao.aidlexample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var mService: IMainService? = null
    private lateinit var mLog: TextView

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mLog.append("Service binded!\n")
            mService = IMainService.Stub.asInterface(service)

            performListing()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            // This method is only invoked when the service quits from the other end or gets killed
            // Invoking exit() from the AIDL interface makes the Service kill itself, thus invoking this.
            mLog.append("Service disconnected.\n")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLog = findViewById(R.id.log)

        val serviceIntent = Intent()
                .setComponent(ComponentName(
                        "com.xiao.aidlexamplereceiver",
                        "com.xiao.aidlexamplereceiver.MainService"))
        mLog.text = "Starting service…\n"
        startService(serviceIntent)
        mLog.append("Binding service…\n")
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun performListing() {
        mLog.append("Requesting file listing…\n")
        val start = System.currentTimeMillis()
        var end: Long = 0
        try {
            val results = mService!!.listFiles("/sdcard/testing")
            end = System.currentTimeMillis()
            var index = 0
            mLog.append("Received " + results.size + " results:\n")
            for (o in results) {
                if (index > 20) {
                    mLog.append("\t -> Response truncated!\n")
                    break
                }
                mLog.append("\t -> " + o.path + "\n")
                index++
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        mLog.append("File listing took " + (end.toDouble() - start.toDouble()) / 1000.0 + " seconds, or " + (end - start) + " milliseconds.\n")
        try {
            mService!!.exit()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }
}
