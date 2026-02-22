package com.emc.moodmingle.ui.settings.favorites.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesCollectionEntity
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.saved.collection.CollectionsBottomSheet
import com.emc.moodmingle.ui.settings.saved.collection.ShowEditCollectionDialog
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.settings.utils.ShowRemoveDialog
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesCollectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun FavoritesCollection(
    userId: String,
    favorites: List<FavoritesEntityFirebase>,
    onSelectedType: (String) -> Unit,
    onGroupByCollection: (List<FavoritesEntityFirebase>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val collectionViewModel = hiltViewModel<FavoritesCollectionViewModel>()

    val collections by remember(userId) {
        collectionViewModel.getCollectionByUser(userId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    }.collectAsState(emptyList())

    Column {
        Header(userId, collections)
        DrawNoPaddingLine(thickness = 0.5.dp)
        Content(
            favorites,
            collections,
            collectionViewModel,
            scope,
            userId,
            onSelectedType = onSelectedType,
            onGroupByCollection = onGroupByCollection,
        )
    }
}

@Composable
private fun Header(userId: String, collections: List<FavoritesCollectionEntity>) {
    val scope = rememberCoroutineScope()
    val favoritesCollectionViewModel = hiltViewModel<FavoritesCollectionViewModel>()
    var showAddDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
                .clickable { showAddDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }

        Text(text = "${collections.size} Total", color = Color.White)

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Red, RoundedCornerShape(8.dp))
                .clickable {
                    if (collections.isEmpty()) {
                        return@clickable
                    }
                    showRemoveDialog = true
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.remove_all_saved),
                contentDescription = "Remove",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }

    if (showAddDialog) {
        CreateFavoritesCollectionDialog(onDismiss = { showAddDialog = false }, userId)
    }

    if (showRemoveDialog) {
        ShowRemoveDialog(onShowDialog = { showRemoveDialog = it }, onLoading = { isLoading = it })
    }

    if (isLoading) {
        LoadingDialog("Deleting all") {
            scope.launch {
                delay(2000)
                favoritesCollectionViewModel.deleteAll(collections)
                isLoading = false
            }
        }
    }
}

@Composable
private fun Content(
    favorites: List<FavoritesEntityFirebase>,
    collections: List<FavoritesCollectionEntity>,
    collectionViewModel: FavoritesCollectionViewModel,
    scope: CoroutineScope,
    userId: String,
    onSelectedType: (String) -> Unit,
    onGroupByCollection: (List<FavoritesEntityFirebase>) -> Unit
) {
    val sortedCollections = collections.sortedByDescending { it.time }
    var isLoading by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }
    var selectedCollection by remember { mutableStateOf<FavoritesCollectionEntity?>(null) }
    var newName by remember { mutableStateOf("") }

    if (collections.isEmpty()) {
        EmptyComponent(iconRes = R.drawable.no_collections, text = "No collections yet. Create one.")
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(sortedCollections) { collection ->
            var showSheet by remember { mutableStateOf(false) }

            CollectionItem(
                userId,
                collection,
                favorites,
                collectionViewModel,
                scope,
                onSelectedType,
                onGroupByCollection,
                onShowSheet = { showSheet = it },
                onSelectedCollection = { selectedCollection = it }
            )

            if (showSheet) {
                CollectionsBottomSheet(
                    onShowSheet = { showSheet = it },
                    onTypeChange = { type = it },
                    onLoadingChange = { isLoading = it },
                    onEditing = { isEditing = it }
                )
            }
        }
    }

    if (isEditing) {
        ShowEditCollectionDialog(
            collectionName = selectedCollection?.name,
            onLoading = { isLoading = it },
            onEditing = { isEditing = it },
            onNewNameChange = { newName = it }
        )
    }

    if (isLoading) {
        PerformOperation(
            onLoading = { isLoading = it },
            type,
            newName,
            collection = selectedCollection!!,
            collectionViewModel
        )
    }
}

@Composable
private fun CollectionItem(
    userId: String,
    collection: FavoritesCollectionEntity,
    favorites: List<FavoritesEntityFirebase>,
    collectionViewModel: FavoritesCollectionViewModel,
    scope: CoroutineScope,
    onSelectedType: (String) -> Unit,
    onGroupByCollection: (List<FavoritesEntityFirebase>) -> Unit,
    onShowSheet: (Boolean) -> Unit,
    onSelectedCollection: (FavoritesCollectionEntity?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .clickable {
                displayFavoritesCollectionItems(
                    userId,
                    collection,
                    favorites,
                    collectionViewModel,
                    scope,
                    onSelectedType,
                    onGroupByCollection
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.collections),
                    contentDescription = "Collections",
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(text = collection.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }

            Icon(
                painter = painterResource(R.drawable.more),
                contentDescription = "More",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onShowSheet(true); onSelectedCollection(collection) }
            )
        }

        if (collection.favoritesIds.isNotEmpty()) {
            Text(
                text = "${collection.favoritesIds.size} ${if (collection.favoritesIds.size == 1) "item" else "items"}",
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                modifier = Modifier.padding(start = 38.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun PerformOperation(
    onLoading: (Boolean) -> Unit,
    type: String,
    newName: String,
    collection: FavoritesCollectionEntity,
    collectionViewModel: FavoritesCollectionViewModel
) {
    val scope = rememberCoroutineScope()

    LoadingDialog(
        text = when (type) {
            "Edit" -> "Editing"
            "Remove" -> "Removing"
            else -> ""
        }
    ) {
        scope.launch {
            delay(1000)

            when (type) {
                "Edit" -> collectionViewModel.update(collection.copy(name = newName))
                "Remove" -> collectionViewModel.delete(collection)
            }
            onLoading(false)
        }
    }
}