package com.camtech.books.utils

abstract class OnSwipeListener {
    // Used to set the same action to both a
    // left and right swipe
    open fun onSwipe(position: Int) {}
    open fun onSwipeLeft(position: Int) {}
    open fun onSwipeRight(position: Int) {}
}
