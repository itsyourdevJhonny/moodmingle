package com.emc.moodmingle.ui.settings.password.recover

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.settings.password.utils.TopIcon
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(onSendCodeSuccess: (String) -> Unit) {
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()

    var email by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp),
            content = { TopIcon(imageVector = Icons.Default.Email, size = 88.dp) }
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your registered email to receive a verification code.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            EmailField(email, onValueChange = { email = it })

            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
                        Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .background(BrushPrimaryGradient, CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = "Continue",
                        tint = Color.White
                    )

                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "Send Code",
                        color = Color.White
                    )
                }
            }

            if (isLoading) {
                LoadingDialog("Sending code") {
                    scope.launch {
                        val result = firebaseUserViewModel.sendVerificationCode(email)
                        result.onSuccess { exist ->
                            if (exist) {
                                Toast.makeText(context, "Code sent to $email", Toast.LENGTH_SHORT)
                                    .show()
                                onSendCodeSuccess(email)
                            } else {
                                Toast.makeText(context, "Email does not exist", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            isLoading = false
                        }.onFailure {
                            Toast.makeText(
                                context,
                                "Failed to send code: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            isLoading = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmailField(email: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        value = email,
        onValueChange = { onValueChange(it) },
        label = { Text(text = "Email") },
        shape = CircleShape,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon",
                tint = Color.White,
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedLabelColor = Color.White,
            focusedTextColor = Color.White,
            focusedBorderColor = PurplePrimary
        )
    )
}
