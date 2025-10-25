package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark

data class Search(
    val id: Int,
    val avatar: String,
    val username: String
)

@Composable
fun SearchScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopBar(onBackClick)
    }
}

@Composable
fun TopBar(onBackClick: () -> Unit) {
    val searchList = List(50) { index ->
        Search(
            id = index + 1,
            avatar = listOf("😀", "😎", "😊", "😇", "🥳", "🤩").random(),
            username = "User ${index + 1}"
        )
    }

    var searchText by remember { mutableStateOf("") }
    val filteredList = searchList.filter { it.username.contains(searchText, ignoreCase = true) }
    var showSearchScreen by remember { mutableStateOf(false) }

    if (showSearchScreen) {
        SearchResultsScreen(filteredList) {
            showSearchScreen = false
        }
    } else {
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
                    onSearchTextChange = { searchText = it }
                )

                TopIconButton(
                    onClick = { if (searchText.isNotEmpty()) showSearchScreen = true },
                    icon = Icons.Default.Search,
                    description = "Search"
                )
            }

            SearchResults(searchList, false)
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
fun SearchInputField(searchText: String, onSearchTextChange: (String) -> Unit) {
    TextField(
        modifier = Modifier
            .width(250.dp)
            .background(
                brush = BrushPrimaryGradient,
                shape = RoundedCornerShape(30.dp)
            ),
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = { Text(text = "Search", color = GrayTextColor) },
        trailingIcon = {
            if (searchText.isNotBlank()) {
                IconButton(onClick = { onSearchTextChange("") }) {
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

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SearchResults(searchResults: List<Search>, isFromSearched: Boolean) {
    var results by remember { mutableStateOf(searchResults) }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { RecentSearchesText() }

        /*if (results.isEmpty()) {
            item { NoSearchResult() }
        }*/

        items(results, key = { it.id }) { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(SecondaryDark),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(BrushPrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.avatar,
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = result.username,
                        color = Color.White
                    )
                }

                if (!isFromSearched) {
                    IconButton(onClick = {
                        results = results.filterNot { it == result }
                    }) {
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
}

/*@Composable
fun NoSearchResult() {
    Column(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.no_search),
            contentDescription = "No Result",
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(50.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = BrushPrimaryGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            tint = Color.Unspecified
        )

        Text(
            text = "No results found. Try adjusting your search.",
            color = GrayTextColor,
            textAlign = TextAlign.Center
        )
    }
}*/

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    SearchScreen {}
}