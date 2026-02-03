package com.emc.moodmingle.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Primary Colors
val PurplePrimary = Color(0xFF8E2DE2)
val PurpleDark = Color(0xFF4A00E0)
val GrayTextColor = Color(0xFFBCBBBF)

val PrimaryDark = Color(0xFF121212)
val SecondaryDark = Color(0xFF1E1E1E)
val TertiaryDark = Color(0xFF313030)

// Gradient Colors
val PrimaryGradient = listOf(PurplePrimary, PurpleDark)
val BrushPrimaryGradient = Brush.linearGradient(PrimaryGradient)
val BrushSecondaryTertiaryGradient =
    Brush.linearGradient(colors = listOf(SecondaryDark, TertiaryDark))

val BrushSecondaryDarkGradient =
    Brush.linearGradient(colors = listOf(SecondaryDark, SecondaryDark))

val BrushGrayGradient =
    Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f)))

// Reaction Color
val HeartColor = Color(0xFFD50000)

val VerifiedColor = Color(0xFF21DFFD)

val HashtagTextColor = Color(0xFFFFB74D)
val EmailTextColor = Color(0xFFBA68C8)
val PhoneTextColor = Color(0xFFE57373)
val UrlTextColor = Color(0xFF64B5F6)
val UrlBackgroundColor = UrlTextColor.copy(alpha = 0.3f)

val MentionBackground = Color(0x330D99E3)
val MentionTextColor = Color(0xFF2196F3)

val TagTextColor = Color(0xFF7C4DFF)
val TagBackground = Color(0x331B74E4)
