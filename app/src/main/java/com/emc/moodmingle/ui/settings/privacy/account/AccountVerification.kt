package com.emc.moodmingle.ui.settings.privacy.account

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.dialogFullSizeProperties
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun AccountVerification(userEntity: UserEntityFirebase) {
    var isVerified by remember(userEntity) { mutableStateOf(userEntity.verified) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.verified),
                contentDescription = "Verified/Unverified",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient(),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Verification Status ",
                    style = Typography.bodyMedium
                )

                Text(
                    text = "(${if (isVerified) "Verified" else "Unverified"})",
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )
            }
        }
    }

    if (showDialog) {
        ShowVerificationDialog(onShowDialog = { showDialog = it })
    }
}

@Composable
private fun ShowVerificationDialog(onShowDialog: (Boolean) -> Unit) {
    Dialog(
        onDismissRequest = { onShowDialog(false) },
        properties = dialogFullSizeProperties()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
                .padding(vertical = 48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onShowDialog(false) }
                )

                Text(
                    text = "Account Verification",
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                VerificationIconAndDescription()
                FromText(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

@Composable
private fun VerificationIconAndDescription() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.customer_service),
            contentDescription = "Verification",
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = "To apply for account verification, please contact our customer service team and provide the required information for review.",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        ContactSupportButton()
    }
}

@Composable
private fun ContactSupportButton() {
    val context = LocalContext.current
    val contactTypes = listOf(
        Triple("support@moodmingel.com", Icons.Default.Email, "Email"),
        Triple("(+63) 0926 891 1259", Icons.Default.Phone, "Phone Number")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Contact Customer Service with:",
            style = Typography.bodyLarge.copy(color = Color.White)
        )

        contactTypes.forEach { contactType ->
            Box(modifier = Modifier.background(SecondaryDark)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = contactType.third,
                        style = Typography.bodyMedium.copy(color = GrayTextColor)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = contactType.first,
                            style = Typography.bodyMedium,
                        )

                        Box(
                            modifier = Modifier
                                .background(BrushPrimaryGradient, CircleShape)
                                .clickable {
                                    val contactIntent = if (contactType.third == "Email") {
                                        Intent(Intent.ACTION_SENDTO).apply {
                                            data = "mailto:".toUri()
                                            putExtra(
                                                Intent.EXTRA_EMAIL,
                                                arrayOf("support@moodmingle.com")
                                            )
                                            putExtra(
                                                Intent.EXTRA_SUBJECT,
                                                "Account Verification Request"
                                            )
                                        }
                                    } else {
                                        Intent(Intent.ACTION_DIAL).apply {
                                            data = "tel:+639268911259".toUri()
                                        }
                                    }

                                    context.startActivity(contactIntent)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = contactType.second,
                                    contentDescription = contactType.third,
                                    tint = Color.White
                                )

                                Text(
                                    text = "Contact",
                                    style = Typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FromText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "from",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "MoodMingle Development Team",
            style = Typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
            color = GrayTextColor
        )
    }
}
