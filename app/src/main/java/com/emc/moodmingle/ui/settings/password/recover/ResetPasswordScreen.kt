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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.settings.password.PasswordChangeSuccessScreen
import com.emc.moodmingle.ui.settings.password.utils.PasswordField
import com.emc.moodmingle.ui.settings.password.utils.TopIcon
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(onContinue: () -> Unit, onCancel: () -> Unit) {
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isPasswordChanged by remember { mutableStateOf(false) }

    if (isPasswordChanged) {
        PasswordChangeSuccessScreen(onContinue = onContinue)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp),
                content = { TopIcon(imageVector = Icons.Default.Check, size = 88.dp) }
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Description()

                PasswordField(
                    label = "New Password",
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    modifier = Modifier.padding(top = 16.dp)
                )

                PasswordField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.padding(top = 16.dp)
                )

                ResetButton(
                    newPassword,
                    confirmPassword,
                    isLoading,
                    onResetting = { isLoading = it }
                )

                CancelButton(onCancel)
            }

            if (isLoading) {
                LoadingDialog("Resetting password") {
                    scope.launch {
                        val result = firebaseUserViewModel.resetPassword(newPassword)
                        result.onSuccess {
                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                            isPasswordChanged = true
                            isLoading = false
                        }.onFailure {
                            isLoading = false
                            Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT)
                                .show()
                            return@launch
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Description() {
    Text(
        text = "Create a strong new password to keep your account secure.\n" +
                "Your new password must be different from your current one.",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun ResetButton(
    newPassword: String,
    confirmPassword: String,
    isResetting: Boolean,
    onResetting: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (newPassword.isBlank() || confirmPassword.isBlank()) {
                Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@Button
            }
            if (newPassword != confirmPassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@Button
            }

            onResetting(true)
        },
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(BrushPrimaryGradient, CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        if (isResetting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Continue",
                tint = Color.White
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Reset Password",
                color = Color.White
            )
        }
    }
}

@Composable
fun CancelButton(onCancel: () -> Unit) {
    TextButton(
        onClick = onCancel,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cancel Icon",
            tint = Color.Red
        )

        Text(text = "Cancel", color = Color.White, modifier = Modifier.padding(start = 8.dp))
    }
}
