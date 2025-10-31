package com.emc.moodmingle.ui.post.data

fun randomComments(): List<String> {
    return listOf(
        "That looks awesome!",
        "I totally agree with you.",
        "Wow, I didn’t expect that!",
        "Nice work!",
        "This made my day 😊",
        "Can you tell me more about it?",
        "So cool! 🔥",
        "That’s interesting, I never thought of that.",
        "I really love this idea!",
        "This is super helpful, thanks!",
        "Hahaha this made me laugh 😂",
        "Looks great!",
        "I’ve been waiting for this!",
        "Absolutely stunning!",
        "I feel the same way.",
        "Such a nice post!",
        "Love this vibe!",
        "Thanks for sharing!",
        "This is gold!",
        "Can’t stop looking at this!"
    ).shuffled()
}