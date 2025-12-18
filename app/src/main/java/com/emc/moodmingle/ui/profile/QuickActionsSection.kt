package com.emc.moodmingle.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QuickActionsSection(
    onCreateClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onInsightsClick: () -> Unit = {},
    onExploreClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        currentUserUid = userDao.getLoggedUser()?.uid ?: ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(brush = BrushPrimaryGradient, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CreateTitle()
            Spacer(Modifier.height(12.dp))
            CreateButton(Icons.Default.Add, "Share New Mood", onClick = onCreateClick)
            Spacer(Modifier.height(8.dp))
            CreateButton(Icons.Default.Menu, "View Insights", onClick = onInsightsClick)
            Spacer(Modifier.height(8.dp))
            CreateButton(Icons.Default.Home, "Explore Community", onClick = onExploreClick)
            Spacer(Modifier.height(8.dp))
            CreateButton(
                Icons.AutoMirrored.Filled.ExitToApp,
                "Logout",
                onClick = { showLogoutDialog = true }
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.clearUser(currentUserUid)

                        withContext(Dispatchers.Main) {
                            FirebaseAuth.getInstance().signOut()
                            onLogoutClick()
                        }
                    }
//                    onLogoutClick()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreateTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings Icon",
            tint = Color.White
        )
        Text(text = "Quick Actions", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CreateButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.White),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Icon(icon, contentDescription = "$text Icon", tint = Color.White)
            Text(text = text, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuickActionsSection() {
    QuickActionsSection()
}