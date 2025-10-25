package com.emc.moodmingle.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home/{username}&{password}&{selectedAvatar}&{bio}")
    object CreatePost : Routes("create_post")
    object Insights : Routes("insights")
    object Search : Routes("search")

    object BottomHome : Routes("home_tab")
    object BottomProfile : Routes("profile_tab")
    object BottomSettings : Routes("settings_tab")
}