package com.camtech.books.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Class to listen for changes in internet connection.
 * Since it's not registered in the manifest, it only
 * lives within the instance of the class it's used in.
 */
class ConnectivityReceiver(private val context: Context) : BroadcastReceiver() {
    private var connectionListener: ConnectionListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            if (!hasConnection()) connectionListener?.onConnectionDropped()
            else connectionListener?.onConnect()
        }
    }

    fun hasConnection(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    fun setConnectionListener(connectionListener: ConnectionListener) {
        this.connectionListener = connectionListener
    }
}