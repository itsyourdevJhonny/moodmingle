package com.emc.moodmingle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onCreateProfile: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("TestUsername") }
    var password by remember { mutableStateOf("test123") }
    var bio by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("😊") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(PurplePrimary, PurpleDark))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(SecondaryDark, shape = RoundedCornerShape(16.dp))
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SetupTitle()

                SetupInputField(username, "username", onValueChange = { username = it })

                SetupInputField(password, "password", onValueChange = { password = it })

                Spacer(modifier = Modifier.height(12.dp))

                SetupButton(
                    username,
                    password,
                    selectedAvatar,
                    bio,
                    onCreateProfile,
                    onRegisterClick
                )
            }
        }
    }
}

@Composable
fun SetupTitle() {
    Text(
        text = "Login",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
            )
        )
    )
    Text(
        text = "Login to your account to start sharing your moods",
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 7.dp, bottom = 20.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun SetupInputField(name: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = onValueChange,
        label = { Text("Enter your $label") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        )
    )
}

@Composable
fun SetupButton(
    name: String,
    password: String,
    selectedAvatar: String,
    bio: String,
    onCreateProfile: (String, String, String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (name.isBlank() || selectedAvatar.isBlank() || password.isBlank()) {
                Toast.makeText(
                    context,
                    "Please complete the required information.",
                    Toast.LENGTH_LONG
                ).show()
                return@Button
            }

            onCreateProfile(name, password, selectedAvatar, bio)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                    ),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Login",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    NoAccountSection(onRegisterClick)
}

@Composable
fun NoAccountSection(onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Don't have an account yet?",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White
            )
        )

        Text(
            modifier = Modifier.clickable { onRegisterClick() },
            text = "Register",
            color = PurplePrimary,
            fontStyle = FontStyle.Italic
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen {}
}
