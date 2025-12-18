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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun SecurityScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(PrimaryDark)
    ) {
        Header(onBack)
        Content()
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
            text = "Security",
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContentHeader()

        DrawNoPaddingLine(thickness = 0.5.dp)

        SecurityStatus()

        DrawNoPaddingLine(thickness = 0.5.dp, modifier = Modifier.padding(bottom = 8.dp))

        CreateContent(
            R.drawable.recovery_email,
            "Add Recovery Email",
            "You can add up to 3 recovery email addresses."
        )

        CreateContent(
            R.drawable.two_factor_authentication,
            "Two Factor Authentication",
            "Setup two factor authentication to prevent unauthorized access to your account."
        )

        CreateContent(
            R.drawable.password_code,
            "Password Code",
            "Setup password code to keep your password secured."
        )

        CreateContent(
            R.drawable.fingerprint,
            "Fingerprint Login",
            "Login to your account with your fingerprint instead of your email and password."
        )
    }
}

@Composable
private fun ContentHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.security),
            contentDescription = "Security",
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = "To make your account more secured or prevent hacking issues, setup at least two security features below.",
            style = Typography.bodyLarge.copy(color = Color.White, textAlign = TextAlign.Center)
        )
    }
}

@Composable
private fun SecurityStatus() {
    val status = "Unsecured"

    Column(
        modifier = Modifier
            .background(SecondaryDark)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.security_status),
                contentDescription = "Security Status",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            Text(
                text = "Security Status (50%) ",
                style = Typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Box(modifier = Modifier.background(color = TertiaryDark, shape = CircleShape)) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (status == "Secured") Icons.Default.Check else Icons.Default.Close,
                        contentDescription = "Status",
                        tint = if (status == "Secured") Color.Green else Color.Red
                    )
                    Text(
                        text = status,
                        color = Color.White,
                        style = Typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateContent(@DrawableRes iconRes: Int, label: String, description: String) {
    Box(
        modifier = Modifier
            .clickable {}
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = label,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = description,
                style = Typography.bodyMedium.copy(color = GrayTextColor/*, fontStyle = FontStyle.Italic*/)
            )
        }
    }
}