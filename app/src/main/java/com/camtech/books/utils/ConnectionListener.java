package com.camtech.books.utils;

import java.io.IOException;

public abstract class ConnectionListener {
    public void onConnect(){}
    public void onConnectionTimeout(IOException e){}
    public void onConnectionDropped(){}
}
