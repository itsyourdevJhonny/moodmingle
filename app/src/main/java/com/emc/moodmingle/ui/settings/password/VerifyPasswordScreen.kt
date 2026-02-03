package com.emc.moodmingle.ui.settings.password

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun VerifyPasswordScreen(
    onBackClick: () -> Unit,
    onVerified: () -> Unit,
    onRecover: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 26.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = BrushPrimaryGradient),
        ) {
            BackButton(onBackClick)
            VerifyPasswordText()
        }

        VerifyPasswordMainContent(onVerified, onRecover)
    }
}

@Composable
private fun BackButton(onBackClick: () -> Unit) {
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Back Icon",
            tint = Color.White
        )
    }
}

@Composable
private fun VerifyPasswordText() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        text = "Verify Password",
        style = MaterialTheme.typography.titleMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun VerifyPasswordMainContent(onVerified: () -> Unit, onRecover: () -> Unit) {
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()

    var value by rememberSaveable { mutableStateOf("") }
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
                .padding(top = 70.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.password),
                contentDescription = "Password",
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Description()
            PasswordInputField(value, onValueChange = { value = it })
            ContinueButton(value, isLoading, onLoading = { isLoading = it })
            ForgotPasswordText()
            RecoverButton(onRecover)

            if (isLoading) {
                LoadingDialog("Confirming") {
                    scope.launch {
                        val result = firebaseUserViewModel.verifyCurrentPassword(value)

                        if (result.isSuccess) {
                            isLoading = false
                            Toast.makeText(context, "Password confirmed", Toast.LENGTH_SHORT).show()
                            onVerified()
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
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
        text = "To make sure it's really you, please enter your current password before viewing or changing to a new one.",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun PasswordInputField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = "Current password") },
        shape = CircleShape,
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
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

@Composable
private fun ContinueButton(value: String, isLoading: Boolean, onLoading: (Boolean) -> Unit) {
    val context = LocalContext.current
    Button(
        onClick = {
            if (value.isBlank()) {
                Toast.makeText(
                    context,
                    "Please enter your current password",
                    Toast.LENGTH_SHORT
                ).show()
                return@Button
            }

            onLoading(true)
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(BrushPrimaryGradient, CircleShape)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Continue",
                tint = Color.White
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Continue",
                color = Color.White
            )
        }
    }
}

@Composable
private fun ForgotPasswordText() {
    Text(
        modifier = Modifier.padding(top = 24.dp),
        text = "Forgot Password?",
        style = MaterialTheme.typography.titleSmall.copy(
            color = Color.White,
            fontStyle = FontStyle.Italic
        )
    )
}

@Composable
private fun RecoverButton(onRecover: () -> Unit) {
    TextButton(
        onClick = onRecover,
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    ) {
        Text(
            text = "Recover here",
            style = MaterialTheme.typography.titleMedium.copy()
        )
    }
}