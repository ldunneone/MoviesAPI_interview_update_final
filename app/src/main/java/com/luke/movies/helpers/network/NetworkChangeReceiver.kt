package com.luke.movies.helpers.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkChangeReceiver: BroadcastReceiver() {
    private lateinit var listener: OnNetworkChangeListener

    fun setListener(listener: OnNetworkChangeListener) {
        this.listener = listener
    }
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (::listener.isInitialized) {
            listener?.let {
                if (NetworkUtil.isInternetConnected()) {
                    listener.onInternetConnected()
                } else {
                    listener.onInternetNotConnected()
                }
            }
        }
    }
    interface OnNetworkChangeListener {
        fun onInternetConnected()
        fun onInternetNotConnected()
    }
}