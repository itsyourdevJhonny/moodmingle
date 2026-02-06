package com.emc.moodmingle.ui.dailymood.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.api.nominatim.NominatimPlace
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.LocationViewModel

@Composable
fun DailyMoodLocation(
    dailyMood: DailyMoodEntity,
    onDailyMoodEdited: (DailyMoodEntity) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Find Location",
                enabled = false,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Content(paddingValues, dailyMood, onDailyMoodEdited, onBack)
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    dailyMood: DailyMoodEntity,
    onDailyMoodEdited: (DailyMoodEntity) -> Unit,
    onBack: () -> Unit
) {
    val locationViewModel = hiltViewModel<LocationViewModel>()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = locationViewModel.query,
            onValueChange = {
                locationViewModel.onQueryChange(it)
                isLoading = true
            },
            placeholder = { Text(text = "Search Location...") },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                unfocusedPlaceholderColor = Color.Gray,
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Color.White,
                focusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        if (isLoading && locationViewModel.query.isNotBlank()) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .drawGradient()
            )
        }

        if (locationViewModel.searchResults.isNotEmpty() && locationViewModel.query.isNotBlank()) {
            isLoading = false

            LazyColumn {
                items(locationViewModel.searchResults) { place ->
                    LocationItem(place) {
                        onDailyMoodEdited(dailyMood.copy(location = place.displayName))
                        locationViewModel.onQueryChange("")
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
fun LocationItem(place: NominatimPlace, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = place.displayName,
            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Latitude: ${place.lat}",
                style = Typography.bodySmall.copy(color = GrayTextColor)
            )

            Text(
                text = "Longitude ${place.lon}",
                style = Typography.bodySmall.copy(color = GrayTextColor)
            )
        }
    }
}