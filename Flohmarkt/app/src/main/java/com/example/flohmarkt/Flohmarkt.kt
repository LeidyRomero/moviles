package com.example.flohmarkt

import android.app.Application

class Flohmarkt: Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    companion object {
        lateinit var mInstance: Flohmarkt
        fun getInstance(): Flohmarkt {
            return mInstance
        }
    }

    fun setConnectivityLister(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiver = listener
    }
}