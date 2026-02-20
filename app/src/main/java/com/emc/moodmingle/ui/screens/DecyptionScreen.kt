package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun DecryptionScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(PrimaryDark),
        content = {
            Header(onBack)
            Content()
        }
    )
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
            text = "Decryption",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun Content() {
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val userEntity by userViewModelFirebase.loggedUser
    val state = rememberScrollState()

    val encryptedDataList = userEntity?.let { user ->
        listOf(
            user.emailEncrypt to "Email",
            user.usernameEncrypt to "Username",
            user.passwordEncrypt to "Password",
            user.bioEncrypt to "Bio",
            user.avatarEncrypt to "Avatar",
            user.hashtagEncrypt to "Hashtag",
            user.captionEncrypt to "Caption",
            user.descriptionEncrypt to "Description",
            user.moodTextEncrypt to "Mood Text",
            user.moodEmojiEncrypt to "Mood Emoji"
        )
    }

    val filteredDataList = encryptedDataList
        ?.map { (map, label) ->
            val filteredMap = map.filter { (key, _) -> key != "value" && key.isNotEmpty() }
            filteredMap to label
        }
        ?.filter { (map, _) -> map.isNotEmpty() }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 42.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = "Found ${filteredDataList?.size} encrypted data",
            style = Typography.bodyLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 540.dp)
                .background(SecondaryDark, RoundedCornerShape(8.dp))
                .verticalScroll(state)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                filteredDataList?.forEach { filteredData ->
                    val encryptedData = filteredData.first.keys.first()
                    val label = filteredData.second

                    Column {
                        Text(
                            text = label,
                            style = Typography.bodyLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            ),
                        )

                        Text(
                            text = encryptedData,
                            style = Typography.bodyMedium.copy(
                                color = GrayTextColor,
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }
                }
            }
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .background(BrushPrimaryGradient, CircleShape)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                painter = painterResource(R.drawable.decryption),
                contentDescription = "Decrypt",
                modifier = Modifier.size(24.dp)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Decrypt All",
                fontWeight = FontWeight.W900
            )
        }
    }
}