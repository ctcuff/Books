package com.camtech.books;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.camtech.books.activities.BookDetails;
import com.camtech.books.data.Book;
import com.camtech.books.database.DBContract.BookEntry;

import java.util.ArrayList;

/**
 * RecyclerView adapter used in {@link com.camtech.books.activities.MainActivity}
 * */
public class CursorViewAdapter extends RecyclerView.Adapter<CursorViewAdapter.ViewHolder> {

    private final String TAG = CursorViewAdapter.class.getSimpleName();

    private CursorAdapter cursorAdapter;
    private Context context;
    private ArrayList<Book> books;

    /**
     *  A typical {@link CursorAdapter} doesn't work with a RecyclerView
     *  so we need to wrap the cursor adapter inside a RecyclerView adapter
     * */
    public CursorViewAdapter(Context context, Cursor c) {
        this.context = context;
        books = new ArrayList<>();
        cursorAdapter = new CursorAdapter(context, c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout._list_books, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView tvTitle = view.findViewById(R.id.book_title);
                TextView tvAuthor = view.findViewById(R.id.book_author);
                TextView tvPublisher = view.findViewById(R.id.book_publisher);
                ImageView ivThumbnail = view.findViewById(R.id.book_thumbnail);
                ImageView ivFavorite = view.findViewById(R.id.book_favorite);

                int titleIndex = cursor.getColumnIndex(BookEntry.COLUMN_TITLE);
                int authorIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHORS);
                int publisherIndex = cursor.getColumnIndex(BookEntry.COLUMN_PUBLISHER);
                int smallThumbnailIndex = cursor.getColumnIndex(BookEntry.COLUMN_SMALL_THUMBNAIL);
                int descriptionIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_DESCRIPTION);
                int thumbnailIndex = cursor.getColumnIndex(BookEntry.COLUMN_THUMBNAIL);
                int buyLinkIndex = cursor.getColumnIndex(BookEntry.COLUMN_BUY_LINK);
                int apiIdIndex = cursor.getColumnIndex(BookEntry.COLUMN_API_ID);

                String bookTitle = cursor.getString(titleIndex);
                String bookAuthor = cursor.getString(authorIndex);
                String bookPublisher = cursor.getString(publisherIndex);
                Bitmap smallThumbnail = Book.byteArrayToBitmap(cursor.getBlob(smallThumbnailIndex));
                Bitmap thumbnail = Book.byteArrayToBitmap(cursor.getBlob(thumbnailIndex));
                String description = cursor.getString(descriptionIndex);
                String buyLink = cursor.getString(buyLinkIndex);
                String apiId = cursor.getString(apiIdIndex);

                tvTitle.setText(bookTitle);
                tvAuthor.setText(context.getString(R.string.book_detail_authors, bookAuthor));
                tvPublisher.setText(context.getString(R.string.book_detail_publisher, bookPublisher));
                ivThumbnail.setImageBitmap(smallThumbnail);
                ivFavorite.setVisibility(View.VISIBLE);

                books.add(new Book(
                        bookTitle,
                        new String[]{bookAuthor},
                        bookPublisher,
                        description,
                        smallThumbnail,
                        thumbnail,
                        buyLink,
                        apiId));
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor adapter
        View v = cursorAdapter.newView(context, cursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursorAdapter.getCursor().moveToPosition(position);
        cursorAdapter.bindView(holder.itemView, context, cursorAdapter.getCursor());

    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.root);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent bookDetailIntent = new Intent(context, BookDetails.class);
            bookDetailIntent.putExtra(Book.TITLE, getBook(getAdapterPosition()).getTitle());
            bookDetailIntent.putExtra(Book.AUTHORS, getBook(getAdapterPosition()).getAuthors());
            bookDetailIntent.putExtra(Book.PUBLISHER, getBook(getAdapterPosition()).getPublisher());
            bookDetailIntent.putExtra(Book.DESCRIPTION, getBook(getAdapterPosition()).getDescription());
            bookDetailIntent.putExtra(Book.THUMBNAIL, getBook(getAdapterPosition()).getThumbnail());
            bookDetailIntent.putExtra(Book.BUY_LINK, getBook(getAdapterPosition()).getBuyLink());
            context.startActivity(bookDetailIntent);
        }
    }

    public Book getBook(int position) {
        return books.get(position);
    }
}