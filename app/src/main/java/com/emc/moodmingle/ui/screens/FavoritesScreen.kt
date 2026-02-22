package com.emc.moodmingle.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesEntityFirebase
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.favorites.collection.FavoritesCollection
import com.emc.moodmingle.ui.settings.favorites.FavoritesContent
import com.emc.moodmingle.ui.theme.BrushGrayGradient
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val favoritesViewModelFirebase = hiltViewModel<FavoritesViewModelFirebase>()

    val userDao = AppDatabase.getDatabase(context).userDao()
    var userId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var selectedType by remember { mutableStateOf("Favorites") }

    LaunchedEffect(Unit) {
        userId = userDao.getLoggedUser()?.uid ?: ""
    }

    val favorites by remember(userId) {
        favoritesViewModelFirebase.getFavoritesByUser(userId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    }.collectAsState(initial = emptyList())

    var filteredFavorites by remember { mutableStateOf(emptyList<FavoritesEntityFirebase>()) }

    LaunchedEffect(favorites) {
        filteredFavorites = favorites.sortedByDescending { it.time }
    }

    LaunchedEffect(userId, filteredFavorites) {
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(PrimaryDark),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(favorites.size, onBackClick)

        Header(
            selectedType,
            favorites,
            onSelectedType = { selectedType = it },
            onRefreshFavorites = { filteredFavorites = it },
            onLoading = { isLoading = it }
        )

        if (!isLoading) {
            when (selectedType) {
                "Favorites" -> FavoritesContent(
                    favorites = filteredFavorites,
                    isLoading,
                    userId
                )

                "Collections" -> FavoritesCollection(
                    userId,
                    filteredFavorites,
                    onSelectedType = { selectedType = it },
                    onGroupByCollection = { filteredFavorites = it }
                )
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient(),
            )
        }
    }
}

@Composable
private fun TopBar(count: Int, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBackClick() }
        )

        Text(
            text = "$count Favorites ${if (count > 1) "Posts" else "Post"}",
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun Header(
    selectedType: String,
    favorites: List<FavoritesEntityFirebase>,
    onSelectedType: (String) -> Unit,
    onRefreshFavorites: (List<FavoritesEntityFirebase>) -> Unit,
    onLoading: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val actionTypes =
        listOf(R.drawable.favorites to "Favorites", R.drawable.collections to "Collections")

    Column {
        Text(
            text = "Your $selectedType",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            actionTypes.forEach { actionType ->
                val isSelected = selectedType == actionType.second
                Box(
                    modifier = Modifier
                        .width(158.dp)
                        .size(48.dp)
                        .background(
                            brush = if (isSelected) BrushPrimaryGradient else BrushGrayGradient,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectedType(actionType.second) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(actionType.first),
                            contentDescription = actionType.second,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )

                        Text(text = actionType.second, color = Color.White)
                    }
                }
            }
        }

        DrawNoPaddingLine(thickness = 0.5.dp)

        if (selectedType == "Favorites") {
            Box(
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.clickable {
                        onRefreshFavorites(favorites)
                        scope.launch {
                            onLoading(true)
                            delay(1000)
                            onLoading(false)
                        }
                    },
                    tint = Color.White
                )
            }
        }
    }
}