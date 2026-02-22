package com.emc.moodmingle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.viewmodel.remote.UserViewModelFirebase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrushPrimaryGradient),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(SecondaryDark, shape = RoundedCornerShape(16.dp))
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginHeader()

                LoginInputField(
                    value = email,
                    label = "Email",
                    onValueChange = { email = it },
                    type = "EMAIL"
                )

                LoginInputField(
                    value = password,
                    label = "Password",
                    onValueChange = { password = it },
                    type = "PASSWORD"
                )

                Spacer(modifier = Modifier.height(12.dp))

                LoginButton(email, password, onLogin, onRegisterClick)
            }
        }
    }
}

@Composable
fun LoginHeader() {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo",
        modifier = Modifier
            .size(100.dp)
            .wrapContentSize()
            .scale(2f),
    )

    Text(
        text = "Login to your account to start sharing your moods",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun LoginInputField(value: String, label: String, onValueChange: (String) -> Unit, type: String) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = CircleShape,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        ),
        visualTransformation = if (type == "PASSWORD") {
            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (type == "PASSWORD" && value.trim().isNotEmpty()) {
                val iconRes = if (isPasswordVisible)
                    R.drawable.visibility_on
                else
                    R.drawable.visibility_off

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(iconRes),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White
                    )
                }
            }
        },
        leadingIcon = {
            val imageVector = if (type == "PASSWORD") Icons.Default.Lock else Icons.Default.Email
            Icon(
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = BrushPrimaryGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    },
                imageVector = imageVector,
                contentDescription = type
            )
        }
    )
}

@Composable
fun LoginButton(email: String, password: String, onLogin: () -> Unit, onRegisterClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<UserViewModelFirebase>()
    var isLoading by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (email.isBlank()) {
                Toast.makeText(context, "Enter your email", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (password.isBlank()) {
                Toast.makeText(context, "Enter your password", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (!email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
                Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show()
                return@Button
            }

            isLoading = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = BrushPrimaryGradient, shape = CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Register",
                tint = Color.White
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Login",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    NoAccountSection(onRegisterClick)

    if (isLoading) {
        LoadingDialog("Logging in", onLoading = { isLoading = it }) {
            scope.launch {
                try {
                    userViewModelFirebase.login(
                        email.trim(),
                        password.trim(),
                        onSuccess = {
                            isLoading = false

                            onLogin()
                        },
                        onError = { message ->
                            isLoading = false
                            Toast.makeText(context, "Login failed: $message", Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
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