package com.camtech.books.adapters

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import com.camtech.books.R
import com.camtech.books.activities.BookDetails
import com.camtech.books.data.Book
import com.camtech.books.database.DBContract
import java.util.*

/**
 * RecyclerView adapter used in [com.camtech.books.activities.MainActivity]
 *
 *  A typical [CursorAdapter] doesn't work with a RecyclerView
 *  so we need to wrap the cursor adapter inside a RecyclerView adapter
 *
 */
class CursorViewAdapter(private val context: Context, c: Cursor?) : RecyclerView.Adapter<CursorViewAdapter.ViewHolder>() {


    private var cursorAdapter: CursorAdapter
    private var books: ArrayList<Book> = ArrayList()


    init {
        cursorAdapter = object : CursorAdapter(context, c, 0) {
            override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
                return LayoutInflater.from(context).inflate(R.layout._list_books, parent, false)
            }

            override fun bindView(view: View, context: Context, cursor: Cursor) {
                val tvTitle: TextView = view.findViewById(R.id.book_title)
                val tvAuthor: TextView = view.findViewById(R.id.book_author)
                val tvPublisher: TextView = view.findViewById(R.id.book_publisher)
                val ivThumbnail: ImageView = view.findViewById(R.id.book_thumbnail)
                val ivFavorite: ImageView = view.findViewById(R.id.book_favorite)

                // Extract the properties of the book from the cursor
                val titleIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_TITLE)
                val authorIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_AUTHORS)
                val publisherIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_PUBLISHER)
                val smallThumbnailIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_SMALL_THUMBNAIL)
                val descriptionIndex = cursor.getColumnIndexOrThrow(DBContract.BookEntry.COLUMN_DESCRIPTION)
                val thumbnailIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_THUMBNAIL)
                val buyLinkIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_BUY_LINK)
                val apiIdIndex = cursor.getColumnIndex(DBContract.BookEntry.COLUMN_API_ID)

                val bookTitle = cursor.getString(titleIndex)
                val bookAuthor = cursor.getString(authorIndex).replace("[", "").replace("]", "")
                val bookPublisher = cursor.getString(publisherIndex)
                val smallThumbnail = Book.byteArrayToBitmap(cursor.getBlob(smallThumbnailIndex))
                val thumbnail = Book.byteArrayToBitmap(cursor.getBlob(thumbnailIndex))
                val description = cursor.getString(descriptionIndex)
                val buyLink = cursor.getString(buyLinkIndex)
                val apiId = cursor.getString(apiIdIndex)

                tvTitle.text = bookTitle
                tvAuthor.text = context.getString(R.string.book_detail_authors, bookAuthor)
                tvPublisher.text = context.getString(R.string.book_detail_publisher, bookPublisher)
                ivThumbnail.setImageBitmap(smallThumbnail)
                ivFavorite.visibility = View.VISIBLE

                books.add(Book(
                        bookTitle,
                        arrayOf(bookAuthor),
                        bookPublisher,
                        description,
                        smallThumbnail,
                        thumbnail,
                        buyLink,
                        apiId))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        // Passing the inflater job to the cursor adapter
        val v = cursorAdapter.newView(context, cursorAdapter.cursor, parent)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        cursorAdapter.cursor.moveToPosition(position)
        cursorAdapter.bindView(holder?.itemView, context, cursorAdapter.cursor)
    }

    override fun getItemCount(): Int = cursorAdapter.count

    fun getBook(position: Int): Book = books[position]

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var cardView: CardView = itemView.findViewById(R.id.root)

        init {
            cardView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val bookDetailIntent = Intent(context, BookDetails::class.java)
            bookDetailIntent.putExtra(Book.TITLE, getBook(adapterPosition).title)
            bookDetailIntent.putExtra(Book.AUTHORS, getBook(adapterPosition).authors)
            bookDetailIntent.putExtra(Book.PUBLISHER, getBook(adapterPosition).publisher)
            bookDetailIntent.putExtra(Book.DESCRIPTION, getBook(adapterPosition).description)
            bookDetailIntent.putExtra(Book.THUMBNAIL, getBook(adapterPosition).thumbnail)
            bookDetailIntent.putExtra(Book.BUY_LINK, getBook(adapterPosition).buyLink)
            context.startActivity(bookDetailIntent)
        }
    }
}