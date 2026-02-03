package com.emc.moodmingle.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emc.moodmingle.data.firebase.model.search.SearchEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.post.action.formatText
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.SearchViewModelFirebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onSearchClick: (List<SearchEntityFirebase>) -> Unit,
    onViewClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopBar(onBackClick, onSearchClick, onViewClick)
    }
}

@Composable
fun TopBar(
    onBackClick: () -> Unit,
    onSearchClick: (List<SearchEntityFirebase>) -> Unit,
    onViewClick: (String) -> Unit
) {
    val context = LocalContext.current
    val searchViewModel = hiltViewModel<SearchViewModelFirebase>()

    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userDao.getLoggedUser()?.uid?.let { currentUserUid = it }
    }

    val searchHistory by remember(currentUserUid) {
        searchViewModel.getSearchesBySearcherId(currentUserUid)
    }.collectAsState(initial = emptyList())

    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<SearchEntityFirebase>?>(null) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopIconButton(onBackClick, Icons.AutoMirrored.Filled.ArrowBack, "Back")

            SearchInputField(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearching = { isSearching = it }
            )

            TopIconButton(
                onClick = {
                    Log.d("TOP BAR", "FOUND: ${searchResults?.size}")
                    if (searchText.trim().isNotEmpty() && searchResults != null) {
                        searchResults?.let { onSearchClick(it) }
                    } else {
                        Toast.makeText(context, "Try to search by username.", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                icon = Icons.Default.Search,
                description = "Search"
            )
        }

        if (isSearching) {
            SearchingContent(searchText, onSearchResults = { searchResults = it }, onViewClick)
        } else {
            if (searchHistory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    NoSearchResult()
                }
            } else {
                SearchContent(searchHistory, onViewClick)
            }
        }
    }
}

@Composable
fun TopIconButton(onClick: () -> Unit, icon: ImageVector, description: String) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = icon,
            contentDescription = description,
            tint = Color.White
        )
    }
}

@Composable
fun RecentSearchesText() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Recent Searches",
        style = MaterialTheme.typography.titleMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun SearchInputField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearching: (Boolean) -> Unit
) {
    TextField(
        modifier = Modifier
            .width(250.dp)
            .background(
                brush = BrushPrimaryGradient,
                shape = RoundedCornerShape(30.dp)
            ),
        value = searchText,
        onValueChange = {
            onSearchTextChange(it)

            onSearching(it.isNotEmpty())
        },
        placeholder = { Text(text = "Search", color = GrayTextColor) },
        trailingIcon = {
            if (searchText.isNotBlank()) {
                IconButton(
                    onClick = {
                        onSearchTextChange("")
                        onSearching(false)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear text",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun SearchingContent(
    searchText: String,
    onSearchResults: (List<SearchEntityFirebase>) -> Unit,
    onViewClick: (String) -> Unit
) {
    if (searchText.trim().isNotEmpty()) {
        val searchViewModel = hiltViewModel<SearchViewModelFirebase>()
        val searchResults by searchViewModel.searchResults.collectAsState()

        LaunchedEffect(Unit) {
            searchViewModel.searchUsers(searchText)
        }

        if (searchResults.isNotEmpty()) {
            onSearchResults(searchResults)
            SearchingResult(searchResults, onViewClick)
        } else {
            NoSearchResult()
        }
    }
}

@Composable
fun SearchingResult(searchResults: List<SearchEntityFirebase>, onViewClick: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val searchViewModel = hiltViewModel<SearchViewModelFirebase>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    var results by remember { mutableStateOf(searchResults) }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { RecentSearchesText() }

        items(results, key = { it.userUid }) { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(PrimaryDark)
                    .clickable {
                        scope.launch {
                            val search = searchViewModel.getSearchBySearcherIdAndUserId(
                                currentUser?.uid ?: "", result.userUid
                            )

                            if (search == null) {
                                searchViewModel.addSearch(
                                    SearchEntityFirebase(
                                        searcherId = currentUser?.uid ?: "",
                                        userUid = result.userUid,
                                        username = result.username
                                    )
                                )
                            }

                            onViewClick(result.userUid)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    var user by remember { mutableStateOf<UserEntityFirebase?>(null) }

                    LaunchedEffect(result.userUid) {
                        user = userViewModel.getUserByUid(result.userUid).first().getOrNull()
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user?.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )

                    Text(text = formatText(result.username, 27), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SearchContent(searchHistory: List<SearchEntityFirebase>, onViewClick: (String) -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModelFirebase>()

    val allUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())
    val userLookup = remember(allUsers) { allUsers.associateBy { it.uid } }

    if (searchHistory.isEmpty()) NoSearchResult()

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { RecentSearchesText() }

        items(searchHistory, key = { it.searcherId + it.time }) { history ->
            val user = userLookup[history.userUid]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(PrimaryDark)
                    .clickable { onViewClick(history.userUid) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user?.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)

                    )

                    Text(
                        text = formatText(user?.username ?: "", 20),
                        color = Color.White
                    )
                }

                IconButton(onClick = { searchViewModel.deleteSearch(history) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.Red,
                    )
                }
            }
        }
    }
}