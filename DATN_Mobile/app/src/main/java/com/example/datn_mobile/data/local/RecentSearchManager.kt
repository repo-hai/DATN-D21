package com.example.datn_mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object RecentSearchManager {
    private val Context.dataStore by preferencesDataStore(name = "recent_searchs")
    private val RECENT_SEARCH_KEY = stringSetPreferencesKey("search_history")
    private const val MAX_RECENT_SEARCHES = 15

    /**
     * Save search keyword. Removes duplicate and keeps only last 15 searches.
     * Newest search is placed first in the list.
     */
    suspend fun saveSearch(context: Context, keyword: String) {
        if (keyword.isBlank()) return

        context.dataStore.edit { preferences ->
            val currentSearches = preferences[RECENT_SEARCH_KEY]?.toMutableList() ?: mutableListOf()
            // Remove if already exists to avoid duplicates
            currentSearches.remove(keyword)
            // Add to front (newest first)
            currentSearches.add(0, keyword)
            // Limit to 15 items
            if (currentSearches.size > MAX_RECENT_SEARCHES) {
                currentSearches.removeAt(currentSearches.size - 1)
            }
            // Convert back to set for storage
            preferences[RECENT_SEARCH_KEY] = currentSearches.toSet()
        }
    }

    /**
     * Get recent searches ordered by most recent first.
     * Returns maximum 15 searches.
     */
    suspend fun getRecentSearches(context: Context): List<String> {
        return context.dataStore.data.map { preferences ->
            preferences[RECENT_SEARCH_KEY]?.toList() ?: emptyList()
        }.first()
    }

    /**
     * Clear all search history
     */
    suspend fun clearSearches(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_SEARCH_KEY)
        }
    }
}