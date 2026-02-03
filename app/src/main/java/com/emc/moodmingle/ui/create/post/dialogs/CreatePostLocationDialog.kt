package com.emc.moodmingle.ui.create.post.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.api.nominatim.NominatimPlace
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.LocationViewModel

@Composable
fun CreatePostLocationDialog(
    location: String,
    onSelectedLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val locationViewModel = hiltViewModel<LocationViewModel>()

    LaunchedEffect(Unit) {
        locationViewModel.loadNearby()
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Add Location",
                onBack = onDismiss
            )
        }
    ) { paddingValues ->
        LocationDialogContent(
            paddingValues,
            location,
            locationViewModel,
            onSelectedLocation,
            onDismiss
        )
    }
}

@Composable
fun LocationDialogContent(
    paddingValues: PaddingValues,
    location: String,
    locationViewModel: LocationViewModel,
    onSelectedLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .background(Color.Black),
    ) {
        if (location.isNotEmpty()) CurrentLocation(location)

        OutlinedTextField(
            value = locationViewModel.query,
            onValueChange = {
                locationViewModel.onQueryChange(it)
                isLoading = true
            },
            label = { Text("Search location") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = GrayTextColor,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = PurplePrimary,
                focusedLabelColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        NearbyLocation(locationViewModel)

        if (isLoading && locationViewModel.query.isNotBlank()) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        }

        if (locationViewModel.searchResults.isNotEmpty() && locationViewModel.query.isNotBlank()) {
            isLoading = false

            LazyColumn {
                items(locationViewModel.searchResults) { place ->
                    LocationItem(
                        place,
                        onSelectedLocation = {
                            onSelectedLocation(it)
                            locationViewModel.onQueryChange("")
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentLocation(location: String) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = "Current Selected Location",
                style = Typography.bodyMedium.copy(color = GrayTextColor)
            )
        }

        Text(
            text = location,
            style = Typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun NearbyLocation(locationViewModel: LocationViewModel) {
    locationViewModel.nearbyLocation?.let {
        Text(
            text = "Nearby: ${it.displayName}",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LocationItem(place: NominatimPlace, onSelectedLocation: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable { onSelectedLocation(place.displayName) }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.location),
            contentDescription = "Location",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Column {
            Text(text = place.displayName, color = Color.White, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    "Lat" to place.lat,
                    "Lon" to place.lon
                ).forEach { (type, value) ->
                    Text(
                        text = "$type: $value",
                        style = Typography.bodySmall.copy(
                            color = GrayTextColor,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }
        }
    }
}