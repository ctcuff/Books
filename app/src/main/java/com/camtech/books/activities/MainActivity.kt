package com.camtech.books.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MenuItem
import com.camtech.books.R
import com.camtech.books.adapters.CursorViewAdapter
import com.camtech.books.database.DBContract
import com.camtech.books.database.DBHelper
import com.camtech.books.utils.OnSwipeListener
import com.camtech.books.utils.SuggestionBuilder
import com.camtech.books.utils.SwipeController
import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate
import org.cryse.widget.persistentsearch.PersistentSearchView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val VOICE_REQUEST_CODE = 100
    private lateinit var searchView: PersistentSearchView
    private lateinit var suggestionBuilder: SuggestionBuilder
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CursorViewAdapter
    private lateinit var drawerLayout: DrawerLayout

    private var dbHelper: DBHelper? = null
    private var cursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        searchView = findViewById(R.id.search_view)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        suggestionBuilder = SuggestionBuilder()

        searchView.setSearchListener(searchListener)
        searchView.setSuggestionBuilder(suggestionBuilder)
        // Adds the voice search icon to the search view
        val delegate = DefaultVoiceRecognizerDelegate(this, VOICE_REQUEST_CODE)
        if (delegate.isVoiceRecognitionAvailable) {
            searchView.setVoiceRecognitionDelegate(delegate)
        }
        searchView.setHomeButtonListener { drawerLayout.openDrawer(GravityCompat.START) }

        dbHelper = DBHelper(this)
        cursor = queryDatabase()
        adapter = CursorViewAdapter(this, cursor)
        val swipeController = SwipeController(this)
        // Make sure only the delete swipe option is shown
        swipeController.setAddAndDelete(false)
        swipeController.setOnSwipeListener(object : OnSwipeListener() {
            // We set the on swipe listener to use the
            // same action for both swipe directions
            override fun onSwipe(position: Int) {
                adapter.getBook(position).removeFromFavorites(baseContext, adapter.getBook(position).apiId)
                reloadRecyclerView()
                Snackbar.make(drawerLayout, "Removed from favorites", Snackbar.LENGTH_SHORT).show()
            }
        })

        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        if (searchView.isSearching) {
            searchView.closeSearch()
        }
        reloadRecyclerView()
    }

    override fun onBackPressed() {
        if (searchView.isSearching) {
            searchView.closeSearch()
            return
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        if (!searchView.isSearching && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cursor?.close()
        dbHelper?.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            searchView.populateEditText(matches)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    private fun queryDatabase(): Cursor? {
        return dbHelper?.readableDatabase?.query(
                DBContract.BookEntry.TABLE_NAME, null, null, null, null, null, null)
    }

    private fun reloadRecyclerView() {
        cursor = queryDatabase()
        adapter = CursorViewAdapter(this, cursor)
        recyclerView.adapter = adapter

    }

    private val searchListener = object : PersistentSearchView.SearchListener {
        override fun onSearchCleared() {

        }

        override fun onSearchTermChanged(term: String) {

        }

        override fun onSearch(query: String) {
            // Get the search query and pass it to the SearchBooks activity
            val searchIntent = Intent(this@MainActivity, SearchBooks::class.java)
            searchIntent.putExtra(Intent.EXTRA_TEXT, query)
            startActivity(searchIntent)
            suggestionBuilder.addSuggestion(query)
        }

        override fun onSearchEditOpened() {

        }

        override fun onSearchEditClosed() {

        }

        override fun onSearchEditBackPressed(): Boolean {
            return false
        }

        override fun onSearchExit() {

        }
    }
}