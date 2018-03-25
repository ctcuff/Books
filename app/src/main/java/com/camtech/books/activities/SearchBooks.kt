package com.camtech.books.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Vibrator
import android.speech.RecognizerIntent
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.camtech.books.R
import com.camtech.books.adapters.ViewAdapter
import com.camtech.books.data.Book
import com.camtech.books.utils.*
import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate
import org.cryse.widget.persistentsearch.PersistentSearchView
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*

class SearchBooks : AppCompatActivity() {

    private val TAG = SearchBooks::class.java.simpleName
    private val VOICE_REQUEST_CODE = 100
    private lateinit var books: ArrayList<Book>
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewAdapter
    private lateinit var connectivityReceiver: ConnectivityReceiver
    private lateinit var searchQuery: String
    private lateinit var emptyView: RelativeLayout
    private lateinit var searchView: PersistentSearchView
    private var snackbar: Snackbar? = null
    private var startIndex: Int = 0
    private val MAX_RESULTS = 10
    private var totalItems: Int = 0
    private var isLoading = false
    private var isLastPage = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startIndex = 0
        setContentView(R.layout.activity_search_books)

        // Get the search query passed over from the MainActivity
        searchQuery = intent.getStringExtra(Intent.EXTRA_TEXT)

        emptyView = findViewById(R.id.empty_view)
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById(R.id.recycler_view)
        searchView = findViewById(R.id.search_view_books)
        when {
            searchQuery.startsWith(SuggestionBuilder.SEARCH_AUTHOR) ->
                searchView.populateEditText(searchQuery.replace(SuggestionBuilder.SEARCH_AUTHOR, ""))
            searchQuery.startsWith(SuggestionBuilder.SEARCH_SUBJECT) ->
                searchView.populateEditText(searchQuery.replace(SuggestionBuilder.SEARCH_SUBJECT, ""))
            else -> searchView.populateEditText(searchQuery)
        }
        searchView.setSuggestionBuilder(SuggestionBuilder())
        searchView.setSearchListener(searchListener)

        val delegate = DefaultVoiceRecognizerDelegate(this, VOICE_REQUEST_CODE)
        if (delegate.isVoiceRecognitionAvailable) {
            searchView.setVoiceRecognitionDelegate(delegate)
        }

        // Receiver with listener to determine if there is internet connection
        // or if connection has dropped
        connectivityReceiver = ConnectivityReceiver(this)
        connectivityReceiver.setConnectionListener(object : ConnectionListener() {
            override fun onConnectionDropped() {
                val booksTask = SearchBooksTask()
                showConnectionError("No internet connection")
                if (booksTask.status == AsyncTask.Status.PENDING) {
                    booksTask.cancel(true)
                }
            }

            override fun onConnect() {
                val booksTask = SearchBooksTask()
                if (snackbar != null && snackbar!!.isShown) {
                    snackbar!!.dismiss()
                }
                if (booksTask.status != AsyncTask.Status.PENDING) {
                    SearchBooksTask().execute(NetworkUtils.buildUrl(searchQuery, MAX_RESULTS, startIndex))
                }
            }
        })

        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        // This is triggered when the recycler view reaches the bottom of the list
        val scrollListener = object : ScrollListener(manager) {

            override val mIsLastPage: Boolean
                get() = isLastPage

            override val mIsLoading: Boolean
                get() = isLoading

            override fun loadMoreItems() {
                // Only load more items if there's internet connection
                if (connectivityReceiver.hasConnection()) {
                    SearchBooksTask().execute(NetworkUtils.buildUrl(searchQuery, MAX_RESULTS, startIndex))
                } else {
                    isLoading = true
                    if (snackbar != null && !snackbar!!.isShownOrQueued)
                        showConnectionError("No internet connection")
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        books = ArrayList()
        adapter = ViewAdapter(baseContext, books)
        adapter.setOnItemClickListener(object: ViewAdapter.OnItemClickListener {
            override fun onItemClicked(position: Int) {
                // Package the details of the current book to BookDetails activity
                val bookDetailIntent = Intent(baseContext, BookDetails::class.java)
                bookDetailIntent.putExtra(Book.TITLE, books[position].title)
                bookDetailIntent.putExtra(Book.AUTHORS, books[position].authors)
                bookDetailIntent.putExtra(Book.PUBLISHER, books[position].publisher)
                bookDetailIntent.putExtra(Book.DESCRIPTION, books[position].description)
                bookDetailIntent.putExtra(Book.THUMBNAIL, books[position].thumbnail)
                bookDetailIntent.putExtra(Book.BUY_LINK, books[position].buyLink)
                startActivity(bookDetailIntent)
            }
        })
        recyclerView.adapter = adapter

        val swipeController = SwipeController(this)
        swipeController.setOnSwipeListener(object : OnSwipeListener() {
            override fun onSwipeRight(position: Int) {
                if (books[position].isFavorite(baseContext)) {
                    books[position].removeFromFavorites(baseContext, books[position].apiId)
                    Snackbar.make(findViewById(R.id.main_layout), "Removed from favorites", Snackbar.LENGTH_SHORT).show()
                }
                adapter.notifyItemChanged(position)
            }

            override fun onSwipeLeft(position: Int) {
                if (!books[position].isFavorite(baseContext)) {
                    books[position].addToFavorites(baseContext)
                    Snackbar.make(findViewById(R.id.main_layout), "Added to favorites", Snackbar.LENGTH_SHORT).show()
                }
                adapter.notifyItemChanged(position)
            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Triggers when the device takes too long to connect
        NetworkUtils.setOnConnectionTimeoutListener(object : ConnectionListener() {
            override fun onConnectionTimeout(e: IOException) {
                val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v.vibrate(100)
                showConnectionError("Connection timed out")
            }
        })

        SearchBooksTask().execute(NetworkUtils.buildUrl(searchQuery, MAX_RESULTS, startIndex))

        progressBar.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
        // Don't want to keep the task running if the activity has been exited
        val bookTask = SearchBooksTask()
        if (bookTask.status == AsyncTask.Status.PENDING || bookTask.status == AsyncTask.Status.RUNNING) {
            bookTask.cancel(true)
        }
    }

    override fun onBackPressed() {
        val bookTask = SearchBooksTask()
        if (bookTask.status == AsyncTask.Status.PENDING || bookTask.status == AsyncTask.Status.RUNNING) {
            bookTask.cancel(true)
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // After a voice search is initiated, we need to get the results here
        if (requestCode == VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            searchView.populateEditText(matches)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showConnectionError(message: String) {
        snackbar = Snackbar.make(findViewById(R.id.main_layout), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY") { _ -> SearchBooksTask().execute(NetworkUtils.buildUrl(searchQuery, MAX_RESULTS, startIndex)) }
        snackbar!!.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                isLoading = false
            }
        })
        snackbar!!.setActionTextColor(ContextCompat.getColor(this, R.color.colorSecondaryLight))
        snackbar!!.show()
    }

    private val searchListener = object : PersistentSearchView.SearchListener {
        override fun onSearchCleared() {

        }

        override fun onSearchTermChanged(term: String) {

        }

        override fun onSearch(searchTerm: String) {
            // Execute a new search if the query isn't the same
            if (!searchTerm.equals(searchQuery, ignoreCase = true)) {
                searchQuery = searchTerm
                adapter.clear()
                progressBar.visibility = View.VISIBLE
                startIndex = 0
                SearchBooksTask().execute(NetworkUtils.buildUrl(searchQuery, MAX_RESULTS, startIndex))

            }
        }

        override fun onSearchEditOpened() {

        }

        override fun onSearchEditClosed() {

        }

        override fun onSearchEditBackPressed(): Boolean {
            return false
        }

        override fun onSearchExit() {
            onBackPressed()
        }
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class SearchBooksTask : AsyncTask<URL, Book, Int>() {

        override fun onPreExecute() {
            super.onPreExecute()
            // Check if the user has internet connection before the task starts
            if (connectivityReceiver.hasConnection()) {
                emptyView.visibility = View.GONE
                isLoading = true
                // This shows the progress bar at the bottom of the recycler view
                adapter.showLoadingFooter()

            } else {
                // Bail early if there's no connection
                cancel(true)
                showConnectionError("No internet connection")
            }
        }

        override fun doInBackground(vararg urls: URL): Int {
            val searchQuery = urls[0]
            val JSONResponse: String
            try {
                // Make the API request from the built URL
                JSONResponse = NetworkUtils.makeHttpRequest(searchQuery)
                val baseJSONObject = JSONObject(JSONResponse)

                // The start index will always be zero for a new book so
                // we can store the total results once per new search
                if (startIndex == 0) {
                    totalItems = baseJSONObject.getInt("totalItems")
                    Log.i(TAG, "Total items:" + totalItems)
                }

                // Check if there are any books that matched the user's search
                if (baseJSONObject.has("items")) {
                    val items = baseJSONObject.getJSONArray("items")
                    Log.i(TAG, "doInBackground... items.length() == " + items.length())
                    for (i in 0 until items.length()) {
                        val volumeInfo = items.getJSONObject(i).getJSONObject(Book.VOLUME_INFO)
                        val saleInfo = items.getJSONObject(i).getJSONObject(Book.SALE_INFO)

                        val title = volumeInfo.getString(Book.TITLE)
                        var authors: Array<String>? = null
                        var publisher: String? = null
                        var description: String? = null
                        var smallThumbnail: Bitmap? = null
                        var thumbnail: Bitmap? = null
                        var buyLink: String? = null

                        // Since every book has a different API ID, we can use this
                        // as a unique identifier to make sure no duplicate books
                        // are added to the database
                        val apiId = items.getJSONObject(i).getString(Book.API_ID)

                        // Not every book has these details so wee need to check
                        if (volumeInfo.has(Book.AUTHORS)) {
                            val jsonAuthorArray = volumeInfo.getJSONArray(Book.AUTHORS)
                            authors = Array(jsonAuthorArray.length(), { _ -> "" })
                            for (j in 0 until jsonAuthorArray.length()) {
                                authors[j] = jsonAuthorArray.getString(j)
                            }
                        }

                        if (volumeInfo.has(Book.PUBLISHER)) {
                            publisher = volumeInfo.getString(Book.PUBLISHER)
                        }

                        if (volumeInfo.has(Book.DESCRIPTION)) {
                            description = volumeInfo.getString(Book.DESCRIPTION)
                        }

                        if (volumeInfo.has(Book.IMAGE_LINKS)) {
                            val smallThumbnailURL = URL(volumeInfo.getJSONObject(Book.IMAGE_LINKS).getString(Book.SMALL_THUMBNAIL))
                            val thumbnailURL = URL(volumeInfo.getJSONObject(Book.IMAGE_LINKS).getString(Book.THUMBNAIL))
                            smallThumbnail = BitmapFactory.decodeStream(smallThumbnailURL.openConnection().getInputStream())
                            thumbnail = BitmapFactory.decodeStream(thumbnailURL.openConnection().getInputStream())
                        }

                        if (saleInfo.has(Book.BUY_LINK)) {
                            buyLink = saleInfo.getString(Book.BUY_LINK)
                        }

                        books.add(Book(
                                title,
                                authors,
                                publisher,
                                description,
                                smallThumbnail,
                                thumbnail,
                                buyLink,
                                apiId))

                        publishProgress(books[i])
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return 0
        }

        /**
         * This adds books to recycler view one by one, this way,
         * the user doesn't have to wait until every book
         * has loaded to see results
         * */
        override fun onProgressUpdate(vararg values: Book) {
            progressBar.visibility = View.GONE
            adapter.addBook(books[startIndex])
            // Swap the adapter to make sure the list starts off
            // from where it last left off
            recyclerView.swapAdapter(adapter, false)
            if (books.size >= totalItems) {
                // If the number of books in the array is the same as or greater
                // than the number of items in the JSON array, then we don't
                // need to load anymore books
                isLastPage = true
                adapter.removeLoadingFooter()
            }
            // Since the google books API can't show every result, we just need
            // to increment the starting index on every successful book load
            startIndex++
            super.onProgressUpdate(*values)
        }


        override fun onCancelled() {
            super.onCancelled()
            adapter.removeLoadingFooter()
        }

        override fun onPostExecute(result: Int) {
            super.onPostExecute(result)
            isLoading = false
            // No books were found so we show the empty view
            if (books.size == 0) {
                progressBar.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
    }
}