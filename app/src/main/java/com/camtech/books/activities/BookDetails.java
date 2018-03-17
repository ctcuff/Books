package com.camtech.books.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.camtech.books.R;
import com.camtech.books.data.Book;

import java.util.Arrays;

public class BookDetails extends AppCompatActivity {

    TextView tvBookTitle;
    TextView tvBookAuthor;
    TextView tvBookPublisher;
    TextView tvBookDescription;
    Button btBuyLink;
    ImageView ivBookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent bookDetailIntent = getIntent();

        String title = bookDetailIntent.getStringExtra(Book.TITLE);
        String authors = Arrays.toString(bookDetailIntent.getStringArrayExtra(Book.AUTHORS))
                .replace("[", "").replace("]", "");
        String publisher = bookDetailIntent.getStringExtra(Book.PUBLISHER);
        Bitmap thumbnail = bookDetailIntent.getParcelableExtra(Book.THUMBNAIL);
        String description = bookDetailIntent.getStringExtra(Book.DESCRIPTION);
        String buyLink = bookDetailIntent.getStringExtra(Book.BUY_LINK);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }

        tvBookTitle = findViewById(R.id.book_detail_title);
        tvBookAuthor = findViewById(R.id.book_detail_author);
        tvBookPublisher = findViewById(R.id.book_detail_publisher);
        ivBookImage = findViewById(R.id.book_detail_image);
        tvBookDescription = findViewById(R.id.book_detail_description);
        btBuyLink = findViewById(R.id.book_detail_buy_link);

        tvBookTitle.setText(title);
        tvBookAuthor.setText(getString(R.string.book_detail_authors, authors));
        ivBookImage.setImageBitmap(thumbnail);
        tvBookPublisher.setText(getString(R.string.book_detail_publisher, publisher));
        if (description != null) {
            tvBookDescription.setText(getString(R.string.book_detail_description, description));
        } else {
            tvBookDescription.setVisibility(View.GONE);
        }

        if (buyLink != null) {
            btBuyLink.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(buyLink))));
        } else {
            btBuyLink.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}