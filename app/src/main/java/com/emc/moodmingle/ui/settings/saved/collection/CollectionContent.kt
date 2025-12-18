package com.emc.moodmingle.ui.settings.saved.collection

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.saved.CollectionEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.saved.BottomSheetItem
import com.emc.moodmingle.ui.settings.saved.utils.NoResult
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.saved.CollectionViewModelFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun CollectionContent(
    userId: String,
    saved: List<SaveEntityFirebase>,
    onSelectedType: (String) -> Unit,
    onGroupBy: (List<SaveEntityFirebase>) -> Unit,
    onSelectedCollectionName: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val collectionViewModelFirebase = hiltViewModel<CollectionViewModelFirebase>()

    val collections by remember(userId) {
        collectionViewModelFirebase.getCollectionByUser(userId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    }.collectAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(PrimaryDark)
    ) {
        Header(collections.size)
        Content(
            saved,
            collections,
            collectionViewModelFirebase,
            scope,
            userId,
            onSelectedType,
            onGroupBy,
            onSelectedCollectionName
        )
    }
}

@Composable
private fun Header(totalCollections: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Your Collections")

        if (totalCollections != 0) {
            Text(
                text = "$totalCollections",
                color = Color.White,
                fontStyle = FontStyle.Italic
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient(),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun Content(
    saved: List<SaveEntityFirebase>,
    collections: List<CollectionEntityFirebase>,
    collectionViewModelFirebase: CollectionViewModelFirebase,
    scope: CoroutineScope,
    userId: String,
    onSelectedType: (String) -> Unit,
    onGroupBy: (List<SaveEntityFirebase>) -> Unit,
    onSelectedCollectionName: (String) -> Unit
) {
    val sortedCollections = collections.sortedByDescending { it.time }
    var isLoading by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }
    var selectedCollection by remember { mutableStateOf<CollectionEntityFirebase?>(null) }
    var newName by remember { mutableStateOf("") }

    if (collections.isEmpty()) {
        NoResult(iconRes = R.drawable.no_collections, text = "No collections yet. Create one.")
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
                saved,
                collectionViewModelFirebase,
                scope,
                onSelectedType,
                onGroupBy,
                onSelectedCollectionName,
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
            collectionEntityFirebase = selectedCollection!!,
            collectionViewModelFirebase
        )
    }
}

@Composable
private fun CollectionItem(
    userId: String,
    collection: CollectionEntityFirebase,
    saved: List<SaveEntityFirebase>,
    collectionViewModelFirebase: CollectionViewModelFirebase,
    scope: CoroutineScope,
    onSelectedType: (String) -> Unit,
    onGroupBy: (List<SaveEntityFirebase>) -> Unit,
    onSelectedCollectionName: (String) -> Unit,
    onShowSheet: (Boolean) -> Unit,
    onSelectedCollection: (CollectionEntityFirebase?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .clickable {
                displayCollectionItems(
                    userId,
                    collection,
                    saved,
                    collectionViewModelFirebase,
                    scope,
                    onSelectedType,
                    onGroupBy,
                    onSelectedCollectionName
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

        if (collection.saveIds.isNotEmpty()) {
            Text(
                text = "${collection.saveIds.size} ${if (collection.saveIds.size == 1) "item" else "items"}",
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                modifier = Modifier.padding(start = 38.dp, bottom = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsBottomSheet(
    onShowSheet: (Boolean) -> Unit,
    onTypeChange: (String) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    onEditing: (Boolean) -> Unit
) {
    val actionTypes = listOf("Edit" to R.drawable.edit, "Remove" to R.drawable.remove)

    ModalBottomSheet(
        onDismissRequest = { onShowSheet(false) },
        containerColor = PrimaryDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
    ) {
        Column(modifier = Modifier.height(200.dp)) {
            DrawNoPaddingLine(
                thickness = 0.5.dp,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            )

            actionTypes.forEach { actionType ->
                BottomSheetItem(
                    text = actionType.first,
                    iconRes = actionType.second,
                    onType = { onTypeChange(it) },
                    onShowSheet = onShowSheet,
                    onLoading = {
                        if (actionType.first == "Edit") onEditing(it) else onLoadingChange(it)
                    }
                )
            }
        }
    }
}

@Composable
fun ShowEditCollectionDialog(
    collectionName: String?,
    onLoading: (Boolean) -> Unit,
    onEditing: (Boolean) -> Unit,
    onNewNameChange: (String) -> Unit
) {
    val context = LocalContext.current
    var value by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onEditing(false) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryDark)
                .border(
                    width = 0.5.dp,
                    brush = BrushPrimaryGradient,
                    shape = RectangleShape
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "Edit Collection",
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )

                DrawNoPaddingLine(
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                Text(
                    text = collectionName ?: "",
                    color = GrayTextColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(text = "New Name") }
                )

                TextButton(
                    onClick = {
                        if (value.isBlank()) {
                            Toast.makeText(
                                context,
                                "New collection name cannot be blank",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }

                        if (value == collectionName) {
                            Toast.makeText(
                                context,
                                "Cannot use current collection name as new",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }

                        onNewNameChange(value)
                        onLoading(true)
                        onEditing(false)
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .background(BrushPrimaryGradient, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = Color.White
                    )
                    Text(
                        text = "Change",
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformOperation(
    onLoading: (Boolean) -> Unit,
    type: String,
    newName: String,
    collectionEntityFirebase: CollectionEntityFirebase,
    collectionViewModelFirebase: CollectionViewModelFirebase
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
                "Edit" -> collectionViewModelFirebase.update(collectionEntityFirebase.copy(name = newName))
                "Remove" -> collectionViewModelFirebase.delete(collectionEntityFirebase)
            }
            onLoading(false)
        }
    }
}