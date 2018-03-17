package com.camtech.books.utils

import android.content.Context
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

import com.camtech.books.R

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class SwipeController(private val context: Context) : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
    private var swipeListener: OnSwipeListener? = null
    private var addAndDelete = true

    companion object {
        private val LEFT = ItemTouchHelper.LEFT
        private val RIGHT = ItemTouchHelper.RIGHT
    }

    override fun onMove(recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean = false


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            RIGHT -> {
                swipeListener?.onSwipe(viewHolder.adapterPosition)
                swipeListener?.onSwipeRight(viewHolder.adapterPosition)
            }
            LEFT -> {
                swipeListener?.onSwipe(viewHolder.adapterPosition)
                swipeListener?.onSwipeLeft(viewHolder.adapterPosition)
            }
        }
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        val builder = RecyclerViewSwipeDecorator.Builder(
                context,
                c,
                recyclerView,
                viewHolder,
                dX, dY,
                actionState,
                isCurrentlyActive)

        if (addAndDelete) {
            builder.addSwipeLeftActionIcon(R.drawable.ic_star)
            builder.addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.addFavorite))
            builder.addSwipeRightActionIcon(R.drawable.ic_delete)
            builder.addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.removeFavorite))
            builder.create().decorate()
        } else {
            builder.addActionIcon(R.drawable.ic_delete)
            builder.addBackgroundColor(ContextCompat.getColor(context, R.color.removeFavorite))
            builder.create().decorate()
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Because this swipe controller is used to show favorite books, it
     * wouldn't make sense to show the option to add a book to favorites
     * if the book has already been added to favorites
     *
     * @param addAndDelete Set this to true to show both options (i.e. 'add to
     * favorites' and 'remove from favorites'). and
     * false to only show the delete option. The default
     * value is true.
     */
    fun setAddAndDelete(addAndDelete: Boolean) {
        this.addAndDelete = addAndDelete
    }

    fun setOnSwipeListener(swipeListener: OnSwipeListener) {
        this.swipeListener = swipeListener
    }
}
