package com.camtech.books.utils

import java.io.IOException

/**
 * Used to determine when the device connects
 * or looses/drops connection
 * */
abstract class ConnectionListener {
    open fun onConnect() {}
    open fun onConnectionTimeout(e: IOException) {}
    open fun onConnectionDropped() {}
}
