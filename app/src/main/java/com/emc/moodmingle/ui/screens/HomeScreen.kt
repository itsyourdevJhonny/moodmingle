package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.local.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onChatClick: (String, String) -> Unit,
    onConversationClick: () -> Unit,
    onRemix: (String, String) -> Unit,
    onCreate: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopNavigationBar(onCreateClick, onSearchClick, onAvatarClick, onConversationClick)
        FeedScreen(onCreateClick, onProfileClick, onChatClick, onRemix, onCreate)
    }
}

@Composable
fun TopNavigationBar(
    onCreateClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onConversationClick: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userViewModel = hiltViewModel<UserViewModel>()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()
    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { currentUserUid = userDao.getLoggedUser()?.uid ?: "" }

    val currentUser by userViewModel.getUserByUid(currentUserUid).collectAsState(initial = null)
    val conversations by remember(currentUserUid) {
        conversationViewModel.getConversationsByUser(currentUserUid)
    }.collectAsState(initial = emptyList())

    val unreadConversations = conversations.filter { conversation -> !conversation.lastMessageRead }

    Box(
        modifier = Modifier
            .background(PrimaryDark)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MoodMingle",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                TopIcon(imageVector = Icons.Default.Add, label = "Add", onClick = onCreateClick)

                TopIcon(
                    imageVector = Icons.Default.Search,
                    label = "Search",
                    onClick = onSearchClick
                )

                Box {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrushPrimaryGradient)
                            .size(32.dp)
                            .clickable {
                                scope.launch {
                                    unreadConversations.forEach { conversation ->
                                        conversationViewModel.updateConversation(
                                            conversation = conversation.copy(lastMessageRead = true)
                                        )
                                    }
                                }

                                onConversationClick()
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chat),
                            contentDescription = "Chat",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }

                    if (unreadConversations.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 3.dp, y = (-5).dp)
                                .background(Color.Red, CircleShape)
                        ) {
                            Text(
                                text = "${unreadConversations.size}",
                                style = Typography.bodyMedium.copy(
                                    color = Color.White,
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                AsyncImage(
                    model = currentUser?.avatarUrl ?: "",
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .clickable { onAvatarClick() }
                )
            }
        }
    }
}

@Composable
fun TopIcon(imageVector: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(BrushPrimaryGradient)
            .size(32.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = label,
            tint = Color.White
        )
    }
}