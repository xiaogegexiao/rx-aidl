package com.xiao.aidlexample

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.widget.TextView
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.xiao.aidlexamplereceiver.INullBinderService
import com.xiao.aidlexamplereceiver.IRandomNumberService
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var mDisposable: Disposable? = null
    private var mService: IMainService? = null
    private var mRandomNumberService: IRandomNumberService? = null
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
        bind_mainservice_button.setOnClickListener {
            val serviceIntent = Intent()
                    .setComponent(ComponentName(
                            "com.xiao.aidlexamplereceiver",
                            "com.xiao.aidlexamplereceiver.MainService"))
            mLog.text = ""
            mLog.append("Binding main service…\n")
            mDisposable?.dispose()
            mDisposable = AIDLObservable<IMainService, IMainService.Stub>(this, serviceIntent, IMainService::class.java, IMainService.Stub::class.java)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe({
                        log.append("onNext.\n")
                        mService = it
                        performListing()
                    }, {
                        log.append("onError ${it.message}.\n")
                    }, {
                        log.append("onComplete.\n")
                    })
        }
        bind_randomnumberservice_button.setOnClickListener {
            val serviceIntent = Intent()
                    .setComponent(ComponentName(
                            "com.xiao.aidlexamplereceiver",
                            "com.xiao.aidlexamplereceiver.RandomNumberService"))
            mLog.text = ""
            mLog.append("Binding random number service…\n")
            mDisposable?.dispose()
            mDisposable = AIDLObservable<IRandomNumberService, IRandomNumberService.Stub>(this, serviceIntent, IRandomNumberService::class.java, IRandomNumberService.Stub::class.java)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe({
                        log.append("onNext.\n")
                        mRandomNumberService = it
                        getRandomNumber()
                    }, {
                        log.append("onError ${it.message}.\n")
                    }, {
                        log.append("onComplete.\n")
                    })
        }
        bind_notexistingservice_button.setOnClickListener {
            val serviceIntent = Intent()
                    .setComponent(ComponentName(
                            "com.notexisting.service",
                            "com.notexisting.service.NotExistingSerivce"))
            mLog.text = ""
            mLog.append("Binding not existing service…\n")
            mDisposable?.dispose()
            mDisposable = AIDLObservable<IRandomNumberService, IRandomNumberService.Stub>(this, serviceIntent, IRandomNumberService::class.java, IRandomNumberService.Stub::class.java)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe({
                        log.append("onNext.\n")
                    }, {
                        log.append("onError ${it.message}.\n")
                    }, {
                        log.append("onComplete.\n")
                    })
        }
        bind_nullbinderservice_button.setOnClickListener {
            val serviceIntent = Intent()
                    .setComponent(ComponentName(
                            "com.xiao.aidlexamplereceiver",
                            "com.xiao.aidlexamplereceiver.NullBinderService"))
            mLog.text = ""
            mLog.append("Binding null binder service…\n")
            mDisposable?.dispose()
            mDisposable = AIDLObservable<INullBinderService, INullBinderService.Stub>(this, serviceIntent, INullBinderService::class.java, INullBinderService.Stub::class.java)
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe({
                        log.append("onNext.\n")
                    }, {
                        log.append("onError ${it.message}.\n")
                    }, {
                        log.append("onComplete.\n")
                    })
        }
    }

    private fun getRandomNumber() {
        mLog.append("Requesting random number …\n")
        val randomNumber = mRandomNumberService!!.getRandomNumber(1000)

        mLog.append("Got number $randomNumber.\n")
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
    }
}
