package com.luke.movies.base

import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.fragment.app.Fragment
import com.luke.movies.helpers.network.NetworkChangeReceiver

abstract class BaseFragment:Fragment() {

    protected fun enableInternetConnectionListener(listener: NetworkChangeReceiver.OnNetworkChangeListener){
        internetReceiver.setListener(listener)
    }

    val internetReceiver=NetworkChangeReceiver()

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        internetReceiver.let {
            activity?.registerReceiver(it,intentFilter)
        }
    }

    override fun onStop() {
        internetReceiver.let {
            activity?.unregisterReceiver(it)
        }
        super.onStop()
    }

    protected fun updateTitle(title:String){
        val activity = activity as? BaseActivity
        activity?.apply {
            supportActionBar?.apply {
                setTitle(title)
            }
        }
        enableHome(true)
    }

    protected fun enableHome(isVisible:Boolean){
        val activity = activity as? BaseActivity
        activity?.apply {
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(isVisible)
                setDisplayShowHomeEnabled(isVisible)
                setTitle(title)
            }
        }
    }
}