package com.emc.moodmingle.ui.create.post.settings.activity

fun getActivitySuggestions(): List<ActivityItem> {
    return listOf(
        ActivityItem("Watching a movie", "🎬"),
        ActivityItem("Listening to music", "🎧"),
        ActivityItem("Eating", "🍽️"),
        ActivityItem("Drinking coffee", "☕"),
        ActivityItem("Working", "💻"),
        ActivityItem("Studying", "📚"),
        ActivityItem("Gaming", "🎮"),
        ActivityItem("Traveling", "✈️"),
        ActivityItem("Cooking", "👨‍🍳"),
        ActivityItem("Shopping", "🛍️"),
        ActivityItem("Exercising", "🏋️"),
        ActivityItem("Running", "🏃"),
        ActivityItem("Cycling", "🚴"),
        ActivityItem("Sleeping", "😴"),
        ActivityItem("Relaxing", "🛋️"),
        ActivityItem("Meditating", "🧘"),
        ActivityItem("Reading", "📖"),
        ActivityItem("Drawing", "🎨"),
        ActivityItem("Writing", "✍️"),
        ActivityItem("Photography", "📷"),
        ActivityItem("Hanging out with friends", "👥"),
        ActivityItem("At a party", "🎉"),
        ActivityItem("Watching TV", "📺"),
        ActivityItem("Learning something new", "🧠"),
        ActivityItem("Cleaning", "🧹"),
        ActivityItem("Playing with pets", "🐶"),
        ActivityItem("Driving", "🚗"),
        ActivityItem("At the gym", "🏋️‍♀️"),
        ActivityItem("At work", "🏢")
    )
}

data class ActivityItem(
    val label: String,
    val emoji: String
)