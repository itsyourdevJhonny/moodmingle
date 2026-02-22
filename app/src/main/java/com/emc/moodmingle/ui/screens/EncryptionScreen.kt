package com.emc.moodmingle.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.encryption.DisplayEncryptedContent
import com.emc.moodmingle.ui.settings.encryption.getInformationTypes
import com.emc.moodmingle.ui.settings.encryption.updateDataEncryption
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.SwitchButton
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.text.encryptData
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun EncryptionScreen(onBack: () -> Unit) {
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
            text = "Encryption",
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
    val scope = rememberCoroutineScope()
    val state = rememberScrollState()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val userEntity by userViewModelFirebase.loggedUser
    val informationTypes = getInformationTypes(userEntity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 42.dp)
    ) {
        Description()

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state)
        ) {
            informationTypes?.forEach { informationType ->
                val title = informationType.second

                InformationTitle(title, informationType.third)

                informationType.first.forEach { information ->
                    val label = information.first
                    val originalValue = information.second
                    val (encryptedValue, encrypted) = information.third.entries.first()

                    var isEncrypted by remember(userEntity) {
                        mutableStateOf(encrypted)
                    }

                    var currentEncryptedValue by remember(userEntity) {
                        mutableStateOf(encryptedValue)
                    }

                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (originalValue.isNotEmpty()) {
                            SwitchButton(
                                label = "$label (${if (isEncrypted) "Encrypted" else "Unencrypted"})",
                                isChecked = isEncrypted,
                                onCheckedChange = {
                                    val newEncryptedValue = if (!isEncrypted) {
                                        encryptData(information.second)
                                    } else {
                                        "value"
                                    }

                                    updateDataEncryption(
                                        scope,
                                        userViewModelFirebase,
                                        label,
                                        userEntity!!,
                                        newEncryptedValue,
                                        isEncrypted,
                                        title,
                                        state
                                    )
                                }
                            )

                            if (isEncrypted) {
                                DisplayEncryptedContent(
                                    title,
                                    originalValue,
                                    currentEncryptedValue,
                                    encryptedValue,
                                )
                            }
                        }
                    }
                }

                if (title == "Personal Information") {
                    DrawNoPaddingLine(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Description() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Encrypt your data",
            style = Typography.bodyLarge.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "By encrypting your data, it will be converted into non-readable format (Hash Code).",
            style = Typography.bodySmall.copy(
                color = GrayTextColor,
                textAlign = TextAlign.Center
            )
        )

        DrawNoPaddingLine(thickness = 0.5.dp)
    }
}

@Composable
private fun InformationTitle(title: String, @DrawableRes iconRes: Int) {
    Row(
        modifier = Modifier.padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = title,
            style = Typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        )
    }
}