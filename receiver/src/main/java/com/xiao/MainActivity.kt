package com.xiao

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.xiao.aidlexamplereceiver.MainService
import com.xiao.aidlexamplereceiver.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start_service_button.setOnClickListener {
            val intent = Intent(this, MainService::class.java)
            intent.action = Constants.STARTFOREGROUND_ACTION
            startService(intent)
        }
    }
}