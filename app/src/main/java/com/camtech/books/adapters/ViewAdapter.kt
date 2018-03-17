package com.camtech.books.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.camtech.books.R
import com.camtech.books.data.Book

import java.util.ArrayList
import java.util.Arrays

/**
 * RecyclerView adapter used in [com.camtech.books.activities.SearchBooks]
 */
class ViewAdapter(private val context: Context,
                  private val books: ArrayList<Book>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private var isLoadingAdded = false
    private val ITEM = 0
    private val LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent?.context)
        when (viewType) {
            ITEM -> viewHolder = BookViewHolder(inflater.inflate(R.layout._list_books, parent, false))
            LOADING -> viewHolder = LoadingViewHolder(inflater.inflate(R.layout.item_progress, parent, false))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val bvh = holder as BookViewHolder
                bvh.tvTitle.text = context.getString(R.string.book_title, books[position].getTitle())
                bvh.tvAuthor.text = context.getString(R.string.book_author,
                        Arrays.toString(books[position].getAuthors()).replace("[", "").replace("]", ""))
                bvh.tvPublisher.text = context.getString(R.string.book_publisher, books[position].getPublisher())
                bvh.ivSmallThumbnail.setImageBitmap(books[position].getSmallThumbnail())
                // If the book has been added to favorites (A.K.A, is in the database) so show the bookmark icon
                if (books[position].isFavorite(context)) bvh.ivFavorite.visibility = View.VISIBLE
                else bvh.ivFavorite.visibility = View.GONE
            }
            LOADING -> {
            }
        }
    }

    override fun getItemCount(): Int = books.size

    override fun getItemViewType(position: Int): Int =
            if (position == books.size - 1 && isLoadingAdded) LOADING else ITEM

    fun clear() {
        books.clear()
        notifyItemRangeChanged(0, books.size)
    }

    fun showLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    internal inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvTitle: TextView = itemView.findViewById(R.id.book_title)
        var tvAuthor: TextView = itemView.findViewById(R.id.book_author)
        var tvPublisher: TextView = itemView.findViewById(R.id.book_publisher)
        var ivSmallThumbnail: ImageView = itemView.findViewById(R.id.book_thumbnail)
        var ivFavorite: ImageView = itemView.findViewById(R.id.book_favorite)
        var cardView: CardView = itemView.findViewById(R.id.root)

        init {
            cardView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            onItemClickListener?.onItemClicked(adapterPosition)
        }
    }

    internal inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
