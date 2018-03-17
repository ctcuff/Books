package com.camtech.books.activities;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import com.camtech.books.CursorViewAdapter;
import com.camtech.books.R;
import com.camtech.books.database.DBContract;
import com.camtech.books.database.DBHelper;
import com.camtech.books.utils.SuggestionBuilder;
import com.camtech.books.utils.SwipeController;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = MainActivity.class.getSimpleName();
    final int VOICE_REQUEST_CODE = 100;
    PersistentSearchView searchView;
    SuggestionBuilder suggestionBuilder;
    RecyclerView recyclerView;
    DBHelper dbHelper;
    CursorViewAdapter adapter;
    Cursor cursor;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        suggestionBuilder = new SuggestionBuilder();
        searchView = findViewById(R.id.search_view);
        searchView.setSearchListener(searchListener);
        searchView.setSuggestionBuilder(suggestionBuilder);
        // Adds the voice search icon to the search view
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            searchView.setVoiceRecognitionDelegate(delegate);
        }
        searchView.setHomeButtonListener(new PersistentSearchView.HomeButtonListener() {
            @Override
            public void onHomeButtonClick() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        dbHelper = new DBHelper(this);
        cursor = queryDatabase();
        adapter = new CursorViewAdapter(this, cursor);
        SwipeController swipeController = new SwipeController(this);
        // Make sure only the delete swipe option is shown
        swipeController.setAddAndDelete(false);
        swipeController.setOnSwipeListener(position -> {
            adapter.getBook(position).removeFromFavorites(getBaseContext(), adapter.getBook(position).getApiId());
            reloadRecyclerView();
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView.isSearching()) {
            searchView.closeSearch();
        }
        reloadRecyclerView();
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearching()) {
            searchView.closeSearch();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (!searchView.isSearching() && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy...");
        if (cursor != null) cursor.close();
        if (dbHelper != null) dbHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchView.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private Cursor queryDatabase() {
        return dbHelper.getReadableDatabase().query(
                DBContract.BookEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private void reloadRecyclerView() {
        cursor = queryDatabase();
        adapter = new CursorViewAdapter(this, cursor);
        recyclerView.setAdapter(adapter);

    }

    private PersistentSearchView.SearchListener searchListener = new PersistentSearchView.SearchListener() {
        @Override
        public void onSearchCleared() {

        }

        @Override
        public void onSearchTermChanged(String term) {

        }

        @Override
        public void onSearch(String query) {
            // Get the search query and pass it to the SearchBooks activity
            Intent searchIntent = new Intent(MainActivity.this, SearchBooks.class);
            searchIntent.putExtra(Intent.EXTRA_TEXT, query);
            startActivity(searchIntent);
            suggestionBuilder.addSuggestion(query);
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

        }
    };
}
