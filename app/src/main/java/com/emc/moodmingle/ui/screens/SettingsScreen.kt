package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.saved.SaveViewModelFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

var globalOnClick: (String) -> Unit = {}

@Composable
fun SettingsScreen(onBackClick: () -> Unit, onClick: (String) -> Unit) {
    globalOnClick = onClick
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(onBackClick)
            MainContent()
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            LogoutButton(onShowLogoutDialog = { showLogoutDialog = it })
        }

        if (showLogoutDialog) {
            ShowLogoutDialog(onShowLogoutDialog = { showLogoutDialog = it })
        }
    }
}

@Composable
private fun Header(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "Settings",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun LogoutButton(onShowLogoutDialog: (Boolean) -> Unit) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val bounceOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), bounceOffsetY.roundToInt()) }
            .background(BrushPrimaryGradient, CircleShape)
            .padding(12.dp)
            .combinedClickable(
                onClick = {
                    onShowLogoutDialog(true)
                },
                onLongClick = {
                    offsetX = 0f
                    offsetY = 0f
                }
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.logout),
            contentDescription = "Logout",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ShowLogoutDialog(onShowLogoutDialog: (Boolean) -> Unit) {
    val context = LocalContext.current
    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userDao.getLoggedUser()?.uid?.let {
            currentUserUid = it
        }
    }
    AlertDialog(
        onDismissRequest = { onShowLogoutDialog(false) },
        containerColor = PrimaryDark,
        modifier = Modifier
            .border(
                width = 0.5.dp,
                brush = BrushPrimaryGradient,
                shape = RectangleShape
            ),
        shape = RectangleShape,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    contentDescription = "Logout",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text("Confirm Logout")
            }
        },
        text = {
            Text(
                text = "Are you sure you want to log out?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onShowLogoutDialog(false) },
                    modifier = Modifier
                        .width(120.dp)
                        .background(SecondaryDark, CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)

                ) {
                    Text(text = "Cancel", color = Color.White)
                }

                Button(
                    onClick = {
                        onShowLogoutDialog(false)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = AppDatabase.getDatabase(context).userDao()
                            dao.clearUser(currentUserUid)

                            withContext(Dispatchers.Main) {
                                FirebaseAuth.getInstance().signOut()
                                globalOnClick("Logout")
                            }
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .background(BrushPrimaryGradient, CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "Yes", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun MainContent() {
    val listState = rememberLazyListState()
    val actions = listOf(
        Pair(
            "Privacy & Security",
            Pair(
                Triple(
                    "Privacy",
                    R.drawable.privacy,
                    "Manage who can see your profile, posts, and activity across the app."
                ),
                Triple(
                    "Security",
                    R.drawable.security,
                    "Control your login, authentication, and protection settings to keep your account safe."
                )
            )
        ),
        Pair(
            "Saved & Favorite Posts",
            Pair(
                Triple(
                    "Saved",
                    R.drawable.save_post,
                    "View the collection of posts you've saved for easy access later."
                ),
                Triple(
                    "Favorites",
                    R.drawable.favorites,
                    "See the posts you've marked as favorites to quickly revisit the content you like most."
                )
            )
        ),
        Pair(
            "Encryption & Decryption",
            Pair(
                Triple(
                    "Encryption",
                    R.drawable.encryption,
                    "Convert sensitive data into a protected format to prevent unauthorized access."
                ),
                Triple(
                    "Decryption",
                    R.drawable.decryption,
                    "Restore encrypted data back to its original readable form when proper authorization is provided."
                )
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = listState
    ) {
        item { Spacer(Modifier.height(1.dp)) }

        item { SettingsTitle("Personal") }

        item { PersonalInformation() }

        items(actions) { (title, pair) ->
            ActionCards(
                actions = Pair(
                    Triple(
                        pair.first.first,
                        painterResource(pair.first.second),
                        pair.first.third
                    ),
                    Triple(
                        pair.second.first,
                        painterResource(pair.second.second),
                        pair.second.third
                    )
                ),
                title = title
            )
        }

        item { DrawUserNoPaddingLine(thickness = 0.5.dp) }

        item { SettingsTitle("Preferences") }

        item { Preferences() }
    }
}

@Composable
private fun SettingsTitle(text: String) {
    Text(
        text = text,
        style = Typography.titleLarge.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun PersonalInformation() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PersonalInformationButton("Personal", R.drawable.personal_colored)
        PersonalInformationButton("Password", R.drawable.password_colored)
        PersonalInformationButton("Insights", R.drawable.analytics)
    }
}

@Composable
fun PersonalInformationButton(text: String, @DrawableRes iconRes: Int) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .clickable { globalOnClick(text.lowercase()) }
    ) {
        Column(
            modifier = Modifier
                .height(60.dp)
                .width(80.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(model = iconRes, contentDescription = text, modifier = Modifier.size(38.dp))
        }
    }
}

@Composable
fun ActionCards(
    actions: Pair<Triple<String, Painter, String>, Triple<String, Painter, String>>,
    title: String
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ExpandableTitle(title, expanded, onExpanded = { expanded = it })

        if (expanded) {
            ActionButton(actions.first.second, actions.first.first, actions.first.third)
            ActionButton(actions.second.second, actions.second.first, actions.second.third)
        }
    }
}

@Composable
private fun ExpandableTitle(title: String, expanded: Boolean, onExpanded: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpanded(!expanded) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Arrow Right",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
    }
}

@Composable
fun ActionButton(icon: Painter, label: String, description: String) {
    var savedCount by remember { mutableIntStateOf(0) }
    var favoritesCount by remember { mutableIntStateOf(0) }

    if (label == "Saved" || label == "Favorites") {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val saveViewModelFirebase = hiltViewModel<SaveViewModelFirebase>()
        val favoritesViewModelFirebase = hiltViewModel<FavoritesViewModelFirebase>()

        val userDao = AppDatabase.getDatabase(context).userDao()
        var userId by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            userId = userDao.getLoggedUser()?.uid ?: ""
        }

        val saved by remember(userId) {
            saveViewModelFirebase.getSavedByUser(userId)
                .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        }.collectAsState(initial = emptyList())

        val favorites by remember(userId) {
            favoritesViewModelFirebase.getFavoritesByUser(userId)
                .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
        }.collectAsState(initial = emptyList())

        savedCount = saved.size
        favoritesCount = favorites.size
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .clickable { globalOnClick(label.lowercase()) }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(BrushPrimaryGradient, CircleShape)
                    .size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = label, style = Typography.bodyMedium.copy(color = Color.White))

                    if (label == "Saved" || label == "Favorites") {
                        val count = if (label == "Saved") savedCount else favoritesCount
                        val suffix = if (count > 1) "Items" else "Item"

                        Text(
                            text = "$count $suffix",
                            style = Typography.bodyMedium.copy(
                                color = GrayTextColor,
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text(
                    text = description,
                    style = Typography.bodySmall.copy(
                        color = GrayTextColor,
                        fontStyle = FontStyle.Italic
                    ),
                )
            }
        }
    }
}

@SuppressLint("FrequentlyChangingValue")
@Composable
fun Preferences() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        ExpandableTitle("Images/Videos/Audios", expanded, onExpanded = {
            expanded = it
        })
        if (expanded) {
            Images()
            Videos()
            Audios()
        }
    }
}

@Composable
private fun Videos() {
    var isAutoPlay by remember { mutableStateOf(false) }
    var isMute by remember { mutableStateOf(false) }
    var isLoop by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DrawNoPaddingLine(thickness = 0.5.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.video),
                contentDescription = "Video",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            Text(
                text = "Video", color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SwitchButtonSettings(
                label = "Auto Play",
                isChecked = isAutoPlay,
                onCheckedChange = { isAutoPlay = it }
            )

            SwitchButtonSettings(
                label = "Auto Mute",
                isChecked = isMute,
                onCheckedChange = { isMute = it }
            )

            SwitchButtonSettings(
                label = "Loop",
                isChecked = isLoop,
                onCheckedChange = { isLoop = it }
            )

            VideoQuality()
        }
    }
}

@Composable
private fun VideoQuality() {
    val videoQualities = listOf("Auto", "1080dp", "720dp", "480dp", "360dp")
    var selectedQuality by remember { mutableStateOf("Auto") }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Video Quality", style = Typography.bodyMedium)

        Box(modifier = Modifier.clickable { expanded = !expanded }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedQuality,
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Arrow Down",
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                videoQualities.forEach { videoQuality ->
                    DropdownMenuItem(
                        text = { Text(text = videoQuality) },
                        onClick = {
                            selectedQuality = videoQuality
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Audios() {
    var isAutoPlay by remember { mutableStateOf(false) }
    var isLoop by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DrawNoPaddingLine(thickness = 0.5.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.audio),
                contentDescription = "Audio",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            Text(text = "Audio", color = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SwitchButtonSettings(
                label = "Auto Play",
                isChecked = isAutoPlay,
                onCheckedChange = { isLoop = it })
            SwitchButtonSettings(
                label = "Loop",
                isChecked = isLoop,
                onCheckedChange = { isLoop = it })
            AudioVolume()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioVolume() {
    var volume by rememberSaveable { mutableFloatStateOf(0f) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${volume.toInt()}%", style = Typography.bodyMedium.copy(color = Color.White)
            )

            Icon(
                painter = painterResource(if (volume <= 0f) R.drawable.pause_sound else R.drawable.play_sound),
                contentDescription = "Volume",
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            value = volume,
            valueRange = 0f..100f,
            onValueChange = { volume = it },
            thumb = {
                Icon(
                    painter = painterResource(R.drawable.volume),
                    contentDescription = "Sound",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    modifier = Modifier.fillMaxWidth(),
                    sliderState = sliderState,
                    thumbTrackGapSize = 0.dp,
                    colors = SliderDefaults.colors(
                        activeTrackColor = PurpleDark,
                        inactiveTrackColor = PurplePrimary
                    ),
                    trackInsideCornerSize = 0.dp
                )
            }
        )
    }
}

@Composable
private fun Images() {
    var savable by remember { mutableStateOf(false) }
    var guarded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DrawNoPaddingLine(thickness = 0.5.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.image),
                contentDescription = "Image",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            Text(
                text = "Image", color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SwitchButtonSettings(
                label = "Savable",
                isChecked = savable,
                onCheckedChange = { savable = it }
            )

            SwitchButtonSettings(
                label = "Guarded",
                isChecked = guarded,
                onCheckedChange = { guarded = it }
            )
        }
    }
}

@Composable
private fun SwitchButtonSettings(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)

        Box(
            modifier = Modifier
                .width(46.dp)
                .height(24.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = GrayTextColor,
                    shape = CircleShape
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isChecked) listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                        else listOf(Color.Gray, Color.LightGray)
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .width(50.dp)
                    .height(24.dp)
                    .align(
                        if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
                    ),
                thumbContent = {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        drawCircle(
                            brush = if (isChecked) Brush.linearGradient(
                                listOf(
                                    Color.White,
                                    Color.White
                                )
                            ) else BrushPrimaryGradient,
                            radius = size.minDimension / 2
                        )
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Transparent,
                    uncheckedThumbColor = Color.Transparent,
                    checkedTrackColor = Color.Transparent,
                    uncheckedTrackColor = PrimaryDark,
                    uncheckedBorderColor = GrayTextColor
                )
            )
        }
    }
}