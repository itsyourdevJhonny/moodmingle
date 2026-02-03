package com.emc.moodmingle.ui.settings.saved.utils

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.saved.BottomSheetItem
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.saved.CollectionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.SaveViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    save: SaveEntityFirebase,
    user: UserEntityFirebase?,
    onShowSheet: (Boolean) -> Unit,
    onRemove: () -> Unit,
    userId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val saveViewModelFirebase = hiltViewModel<SaveViewModelFirebase>()
    val collectionViewModelFirebase = hiltViewModel<CollectionViewModelFirebase>()
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .height(300.dp)
                .fillMaxSize()
                .background(PrimaryDark)
                .padding(horizontal = 8.dp)
        ) {
            DrawNoPaddingLine(
                thickness = 0.5.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BottomSheetItem(
                text = "Pin",
                iconRes = R.drawable.pin,
                onType = { type = it },
                onShowSheet = onShowSheet,
                onLoading = { isLoading = it }
            )

            var isSavedInCollection by remember { mutableStateOf(false) }

            LaunchedEffect(save.id, user!!.uid) {
                isSavedInCollection =
                    collectionViewModelFirebase.isSaveInCollection(save.id, user.uid)
            }

            if (!isSavedInCollection) {
                /*BottomSheetItem(
                    text = "Add To Collection",
                    iconRes = R.drawable.collections,
                    onType = { type = it },
                    onShowSheet = onShowSheet,
                    onLoading = { showDialog = it }
                )*/

                BottomSheetItem(
                    text = "Add To Collection",
                    iconRes = R.drawable.collections,
                    onType = { type = it },
                    onShowSheet = {
//                        onShowSheet(it)
                        showDialog = true
                    },
                    onLoading = { /*showDialog = it*/ }
                )

            }

            BottomSheetItem(
                text = "Remove",
                iconRes = R.drawable.remove,
                onType = {
                    type = it
                    onRemove()
                },
                onShowSheet = onShowSheet,
                onLoading = { isLoading = it }
            )
        }
    }

    if (isLoading) {
        LoadingDialog(
            text = when (type) {
                "Pin" -> "Pinning"
                "Add To Collection" -> "Adding to collection"
                "Remove" -> "Removing"
                else -> ""
            }
        ) {
            scope.launch {
                when (type) {
                    "Remove" -> saveViewModelFirebase.delete(save)
                    "Add To Collection" -> {
                        val collection =
                            collectionViewModelFirebase.getCollectionByNameAndUser(name, userId)

                        collectionViewModelFirebase.update(
                            collection = collection!!.copy(saveIds = collection.saveIds + save.id)
                        )

                        delay(3000)

                        Toast.makeText(
                            context,
                            "Item added to collection $name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                isLoading = false
            }
        }
    }

    if (showDialog) {
//        onShowSheet(false)
        Dialog(onDismissRequest = { showDialog = false }) {
            val collections by remember(userId) {
                collectionViewModelFirebase.getCollectionByUser(userId)
                    .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
            }.collectAsState(emptyList())

            val collectionNames = collections.map { it.name }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(PrimaryDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Select Collection",
                        style = Typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    collectionNames.forEach { collectionName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = collectionName)

                            IconButton(
                                onClick = {
                                    onShowSheet(false)
                                    isLoading = true
                                    showDialog = false
                                    name = collectionName
                                },
                                modifier = Modifier.background(
                                    brush = BrushPrimaryGradient,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}