package com.camtech.books.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class to listen for changes in internet connection.
 * Since it's not registered in the manifest, it only
 * lives within the instance of the class it's used in.
 * */
public class ConnectivityReceiver extends BroadcastReceiver{

    private Context context;
   private ConnectionListener connectionListener;

    public ConnectivityReceiver(Context context) {

        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Mobile connection has dropped

        if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (!hasConnection()) {
                if (connectionListener != null)connectionListener.onConnectionDropped();
            } else {
                // Connection has been established
               if (connectionListener != null) connectionListener.onConnect();
            }
        }
    }

    public boolean hasConnection(){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
}

