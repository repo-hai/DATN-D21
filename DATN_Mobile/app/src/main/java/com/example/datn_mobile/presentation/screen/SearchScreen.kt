package com.example.datn_mobile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datn_mobile.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onSearchSubmit: (query: String) -> Unit,
    onRecentSearchClick: (keyword: String) -> Unit
) {
    val context = LocalContext.current
    val recentSearches = viewModel.recentSearches.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRecentSearches(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        // Header with search bar
        SearchHeader(
            query = searchQuery.value,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onBackClick = onBackClick,
            onSearchSubmit = {
                if (searchQuery.value.isNotBlank()) {
                    viewModel.saveSearchKeyword(context, searchQuery.value)
                    onSearchSubmit(searchQuery.value)
                }
            },
            onClearClick = { viewModel.updateSearchQuery("") }
        )

        // Loading state
        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        // Recent searches
        if (searchQuery.value.isBlank() && recentSearches.value.isNotEmpty()) {
            RecentSearchSection(
                searches = recentSearches.value,
                onSearchClick = { keyword ->
                    viewModel.updateSearchQuery(keyword)
                    onRecentSearchClick(keyword)
                },
                onRemoveClick = { keyword ->
                    viewModel.removeRecentSearch(context, keyword)
                },
                onClearAll = { viewModel.clearAllSearches(context) }
            )
        } else if (searchQuery.value.isBlank() && recentSearches.value.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "🔍",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Chưa có lịch sử tìm kiếm",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Nhập từ khóa để tìm sản phẩm",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchSubmit: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 8.dp)
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Search input field
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Tìm kiếm sản phẩm...",
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearchSubmit() }
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun RecentSearchSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch sử tìm kiếm",
                fontSize = 14.sp,
                color = Color.Gray
            )
            TextButton(onClick = onClearAll) {
                Text(
                    text = "Xóa tất cả",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        }

        HorizontalDivider()

        // Recent searches list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searches) { keyword ->
                RecentSearchItem(
                    keyword = keyword,
                    onSearchClick = { onSearchClick(keyword) },
                    onRemoveClick = { onRemoveClick(keyword) }
                )
            }
        }
    }
}

@Composable
private fun RecentSearchItem(
    keyword: String,
    onSearchClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search icon + keyword
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onSearchClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 12.dp),
                tint = Color.Gray
            )
            Text(
                text = keyword,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Remove button
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                modifier = Modifier.size(18.dp),
                tint = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true, name = "SearchScreen with recent searches")
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .systemBarsPadding()
        ) {
            // Header with search bar
            SearchHeader(
                query = "",
                onQueryChange = {},
                onBackClick = {},
                onSearchSubmit = {},
                onClearClick = {}
            )

            // Recent searches section
            RecentSearchSection(
                searches = listOf(
                    "iPhone 15",
                    "Samsung Galaxy",
                    "Xiaomi",
                    "OnePlus",
                    "Google Pixel"
                ),
                onSearchClick = {},
                onRemoveClick = {},
                onClearAll = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "SearchHeader with text")
@Composable
fun SearchHeaderPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            SearchHeader(
                query = "Điện thoại gaming",
                onQueryChange = {},
                onBackClick = {},
                onSearchSubmit = {},
                onClearClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty state preview")
@Composable
fun SearchScreenEmptyPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .systemBarsPadding()
        ) {
            SearchHeader(
                query = "",
                onQueryChange = {},
                onBackClick = {},
                onSearchSubmit = {},
                onClearClick = {}
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "🔍",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Chưa có lịch sử tìm kiếm",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Nhập từ khóa để tìm sản phẩm",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


