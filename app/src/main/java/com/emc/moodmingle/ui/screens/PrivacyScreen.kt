package com.emc.moodmingle.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.privacy.PrivacyTitle
import com.emc.moodmingle.ui.settings.privacy.account.AccountPrivacy
import com.emc.moodmingle.ui.settings.privacy.post.PostPrivacy
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.scaleOnPress
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val userEntityFirebase by userViewModelFirebase.loggedUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(PrimaryDark)
    ) {
        Header(onBack)

        userEntityFirebase?.let {
            Content(it)
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBack() },
            tint = Color.White
        )

        Text(
            text = "Privacy",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun Content(userEntityFirebase: UserEntityFirebase) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContentHeader()

        DrawNoPaddingLine(thickness = 0.5.dp)

        CreateContent(
            iconRes = R.drawable.account,
            title = "Account",
            composable = { AccountPrivacy(userEntityFirebase) }
        )

        CreateContent(
            iconRes = R.drawable.privacy_post,
            title = "Post",
            composable = { PostPrivacy(userEntityFirebase) }
        )
    }
}

@Composable
private fun ContentHeader() {
    Icon(
        painter = painterResource(R.drawable.privacy),
        contentDescription = "Security",
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )

    Text(
        text = "To protect your personal information and control what others can see, adjust your privacy settings below.",
        style = Typography.bodyLarge.copy(color = Color.White, textAlign = TextAlign.Center)
    )
}

@Composable
private fun CreateContent(
    @DrawableRes iconRes: Int,
    title: String,
    composable: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .background(color = SecondaryDark, shape = RoundedCornerShape(8.dp))
            .scaleOnPress()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrivacyTitle(iconRes, title)
            composable()
        }
    }
}
