package com.camtech.books.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.camtech.books.R
import com.camtech.books.data.Book
import java.util.*

class BookDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        val bookDetailIntent = intent

        val title = bookDetailIntent.getStringExtra(Book.TITLE)
        val authors = Arrays.toString(bookDetailIntent.getStringArrayExtra(Book.AUTHORS))
                .replace("[", "").replace("]", "")
        val publisher = bookDetailIntent.getStringExtra(Book.PUBLISHER)
        val thumbnail = bookDetailIntent.getParcelableExtra<Bitmap>(Book.THUMBNAIL)
        val description = bookDetailIntent.getStringExtra(Book.DESCRIPTION)
        val buyLink = bookDetailIntent.getStringExtra(Book.BUY_LINK)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title

        val tvBookTitle: TextView = findViewById(R.id.book_detail_title)
        val tvBookAuthor: TextView = findViewById(R.id.book_detail_author)
        val tvBookPublisher: TextView = findViewById(R.id.book_detail_publisher)
        val ivBookImage: ImageView = findViewById(R.id.book_detail_image)
        val tvBookDescription: TextView = findViewById(R.id.book_detail_description)
        val btBuyLink: Button = findViewById(R.id.book_detail_buy_link)

        tvBookTitle.text = title
        tvBookAuthor.text = getString(R.string.book_detail_authors, authors)
        ivBookImage.setImageBitmap(thumbnail)
        tvBookPublisher.text = getString(R.string.book_detail_publisher, publisher)
        if (description != null) {
            tvBookDescription.text = getString(R.string.book_detail_description, description)
        } else {
            tvBookDescription.visibility = View.GONE
        }

        if (buyLink != null) {
            btBuyLink.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(buyLink))) }
        } else {
            btBuyLink.visibility = View.GONE
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}