package com.camtech.books;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.camtech.books.data.Book;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * RecyclerView adapter used in {@link com.camtech.books.activities.SearchBooks}
 * */
public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Book> books;
    private OnItemClickListener onItemClickListener;
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public ViewAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                viewHolder = new BookViewHolder(inflater.inflate(R.layout._list_books, parent, false));
                break;
            case LOADING:
                viewHolder = new LoadingViewHolder(inflater.inflate(R.layout.item_progress, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                BookViewHolder bookViewHolder = (BookViewHolder) holder;
                bookViewHolder.tvTitle.setText(context.getString(R.string.book_title, books.get(position).getTitle()));
                bookViewHolder.tvAuthor.setText(context.getString(R.string.book_author,
                        Arrays.toString(books.get(position).getAuthors()).replace("[", "").replace("]", "")));
                bookViewHolder.tvPublisher.setText(context.getString(R.string.book_publisher, books.get(position).getPublisher()));
                bookViewHolder.ivSmallThumbnail.setImageBitmap(books.get(position).getSmallThumbnail());
                // If the book has been added to favorites (A.K.A, is in the database) so show the bookmark icon
                if (books.get(position).isFavorite(context)) {
                    bookViewHolder.ivFavorite.setVisibility(View.VISIBLE);
                } else {
                    bookViewHolder.ivFavorite.setVisibility(View.GONE);
                }
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == books.size() - 1 && isLoadingAdded) return LOADING;
        else return ITEM;
    }

    public void clear() {
        books.clear();
        notifyItemRangeChanged(0, books.size());
    }

    public void showLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvPublisher;
        ImageView ivSmallThumbnail;
        ImageView ivFavorite;
        CardView cardView;

        BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.book_title);
            tvAuthor = itemView.findViewById(R.id.book_author);
            tvPublisher = itemView.findViewById(R.id.book_publisher);
            ivSmallThumbnail = itemView.findViewById(R.id.book_thumbnail);
            cardView = itemView.findViewById(R.id.root);
            ivFavorite = itemView.findViewById(R.id.book_favorite);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClicked(getAdapterPosition());
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
