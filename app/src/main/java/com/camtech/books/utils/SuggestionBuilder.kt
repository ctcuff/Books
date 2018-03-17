package com.camtech.books.utils

import org.cryse.widget.persistentsearch.SearchItem
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder

import java.util.ArrayList

/**
 * Class used to add suggestions to the search bar
 * after a book is searched
 */
class SuggestionBuilder : SearchSuggestionsBuilder {

    private val suggestions: MutableList<SearchItem>

    init {
        suggestions = ArrayList()
    }

    companion object {
        val SEARCH_AUTHOR = "author:"
        val SEARCH_SUBJECT = "subject:"
    }

    override fun buildEmptySearchSuggestion(maxCount: Int): Collection<SearchItem> {
        return suggestions

    }

    override fun buildSearchSuggestion(maxCount: Int, query: String): Collection<SearchItem> {
        val searchSuggestions = ArrayList<SearchItem>()
        val searchAuthor = SearchItem(
                "Search author: " + query,
                SEARCH_AUTHOR + query,
                SearchItem.TYPE_SEARCH_ITEM_SUGGESTION)

        val searchSubject = SearchItem(
                "Search subject: " + query,
                SEARCH_SUBJECT + query,
                SearchItem.TYPE_SEARCH_ITEM_SUGGESTION)

        searchSuggestions.add(searchAuthor)
        searchSuggestions.add(searchSubject)
        // Match the current search query with the stored list based on the
        // the first letter or the entire word
        suggestions.filterTo(searchSuggestions) { it.value.startsWith(query) || it.value.contains(query) }
        return searchSuggestions
    }

    fun addSuggestion(_word: String) {
        var word = _word
        if (word.startsWith(SEARCH_AUTHOR)) {
            word = word.replace(SEARCH_AUTHOR, "")
        } else if (word.startsWith(SEARCH_SUBJECT)) {
            word = word.replace(SEARCH_SUBJECT, "")
        }
        val searchItem = SearchItem(word, word, SearchItem.TYPE_SEARCH_ITEM_HISTORY)
        // Check if the word already exists in the list before it's added
        // to prevent duplicates
        if (!suggestionExists(word)) {
            suggestions.add(searchItem)
        }
    }

    private fun suggestionExists(word: String): Boolean =
            suggestions.indices.any { suggestions[it].value.equals(word, ignoreCase = true) }
}