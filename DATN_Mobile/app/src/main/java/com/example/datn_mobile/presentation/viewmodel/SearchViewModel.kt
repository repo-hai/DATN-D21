package com.example.datn_mobile.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn_mobile.data.local.RecentSearchManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun loadRecentSearches(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val searches = RecentSearchManager.getRecentSearches(context)
                _recentSearches.value = searches
            } catch (_: Exception) {
                _recentSearches.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveSearchKeyword(context: Context, keyword: String) {
        viewModelScope.launch {
            RecentSearchManager.saveSearch(context, keyword)
            // Reload recent searches
            loadRecentSearches(context)
        }
    }

    fun clearAllSearches(context: Context) {
        viewModelScope.launch {
            RecentSearchManager.clearSearches(context)
            _recentSearches.value = emptyList()
        }
    }

    fun removeRecentSearch(context: Context, keyword: String) {
        viewModelScope.launch {
            // Get current searches, remove this one, and save
            val currentSearches = _recentSearches.value.toMutableList()
            currentSearches.remove(keyword)
            // Save back by converting to set format (DataStore requirement)
            RecentSearchManager.clearSearches(context)
            for (search in currentSearches) {
                RecentSearchManager.saveSearch(context, search)
            }
            _recentSearches.value = currentSearches
        }
    }
}

