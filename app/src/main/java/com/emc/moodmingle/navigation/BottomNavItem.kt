package com.emc.moodmingle.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home_tab", "Home", Icons.Filled.Home),
    BottomNavItem("profile_tab", "Profile", Icons.Filled.Person),
    BottomNavItem("settings_tab", "Settings", Icons.Filled.Settings)
)