package com.camtech.books.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.camtech.books.R;
import com.camtech.books.adapters.ViewAdapter;
import com.camtech.books.data.Book;
import com.camtech.books.utils.ConnectivityReceiver;
import com.camtech.books.utils.NetworkUtils;
import com.camtech.books.utils.ConnectionListener;
import com.camtech.books.utils.OnSwipeListener;
import com.camtech.books.utils.ScrollListener;
import com.camtech.books.utils.SuggestionBuilder;
import com.camtech.books.utils.SwipeController;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SearchBooks extends AppCompatActivity {

    private final String TAG = SearchBooks.class.getSimpleName();
    private final int VOICE_REQUEST_CODE = 100;
    private ArrayList<Book> books;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ViewAdapter adapter;
    private ConnectivityReceiver connectivityReceiver;
    private String searchQuery;
    private Snackbar snackbar;
    private RelativeLayout emptyView;
    private PersistentSearchView searchView;
    private int startIndex;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int MAX_RESULTS = 10;
    private int totalItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startIndex = 0;
        setContentView(R.layout.activity_search_books);

        // Get the search query passed over from the MainActivity
        searchQuery = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view_books);
        if (searchQuery.startsWith(SuggestionBuilder.Companion.getSEARCH_AUTHOR())) {
            searchView.populateEditText(searchQuery.replace(SuggestionBuilder.Companion.getSEARCH_AUTHOR(), ""));
        } else if (searchQuery.startsWith(SuggestionBuilder.Companion.getSEARCH_SUBJECT())) {
            searchView.populateEditText(searchQuery.replace(SuggestionBuilder.Companion.getSEARCH_SUBJECT(), ""));
        } else {
            searchView.populateEditText(searchQuery);
        }
        searchView.setSuggestionBuilder(new SuggestionBuilder());
        searchView.setSearchListener(searchListener);

        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            searchView.setVoiceRecognitionDelegate(delegate);
        }

        // Receiver with listener to determine if there is internet connection
        // or if connection has dropped
        connectivityReceiver = new ConnectivityReceiver(this);
        connectivityReceiver.setConnectionListener(new ConnectionListener() {
            @Override
            public void onConnectionDropped() {
                AsyncTask booksTask = new SearchBooksTask();
                showConnectionError("No internet connection");
                if (booksTask.getStatus() == AsyncTask.Status.PENDING) {
                    booksTask.cancel(true);
                }
            }

            @Override
            public void onConnect() {
                AsyncTask booksTask = new SearchBooksTask();
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                if (booksTask.getStatus() != AsyncTask.Status.PENDING) {
                    new SearchBooksTask().execute(NetworkUtils.INSTANCE.buildUrl(searchQuery, MAX_RESULTS, startIndex));
                }
            }
        });

        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        // This is triggered when the recycler view reaches the bottom of the list
        ScrollListener scrollListener = new ScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                // Only load more items if there's internet connection
                if (connectivityReceiver.hasConnection()) {
                    new SearchBooksTask().execute(NetworkUtils.INSTANCE.buildUrl(searchQuery, MAX_RESULTS, startIndex));
                } else {
                    isLoading = true;
                    if (snackbar != null && !snackbar.isShownOrQueued())
                        showConnectionError("No internet connection");
                }
            }

            @Override
            public boolean getMIsLastPage() {
                return isLastPage;
            }

            @Override
            public boolean getMIsLoading() {
                return isLoading;
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        books = new ArrayList<>();
        adapter = new ViewAdapter(getBaseContext(), books);
        adapter.setOnItemClickListener(position -> {
            // Package the details of the current book to BookDetails activity
            Intent bookDetailIntent = new Intent(getBaseContext(), BookDetails.class);
            bookDetailIntent.putExtra(Book.TITLE, books.get(position).getTitle());
            bookDetailIntent.putExtra(Book.AUTHORS, books.get(position).getAuthors());
            bookDetailIntent.putExtra(Book.PUBLISHER, books.get(position).getPublisher());
            bookDetailIntent.putExtra(Book.DESCRIPTION, books.get(position).getDescription());
            bookDetailIntent.putExtra(Book.THUMBNAIL, books.get(position).getThumbnail());
            bookDetailIntent.putExtra(Book.BUY_LINK, books.get(position).getBuyLink());
            startActivity(bookDetailIntent);
        });
        recyclerView.setAdapter(adapter);

        SwipeController swipeController = new SwipeController(this);
        swipeController.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipeRight(int position) {
                if (books.get(position).isFavorite(getBaseContext())) {
                    books.get(position).removeFromFavorites(getBaseContext(), books.get(position).getApiId());
                    Snackbar.make(findViewById(R.id.main_layout), "Removed from favorites", Snackbar.LENGTH_SHORT).show();
                }
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onSwipeLeft(int position) {
                if (!books.get(position).isFavorite(getBaseContext())) {
                    books.get(position).addToFavorites(getBaseContext());
                    Snackbar.make(findViewById(R.id.main_layout), "Added to favorites", Snackbar.LENGTH_SHORT).show();
                }
                adapter.notifyItemChanged(position);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Triggers when the device takes too long to connect
        NetworkUtils.INSTANCE.setOnConnectionTimeoutListener(new ConnectionListener() {
            @Override
            public void onConnectionTimeout(@NonNull IOException e) {
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(100);
                }
                showConnectionError("Connection timed out");
            }
        });

        new SearchBooksTask().execute(NetworkUtils.INSTANCE.buildUrl(searchQuery, MAX_RESULTS, startIndex));

        progressBar.setVisibility(View.VISIBLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
        // Don't want to keep the task running if the activity has been exited
        AsyncTask bookTask = new SearchBooksTask();
        if (bookTask.getStatus() == AsyncTask.Status.PENDING || bookTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookTask.cancel(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // After a voice search is initiated, we need to get the results here
        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchView.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showConnectionError(String message) {
        snackbar = Snackbar.make(findViewById(R.id.main_layout), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", v ->
                        new SearchBooksTask().execute(NetworkUtils.INSTANCE.buildUrl(searchQuery, MAX_RESULTS, startIndex)));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                isLoading = false;
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorSecondaryLight));
        snackbar.show();
    }

    private PersistentSearchView.SearchListener searchListener = new PersistentSearchView.SearchListener() {
        @Override
        public void onSearchCleared() {

        }

        @Override
        public void onSearchTermChanged(String term) {

        }

        @Override
        public void onSearch(String searchTerm) {
            // Execute a new search if the query isn't the same
            if (!searchTerm.equalsIgnoreCase(searchQuery)) {
                searchQuery = searchTerm;
                adapter.clear();
                progressBar.setVisibility(View.VISIBLE);
                startIndex = 0;
                new SearchBooksTask().execute(NetworkUtils.INSTANCE.buildUrl(searchQuery, MAX_RESULTS, startIndex));

            }
        }

        @Override
        public void onSearchEditOpened() {

        }

        @Override
        public void onSearchEditClosed() {

        }

        @Override
        public boolean onSearchEditBackPressed() {
            return false;
        }

        @Override
        public void onSearchExit() {
            finish();
        }
    };

    @SuppressLint("StaticFieldLeak")
    class SearchBooksTask extends AsyncTask<URL, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Check if the user has internet connection before the task starts
            if (connectivityReceiver.hasConnection()) {
                emptyView.setVisibility(View.GONE);
                isLoading = true;
                // This shows the progress bar at the bottom of the recycler view
                adapter.showLoadingFooter();
            } else {
                // Bail early if there's no connection
                cancel(true);
                showConnectionError("No internet connection");
            }
        }

        @Override
        protected Void doInBackground(URL... urls) {
            URL searchQuery = urls[0];
            String JSONResponse;
            try {
                // Make the API request from the built URL
                JSONResponse = NetworkUtils.INSTANCE.makeHttpRequest(searchQuery);
                JSONObject baseJSONObject = new JSONObject(JSONResponse);

                // The start index will always be zero for a new book so
                // we can store the total results once per new search
                if (startIndex == 0) {
                    totalItems = baseJSONObject.getInt("totalItems");
                    Log.i(TAG, "Total items:" + totalItems);
                }

                // Check if there are any books that matched the user's search
                if (baseJSONObject.has("items")) {
                    JSONArray items = baseJSONObject.getJSONArray("items");
                    Log.i(TAG, "doInBackground... items.length() == " + items.length());
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject volumeInfo = items.getJSONObject(i).getJSONObject(Book.VOLUME_INFO);
                        JSONObject saleInfo = items.getJSONObject(i).getJSONObject(Book.SALE_INFO);

                        String title = volumeInfo.getString(Book.TITLE);
                        String authors[] = null;
                        String publisher = null;
                        String description = null;
                        Bitmap smallThumbnail = null;
                        Bitmap thumbnail = null;
                        String buyLink = null;

                        // Since every book has a different API ID, we can use this
                        // as a unique identifier to make sure no duplicate books
                        // are added to the database
                        String apiId = items.getJSONObject(i).getString(Book.API_ID);

                        // Not every book has these details so wee need to check
                        if (volumeInfo.has(Book.AUTHORS)) {
                            JSONArray jsonAuthorArray = volumeInfo.getJSONArray(Book.AUTHORS);
                            authors = new String[jsonAuthorArray.length()];
                            for (int j = 0; j < jsonAuthorArray.length(); j++) {
                                authors[j] = jsonAuthorArray.getString(j);
                            }
                        }

                        if (volumeInfo.has(Book.PUBLISHER)) {
                            publisher = volumeInfo.getString(Book.PUBLISHER);
                        }

                        if (volumeInfo.has(Book.DESCRIPTION)) {
                            description = volumeInfo.getString(Book.DESCRIPTION);
                        }

                        if (volumeInfo.has(Book.IMAGE_LINKS)) {
                            URL smallThumbnailURL = new URL(volumeInfo.getJSONObject(Book.IMAGE_LINKS).getString(Book.SMALL_THUMBNAIL));
                            URL thumbnailURL = new URL(volumeInfo.getJSONObject(Book.IMAGE_LINKS).getString(Book.THUMBNAIL));
                            smallThumbnail = BitmapFactory.decodeStream(smallThumbnailURL.openConnection().getInputStream());
                            thumbnail = BitmapFactory.decodeStream(thumbnailURL.openConnection().getInputStream());
                        }

                        if (saleInfo.has(Book.BUY_LINK)) {
                            buyLink = saleInfo.getString(Book.BUY_LINK);
                        }

                        books.add(new Book(
                                title,
                                authors,
                                publisher,
                                description,
                                smallThumbnail,
                                thumbnail,
                                buyLink,
                                apiId));
                        publishProgress();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... aVoid) {
            // This adds books to recycler view one by one, this way,
            // the user doesn't have to wait until every book
            // has loaded to see results
            progressBar.setVisibility(View.GONE);

            // Swap the adapter to make sure the list starts off
            // from where it last left off
            recyclerView.swapAdapter(adapter, false);
            if (books.size() >= totalItems) {
                // If the number of books in the array is the same as or greater
                // than the number of items in the JSON array, then we don't
                // need to load anymore books
                isLastPage = true;
                adapter.removeLoadingFooter();
            }
            // Since the google books API can't show every result, we just need
            // to increment the starting index on every successful book load
            startIndex++;
            super.onProgressUpdate(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            adapter.removeLoadingFooter();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isLoading = false;
            // No books were found so we show the empty view
            if (books.size() == 0) {
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }
}