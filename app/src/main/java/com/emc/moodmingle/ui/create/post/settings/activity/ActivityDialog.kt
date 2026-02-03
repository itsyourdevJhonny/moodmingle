package com.emc.moodmingle.ui.create.post.settings.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient


@Composable
fun ActivityDialog(
    activity: String,
    onActivitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var currentActivity by remember { mutableStateOf<ActivityItem?>(null) }

    if (activity.isNotBlank()) {
        val split = activity.split(" ")
        currentActivity =
            ActivityItem(label = (split - split[0]).joinToString(" "), emoji = split[0])
    }

    var search by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf(currentActivity) }
    var customActivity by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredActivities = getActivitySuggestions().filter {
        it.label.contains(search, ignoreCase = true)
    }.sortedWith(
        compareByDescending<ActivityItem> { it == selectedActivity }
            .thenComparing { it.label }
    )

    LaunchedEffect(selectedActivity) { listState.animateScrollToItem(0) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Select Activity",
                enabled = selectedActivity != null || customActivity.isNotBlank(),
                onBack = onDismiss,
                onOkay = {
                    val result =
                        selectedActivity?.let { "${it.emoji} ${it.label}" } ?: customActivity.trim()

                    if (result != activity) toastMessage(context, "Settings Saved")

                    onActivitySelected(result)
                    onDismiss()
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CreateButton(onShowCreateDialog = { showCreateDialog = it })

            HorizontalDivider(thickness = 0.5.dp)

            SearchField(search, onSearchChanged = { search = it }) { selectedActivity = it }

            LazyColumn(state = listState, modifier = Modifier.padding(horizontal = 16.dp)) {
                items(items = filteredActivities, key = { it.label }) { activity ->
                    ActivityChip(
                        activity = activity,
                        selected = selectedActivity == activity,
                        onClick = {
                            selectedActivity = activity
                            customActivity = ""
                        }
                    )
                }
            }

            /*FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredActivities.forEach { activity ->
                    ActivityChip(
                        activity = activity,
                        selected = selectedActivity == activity,
                        onClick = {
                            selectedActivity = activity
                            customActivity = ""
                        }
                    )
                }
            }*/
        }
    }

    if (showCreateDialog) {
        CustomActivityDialog(
            customActivity,
            onCustomActivityChange = { customActivity = it },
            onActivitySelected = { selectedActivity = it },
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
private fun CreateButton(onShowCreateDialog: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "CAN'T FIND YOUR ACTIVITY?",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        TextButton(
            onClick = { onShowCreateDialog(true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryDark,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.drawGradient()
            )

            Text(text = "Create")
        }
    }
}

@Composable
private fun SearchField(
    search: String,
    onSearchChanged: (String) -> Unit,
    onActivitySelected: (ActivityItem?) -> Unit
) {
    OutlinedTextField(
        value = search,
        onValueChange = { onSearchChanged(it); onActivitySelected(null) },
        placeholder = { Text("Search activities...", color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = CircleShape
    )
}

@Composable
private fun LazyItemScope.ActivityChip(
    activity: ActivityItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text("${activity.emoji} ${activity.label}") },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = PrimaryDark,
            labelColor = Color.White,
            selectedLabelColor = Color.White,
            selectedContainerColor = MentionTextColor
        ),
        shape = CircleShape,
        border = BorderStroke(width = 0.5.dp, Color.DarkGray),
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
    )
}