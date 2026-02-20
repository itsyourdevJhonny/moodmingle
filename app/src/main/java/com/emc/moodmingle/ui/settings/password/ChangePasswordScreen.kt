package com.emc.moodmingle.ui.settings.password

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(onBackClick: () -> Unit, onContinue: () -> Unit) {
    var isPasswordChanged by remember { mutableStateOf(false) }

    if (isPasswordChanged) {
        PasswordChangeSuccessScreen(onContinue = onContinue)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 26.dp)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderButton(onBackClick)
            Content(onPasswordChanged = { isPasswordChanged = true })
        }
    }
}

@Composable
private fun HeaderButton(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = BrushPrimaryGradient),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "Change Password",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun Content(onPasswordChanged: () -> Unit) {
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 42.dp),
            content = { TopIcon() }
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Description()

            PasswordField(
                label = "Current password",
                value = currentPassword,
                onValueChange = { currentPassword = it }
            )

            PasswordField(
                label = "New password",
                value = newPassword,
                onValueChange = { newPassword = it }
            )

            PasswordField(
                label = "Confirm password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it }
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT)
                                .show()
                            return@launch
                        }

                        isLoading = true
                        val verifyResult =
                            firebaseUserViewModel.verifyCurrentPassword(currentPassword)

                        if (verifyResult.isSuccess) {
                            val updateResult = firebaseUserViewModel.updatePassword(newPassword)
                            isLoading = false
                            if (updateResult.isSuccess) {
                                onPasswordChanged()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to update password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Incorrect current password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .background(BrushPrimaryGradient, CircleShape)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(R.drawable.change),
                        contentDescription = "Continue",
                        tint = Color.White
                    )
                }

                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = if (isLoading) "Changing password..." else "Change Password",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PasswordChangeSuccessScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Password Updated!",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Your password has been changed successfully. You can now use your new password to log in next time.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Button(
            onClick = { onContinue() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .padding(top = 24.dp)
                .background(BrushPrimaryGradient, CircleShape)
        ) {
            Text("Continue", color = Color.White)
        }
    }
}

@Composable
private fun TopIcon() {
    Icon(
        painter = painterResource(R.drawable.change),
        contentDescription = "Password",
        modifier = Modifier
            .size(88.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )
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
private fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        label = { Text(label) },
        shape = CircleShape,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
                tint = Color.White,
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
                    }
            )
        },
        trailingIcon = {
            if (value.trim().isNotEmpty()) {
                val iconRes = if (passwordVisible)
                    R.drawable.visibility_on
                else
                    R.drawable.visibility_off

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(iconRes),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.White
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary
        )
    )
}