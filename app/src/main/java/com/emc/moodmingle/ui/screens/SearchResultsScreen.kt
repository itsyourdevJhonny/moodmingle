package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.search.SearchEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.model.search.SearchEntity
import com.emc.moodmingle.ui.post.action.formatText
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.local.PostViewModel
import com.emc.moodmingle.viewmodel.local.SearchViewModel
import kotlinx.coroutines.flow.first

@Composable
fun SearchResultsScreen(
    searchResults: List<SearchEntityFirebase>,
    onBackClick: () -> Unit,
    onViewClick: (String) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 30.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopIconButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                description = "Back"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search Results",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Found ${searchResults.size} related search ${if (searchResults.size > 1) "results" else "result"}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = GrayTextColor
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (searchResults.isEmpty()) {
            NoSearchResult()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(searchResults, key = { it.userUid }) { result ->
                    var user by remember { mutableStateOf<UserEntityFirebase?>(null) }

                    LaunchedEffect(result.userUid) {
                        user = userViewModel.getUserByUid(result.userUid).first().getOrNull()
                    }

                    user?.let {
                        ProfileCard(it, onViewClick)
                    }
                }
            }
        }
    }
}

@Composable
fun NoSearchResult() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.no_search),
                contentDescription = "No Result",
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .size(60.dp)
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
                text = "No results found.\nTry adjusting your search.",
                color = GrayTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileCard(userEntity: UserEntityFirebase, onViewClick: (String) -> Unit) {
    val postViewModel = hiltViewModel<PostViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()

    val posts by postViewModel.post.getPostsByUserId(userEntity.uid)
        .collectAsState(initial = emptyList())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryDark)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userEntity.avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = formatText(userEntity.username, 25))

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
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
                thickness = 1.dp,
                color = Color.White
            )

            Text(
                text = "${posts.size} total ${if (posts.size > 1) "posts" else "post"}",
                style = MaterialTheme.typography.bodySmall.copy(color = GrayTextColor)
            )

            Button(
                onClick = {
                    searchViewModel.insertSearch(
                        SearchEntity(
                            userUid = userEntity.uid,
                            time = System.currentTimeMillis()
                        )
                    )

                    onViewClick(userEntity.uid)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(BrushPrimaryGradient, CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    painter = painterResource(R.drawable.view),
                    contentDescription = "View",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp)
                )

                Text(text = "View Profile", color = Color.White)
            }
        }
    }
}
