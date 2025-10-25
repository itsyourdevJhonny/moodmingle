package com.emc.moodmingle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("") }

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
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterSetupTitle()

                RegisterSetupInputField(username, "username", onValueChange = { username = it })

                RegisterSetupInputField(password, "password", onValueChange = { password = it })

                Spacer(modifier = Modifier.height(12.dp))

                SetupAvatar(selectedAvatar, onSelectedAvatar = { selectedAvatar = it })

                Column {
                    RegisterSetupBio(bio, onValueChange = { bio = it })

                    Spacer(modifier = Modifier.height(15.dp))

                    RegisterSetupButton(
                        username,
                        password,
                        selectedAvatar,
                        bio,
                        onRegisterClick,
                        onLoginClick
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterSetupTitle() {
    Text(
        "Register",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            brush = Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
        ),
        textAlign = TextAlign.Center
    )
    Text(
        text = "Set up your profile to start sharing your moods",
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 7.dp, bottom = 20.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun RegisterSetupInputField(name: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = onValueChange,
        label = { Text("Create your $label") },
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
fun SetupAvatar(selectedAvatar: String, onSelectedAvatar: (String) -> Unit) {
    val avatars = listOf("😊", "😎", "😍", "🥳", "🌸", "🦋", "🎶", "🌈", "⚡", "🍀")

    Text(
        text = "Choose Your Avatar",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyMedium
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .height(120.dp)
            .padding(8.dp)
    ) {
        items(avatars) { avatar ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
                    .border(
                        width = if (selectedAvatar == avatar) 2.dp else 1.dp,
                        color = if (selectedAvatar == avatar) PurplePrimary else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectedAvatar(avatar) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = avatar, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun RegisterSetupBio(bio: String, onValueChange: (String) -> Unit) {
    Text(
        text = "Bio (Optional)",
        textAlign = TextAlign.Left,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    OutlinedTextField(
        value = bio,
        onValueChange = onValueChange,
        label = { Text("Tell others about yourself...") },
        modifier = Modifier.fillMaxWidth(),
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
fun RegisterSetupButton(
    name: String,
    password: String,
    selectedAvatar: String,
    bio: String,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
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

            onRegisterClick()
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
                text = "Register",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    HaveAccountSection(onLoginClick)
}

@Composable
fun HaveAccountSection(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White
            )
        )

        Text(
            modifier = Modifier.clickable { onLoginClick() },
            text = "Login",
            color = PurplePrimary,
            fontStyle = FontStyle.Italic
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen({}, {})
}
