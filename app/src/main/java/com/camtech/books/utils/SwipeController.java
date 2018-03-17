package com.camtech.books.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.camtech.books.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeController extends ItemTouchHelper.SimpleCallback {

    private Context context;
    private static final int LEFT = ItemTouchHelper.LEFT;
    private static final int RIGHT = ItemTouchHelper.RIGHT;
    private OnSwipeListener onSwipeListener;
    private OnSwipeLeftListener onSwipeLeftListener;
    private OnSwipeRightListener onSwipeRightListener;
    private boolean addAndDelete = true;

    public SwipeController(Context context) {
        super(0, LEFT | RIGHT);
        this.context = context;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        switch (direction) {
            case RIGHT:
                if (onSwipeListener != null) onSwipeListener.onSwipeListener(position);
                if (onSwipeRightListener != null) onSwipeRightListener.onSwipeRight(position);
                break;
            case LEFT:
                if (onSwipeListener != null) onSwipeListener.onSwipeListener(position);
                if (onSwipeLeftListener != null) onSwipeLeftListener.onSwipeLeft(position);
        }
    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
        RecyclerViewSwipeDecorator.Builder builder = new RecyclerViewSwipeDecorator.Builder(
                context,
                c,
                recyclerView,
                viewHolder,
                dX, dY,
                actionState,
                isCurrentlyActive);

        if (addAndDelete) {
            builder.addSwipeLeftActionIcon(R.drawable.ic_star);
            builder.addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.addFavorite));
            builder.addSwipeRightActionIcon(R.drawable.ic_delete);
            builder.addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.removeFavorite));
            builder.create().decorate();
        } else {
            builder.addActionIcon(R.drawable.ic_delete);
            builder.addBackgroundColor(ContextCompat.getColor(context, R.color.removeFavorite));
            builder.create().decorate();
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * Because this swipe controller is used to show favorite books, it
     * wouldn't make sense to show the option to add a book to favorites
     * if the book has already been added to favorites
     *
     * @param addAndDelete Set this to true to show both options (i.e. 'add to
     *                     favorites' and 'remove from favorites'). The default
     *                     value is true.
     * */
    public void setAddAndDelete(boolean addAndDelete) {
        this.addAndDelete = addAndDelete;
    }

    /**
     * Below are methods to set various swipe listeners.
     * Use setOnSwipeListener to set one listener for both left and right.
     */
    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public void setOnSwipeLeftListener(OnSwipeLeftListener onSwipeLeftListener) {
        this.onSwipeLeftListener = onSwipeLeftListener;
    }

    public void setOnSwipeRightListener(OnSwipeRightListener onSwipeRightListener) {
        this.onSwipeRightListener = onSwipeRightListener;
    }

    public interface OnSwipeListener {
        void onSwipeListener(int position);
    }

    public interface OnSwipeLeftListener {
        void onSwipeLeft(int position);
    }

    public interface OnSwipeRightListener {
        void onSwipeRight(int position);
    }
}
