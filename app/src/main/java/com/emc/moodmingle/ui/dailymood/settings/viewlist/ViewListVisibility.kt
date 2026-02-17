package com.emc.moodmingle.ui.dailymood.settings.viewlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun ViewListVisibility(onDismiss: () -> Unit) {
    Scaffold(
        topBar = { ScaffoldHeader(title = "View List Visibility") { onDismiss() } }
    ) { paddingValues ->
        Content(paddingValues)
    }
}

@Composable
fun Content(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {

    }
}