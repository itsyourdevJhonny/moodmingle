package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.saved.AllContent
import com.emc.moodmingle.ui.settings.saved.collection.CollectionContent
import com.emc.moodmingle.ui.settings.saved.collection.CreateCollectionDialog
import com.emc.moodmingle.ui.settings.saved.media.MediaContent
import com.emc.moodmingle.ui.settings.saved.text.TextContent
import com.emc.moodmingle.ui.theme.BrushGrayGradient
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.CollectionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.SaveViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun SavedScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val saveViewModelFirebase = hiltViewModel<SaveViewModelFirebase>()
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val userDao = AppDatabase.getDatabase(context).userDao()
    var userId by remember { mutableStateOf("") }
    var selectedCollectionName by remember { mutableStateOf("None") }

    var type by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        userId = userDao.getLoggedUser()?.uid ?: ""
    }

    val saved by remember(userId) {
        saveViewModelFirebase.getSavedByUser(userId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    }.collectAsState(initial = emptyList())

    var displayedSaved by rememberSaveable { mutableStateOf(emptyList<SaveEntityFirebase>()) }

    LaunchedEffect(saved) {
        displayedSaved = saved.sortedByDescending { it.time }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(saved.size, onBack)

        Header(
            userId,
            type,
            saved,
            displayedSaved,
            onSortedBy = { displayedSaved = it },
            onGroupedBy = { displayedSaved = it },
            onSelectedType = { type = it },
            selectedCollectionName,
            onSelectedCollection = { selectedCollectionName = it }
        )

        DrawNoPaddingLine(thickness = 0.5.dp)

        if (saved.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.empty),
                    contentDescription = "Empty",
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "You don't have any saved post.",
                    color = GrayTextColor,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            when (type) {
                "All" -> AllContent(
                    saved = displayedSaved,
                    postViewModelFirebase,
                    saveViewModelFirebase,
                    userViewModelFirebase
                )

                "Media" -> MediaContent(displayedSaved, userId)
                "Text" -> TextContent(
                    saved = displayedSaved,
                    postViewModelFirebase,
                    saveViewModelFirebase,
                    userViewModelFirebase
                )

                "Collections" -> CollectionContent(
                    userId,
                    saved,
                    onGroupBy = { displayedSaved = it },
                    onSelectedType = { type = it },
                    onSelectedCollectionName = { selectedCollectionName = it }
                )
            }
        }
    }
}

@Composable
private fun TopBar(count: Int, onBack: () -> Unit) {
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
            modifier = Modifier.clickable { onBack() }
        )

        Text(
            text = "$count Saved ${if (count > 1) "Posts" else "Post"}",
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun Header(
    userId: String,
    type: String,
    saved: List<SaveEntityFirebase>,
    displayedSaved: List<SaveEntityFirebase>,
    onSortedBy: (List<SaveEntityFirebase>) -> Unit,
    onGroupedBy: (List<SaveEntityFirebase>) -> Unit,
    onSelectedType: (String) -> Unit,
    selectedCollectionName: String,
    onSelectedCollection: (String) -> Unit,
) {
    val types = listOf(
        "All" to R.drawable.all,
        "Media" to R.drawable.media,
        "Text" to R.drawable.text,
        "Collections" to R.drawable.collections
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NewCollectionButton(userId)
            RemoveAllButton(saved)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            types.forEach { filterType ->
                val isActive = type == filterType.first
                Box(
                    modifier = Modifier
                        .background(
                            brush = if (isActive) BrushPrimaryGradient else BrushGrayGradient,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectedType(filterType.first) }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(filterType.second),
                            contentDescription = filterType.first,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        if (type != "Collections") {
            DrawNoPaddingLine(thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GroupBy(
                    userId,
                    saved,
                    selectedCollectionName,
                    onSelectedCollection = onSelectedCollection,
                    onGroupedBy = onGroupedBy
                )
                SortBy(displayedSaved, onSortedBy = onSortedBy)
            }
        }
    }
}

@Composable
fun NewCollectionButton(userId: String) {
    var showDialog by remember { mutableStateOf(false) }

    TextButton(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.background(BrushPrimaryGradient, CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = "New Collection",
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    if (showDialog) {
        CreateCollectionDialog(onDismiss = { showDialog = false }, userUid = userId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemoveAllButton(saved: List<SaveEntityFirebase>) {
    val scope = rememberCoroutineScope()
    val saveViewModelFirebase = hiltViewModel<SaveViewModelFirebase>()
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            if (saved.isEmpty()) {
                return@TextButton
            }
            showDialog = true
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.remove_all_saved),
            contentDescription = "Remove All",
            modifier = Modifier.size(24.dp),
            tint = Color.Red
        )

        Text(
            text = "Remove all",
            modifier = Modifier.padding(start = 8.dp),
            color = Color.Red.copy(alpha = 0.7f)
        )
    }

    if (showDialog) {
        AlertDialog(
            containerColor = PrimaryDark,
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    brush = BrushPrimaryGradient,
                    shape = RectangleShape
                ),
            shape = RectangleShape,
            properties = DialogProperties(),
            onDismissRequest = {},
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.remove_all_saved),
                        contentDescription = "Remove All",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Red
                    )

                    Text("Delete all?")
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete all your saved posts?",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showDialog = false
                            isLoading = true
                        },
                        modifier = Modifier.background(Color.Red, CircleShape),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = "Delete All", color = Color.White)
                    }

                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.background(BrushPrimaryGradient, CircleShape),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = "Cancel", color = Color.White)
                    }
                }
            }
        )
    }

    if (isLoading) {
        LoadingDialog("Deleting all") {
            scope.launch {
                delay(3000)
                saveViewModelFirebase.deleteAll(saved)
                isLoading = false
            }
        }
    }
}

@Composable
private fun GroupBy(
    userId: String,
    saved: List<SaveEntityFirebase>,
    selectedCollectionName: String,
    onSelectedCollection: (String) -> Unit,
    onGroupedBy: (List<SaveEntityFirebase>) -> Unit
) {
    val collectionViewModelFirebase = hiltViewModel<CollectionViewModelFirebase>()
    val scope = rememberCoroutineScope()

    val collections by remember(userId) {
        collectionViewModelFirebase.getCollectionByUser(userId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    }.collectAsState(emptyList())

    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Icon(
                painter = painterResource(R.drawable.group_by),
                contentDescription = "Group",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )

            Text(text = "Group By", style = Typography.bodyMedium.copy(color = GrayTextColor))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Arrow Down",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.widthIn(max = 116.dp)
            ) {
                Text(
                    text = selectedCollectionName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.bodyMedium.copy(
                        color = Color.White,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = PrimaryDark,
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .border(
                        width = 0.5.dp,
                        brush = BrushPrimaryGradient,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Collections", color = Color.White, fontWeight = FontWeight.Bold)
                    DrawNoPaddingLine(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }

                val collectionNames = mutableListOf("None") + collections.map { it.name }
                collectionNames.forEach { collectionName ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = collectionName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .widthIn(max = 142.dp)
                                )

                                Icon(
                                    painter = painterResource(R.drawable.select_hand),
                                    contentDescription = "Select",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .graphicsLayer(alpha = 0.99f)
                                        .drawGradient()
                                )
                            }
                        },
                        onClick = {
                            onSelectedCollection(collectionName)
                            expanded = false

                            scope.launch {
                                // GET COLLECTION FOR SELECTED NAME
                                val userCollections = collectionViewModelFirebase
                                    .getCollectionByUser(userId)
                                    .first() // get the latest snapshot
                                val selectedCollection =
                                    userCollections.find { it.name == collectionName }

                                val groupedSaves: List<SaveEntityFirebase> =
                                    if (selectedCollection != null && selectedCollectionName != "None") {
                                        saved.filter { savedItem ->
                                            selectedCollection.saveIds.contains(savedItem.id)
                                        }
                                    } else {
                                        saved // show all if "None"
                                    }

                                onGroupedBy(groupedSaves)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SortBy(
    displayedSaved: List<SaveEntityFirebase>,
    onSortedBy: (List<SaveEntityFirebase>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val sortTypes = listOf("Time Saved", "Time Posted", "Username")
    var selectedType by remember { mutableStateOf("Time Saved") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Icon(
                painter = painterResource(R.drawable.sort),
                contentDescription = "Sort",
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
            Text(text = "Sorted By", style = Typography.bodyMedium.copy(color = GrayTextColor))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Arrow Down",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        Box {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedType,
                    style = Typography.bodyMedium.copy(
                        color = Color.White,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = PrimaryDark,
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .border(
                        width = 0.5.dp,
                        brush = BrushPrimaryGradient,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                sortTypes.forEach { videoQuality ->
                    DropdownMenuItem(
                        text = { Text(text = videoQuality) },
                        onClick = {
                            selectedType = videoQuality
                            expanded = false

                            scope.launch {
                                // first: map every saved item with its user (suspending allowed here)
                                val savedWithUsers = displayedSaved.map { saved ->
                                    val user = userViewModelFirebase
                                        .getUserByUid(saved.userUid)
                                        .first()
                                        .getOrNull()

                                    val post = postViewModelFirebase
                                        .getPostById(saved.postId)
                                        .first()

//                                    saved to user
                                    Triple(saved, user, post)
                                }

                                // second: sort synchronously
                                val sorted = when (selectedType) {
                                    "Username" -> savedWithUsers.sortedByDescending { it.second?.username }
                                    "Time Posted" -> savedWithUsers.sortedByDescending { it.third?.timeAgo }
                                    else -> savedWithUsers.sortedByDescending { it.first.time }
                                }

                                // third: extract the saved items only
                                onSortedBy(sorted.map { it.first })
                            }
                        }
                    )
                }
            }
        }
    }
}