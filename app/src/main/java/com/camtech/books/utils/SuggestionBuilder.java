package com.camtech.books.utils;

import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class used to add suggestions to the search bar
 * after a book is searched
 * */
public class SuggestionBuilder implements SearchSuggestionsBuilder {

    private List<SearchItem> suggestions;
    private final String TAG = SuggestionBuilder.class.getSimpleName();
    public static final String SEARCH_AUTHOR = "author:";
    public static final String SEARCH_SUBJECT = "subject:";

    public SuggestionBuilder() {
        suggestions = new ArrayList<>();
    }

    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {
        return suggestions;

    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {
        List<SearchItem> searchSuggestions = new ArrayList<>();
        SearchItem searchAuthor = new SearchItem(
                "Search author: " + query,
                SEARCH_AUTHOR + query,
                SearchItem.TYPE_SEARCH_ITEM_SUGGESTION);

        SearchItem searchSubject = new SearchItem(
                "Search subject: " + query,
                SEARCH_SUBJECT + query,
                SearchItem.TYPE_SEARCH_ITEM_SUGGESTION);

        searchSuggestions.add(searchAuthor);
        searchSuggestions.add(searchSubject);
        // Match the current search query with the stored list based on the
        // the first letter or the entire word
        for (SearchItem searchItems : suggestions) {
            if (searchItems.getValue().startsWith(query) || searchItems.getValue().contains(query)) {
                searchSuggestions.add(searchItems);
            }
        }
        return searchSuggestions;
    }

    public void addSuggestion(String word) {
        if (word.startsWith(SEARCH_AUTHOR)) {
            word = word.replace(SEARCH_AUTHOR, "");
        } else if (word.startsWith(SEARCH_SUBJECT)) {
            word = word.replace(SEARCH_SUBJECT, "");
        }
        SearchItem searchItem = new SearchItem(
                word,
                word,
                SearchItem.TYPE_SEARCH_ITEM_HISTORY);
        // Check if the word already exists in the list before it's added
        // to prevent duplicates
        if (!suggestionExists(word)) {
            suggestions.add(searchItem);
        }
    }

    private boolean suggestionExists(String word) {
        for (int i = 0; i < suggestions.size(); i++) {
            if (suggestions.get(i).getValue().equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }
}